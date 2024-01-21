package com.sephora.ecommerce.services.impl;

import com.sephora.ecommerce.entities.*;
import com.sephora.ecommerce.exceptions.APIException;
import com.sephora.ecommerce.exceptions.ResourceNotFoundException;
import com.sephora.ecommerce.payloads.OrderDTO;
import com.sephora.ecommerce.payloads.OrderItemDTO;
import com.sephora.ecommerce.payloads.OrderResponse;
import com.sephora.ecommerce.payloads.UserResponse;
import com.sephora.ecommerce.repositories.*;
import com.sephora.ecommerce.services.CartService;
import com.sephora.ecommerce.services.OrderService;
import com.sephora.ecommerce.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private UserRepository userRepo;
    private CartRepository cartRepo;
    private OrderRepo orderRepo;
    private PaymentRepo paymentRepo;
    private CartItemRepository cartItemRepo;
    private  OrderItemRepo orderItemRepo;
    private UserService userService;
    private CartService cartService;
    private ModelMapper modelMapper;

    public OrderServiceImpl(UserRepository userRepo, CartRepository cartRepo, OrderRepo orderRepo, PaymentRepo paymentRepo, CartItemRepository cartItemRepo, OrderItemRepo orderItemRepo, UserService userService, CartService cartService, ModelMapper modelMapper) {
        this.userRepo = userRepo;
        this.cartRepo = cartRepo;
        this.orderRepo = orderRepo;
        this.paymentRepo = paymentRepo;
        this.cartItemRepo = cartItemRepo;
        this.orderItemRepo = orderItemRepo;
        this.userService = userService;
        this.cartService = cartService;
        this.modelMapper = modelMapper;
    }

    @Override
    public OrderDTO placeOrder(String emailId, Long cartId, String paymentMethod) {
        Cart cart = cartRepo.findCartByEmailAndCartId(emailId, cartId);
        if (cart==null){
            throw new ResourceNotFoundException("Cart", "cartId", cartId);
        }

        //Setting Order
        Order order = new Order();
        order.setEmail(emailId);
        order.setOrderDate(LocalDate.now());
        order.setTotalAmount(cart.getTotalPrice());
        order.setOrderStatus("Order Accepted!");
        //Setting Payment
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentMethod(paymentMethod);
        Payment savedPayment = paymentRepo.save(payment);
        
        order.setPayment(savedPayment);
        
        //Saving
        Order savedOrder = orderRepo.save(order);

        //calling CartItems
        List<CartItem> cartItems = cart.getCartItems();

        if (cartItems.size()==0){
            throw new APIException("Cart is empty!");
        }

        //making new OrderItems then copying cartitems to orderitems
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cartItems){
            OrderItem orderItem = new OrderItem();

            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setDiscount(cartItem.getDiscount());
            orderItem.setOrderedProductPrice(cartItem.getProductPrice());
            orderItem.setOrder(savedOrder);

            orderItems.add(orderItem);

        }

        List<OrderItem> savedOrderItems = orderItemRepo.saveAll(orderItems);

        //deleting each cartItems
        cart.getCartItems().forEach(item -> {
            Integer quantity = item.getQuantity();
            Product product = item.getProduct();
            cartService.deleteProductFromCart(cartId, item.getProduct().getProductId());
            product.setQuantity(product.getQuantity() - quantity);
        });

        OrderDTO orderDTO = modelMapper.map(savedOrder, OrderDTO.class);

        orderItems.forEach(item -> orderDTO.getOrderItems().add(modelMapper.map(item, OrderItemDTO.class)));

//        cart.setTotalPrice(0.0);

        return orderDTO;
    }

    @Override
    @Cacheable(value = "orderCache", key = "#orderId")
    public OrderDTO getOrder(String emailId, Long orderId) {
        Order order = orderRepo.findByEmailAndOrderId(emailId, orderId);
        if(order == null){
            throw new ResourceNotFoundException("Order", "orderId", orderId);
        }

        return modelMapper.map(order, OrderDTO.class);
    }

    @Override
    @Cacheable(value = "orderListCache", key = "#emailId")
    public List<OrderDTO> getOrdersByUser(String emailId) {
        List<Order> orders = orderRepo.findAllByEmail(emailId);
        if (orders.size() == 0){
            throw new APIException("No orders placed yet by the user with email: " + emailId);
        }

        List<OrderDTO> orderDTOs = orders.stream().map(order -> modelMapper.map(order, OrderDTO.class)).collect(Collectors.toList());
        return orderDTOs;
    }

    @Override
    @Cacheable(value = "orderListCache", key = "#pageNumber + '-' + #pageSize + '-' + #sortBy + '-' + #sortOrder")
    public OrderResponse getAllOrders(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Order> pageOrders = orderRepo.findAll(pageDetails);
        List<Order> orders = pageOrders.getContent();

        List<OrderDTO> orderDTOs = orders.stream().map(order -> modelMapper.map(order, OrderDTO.class)).collect(Collectors.toList());

        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setContent(orderDTOs);
        orderResponse.setPageNumber(pageOrders.getNumber());
        orderResponse.setPageSize(pageOrders.getSize());
        orderResponse.setTotalElements(pageOrders.getTotalElements());
        orderResponse.setTotalPages(pageOrders.getTotalPages());
        orderResponse.setLastPage(pageOrders.isLast());

        return orderResponse;
    }

    @Override
    @CachePut(value = "orderCache", key = "#orderId")
    @CacheEvict(value = "orderListCache", allEntries = true)
    public OrderDTO updateOrder(String emailId, Long orderId, String orderStatus) {
        Order order = orderRepo.findByEmailAndOrderId(emailId, orderId);

        if (order == null) {
            throw new ResourceNotFoundException("Order", "orderId", orderId);
        }

        order.setOrderStatus(orderStatus);

        return modelMapper.map(order, OrderDTO.class);
    }
}
