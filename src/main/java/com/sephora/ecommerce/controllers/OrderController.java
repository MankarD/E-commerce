package com.sephora.ecommerce.controllers;

import com.sephora.ecommerce.config.AppConstants;
import com.sephora.ecommerce.payloads.OrderDTO;
import com.sephora.ecommerce.payloads.OrderResponse;
import com.sephora.ecommerce.payloads.UserResponse;
import com.sephora.ecommerce.services.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class OrderController {
    private OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/public/users/{emailId}/carts/{cartId}/payments/{paymentMethod}")
    public ResponseEntity<OrderDTO> OrderProducts(@PathVariable String emailId, @PathVariable Long cartId, @PathVariable String paymentMethod){
        OrderDTO dto = orderService.placeOrder(emailId, cartId, paymentMethod);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @GetMapping("/public/users/{emailId}/orders/{orderId}")
    public ResponseEntity<OrderDTO> getOrderByUser(@PathVariable String emailId, @PathVariable Long orderId){
        OrderDTO dto = orderService.getOrder(emailId, orderId);
        return new ResponseEntity<>(dto, HttpStatus.FOUND);
    }

    @GetMapping("/public/users/{emailId}/orders")
    public ResponseEntity<List<OrderDTO>> getOrdersByUser(@PathVariable String emailId){
        List<OrderDTO> dtos = orderService.getOrdersByUser(emailId);
        return new ResponseEntity<>(dtos, HttpStatus.FOUND);
    }

    @GetMapping("/admin/orders")
    public ResponseEntity<OrderResponse> getAllOrders(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_ORDERS_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder
    ){
        OrderResponse dtos = orderService.getAllOrders(pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(dtos, HttpStatus.FOUND);
    }

    @PatchMapping("/admin/users/{emailId}/orders/{orderId}/")
    public ResponseEntity<OrderDTO> updateOrderStatus(@PathVariable String emailId, @PathVariable Long orderId, @RequestParam String orderStatus){
        OrderDTO dto = orderService.updateOrder(emailId, orderId, orderStatus);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

}
