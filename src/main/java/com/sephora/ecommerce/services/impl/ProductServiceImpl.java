package com.sephora.ecommerce.services.impl;

import com.sephora.ecommerce.entities.Cart;
import com.sephora.ecommerce.entities.Category;
import com.sephora.ecommerce.entities.Product;
import com.sephora.ecommerce.exceptions.APIException;
import com.sephora.ecommerce.exceptions.ResourceNotFoundException;
import com.sephora.ecommerce.payloads.CartDTO;
import com.sephora.ecommerce.payloads.ProductDTO;
import com.sephora.ecommerce.repositories.CartRepository;
import com.sephora.ecommerce.repositories.CategoryRepository;
import com.sephora.ecommerce.repositories.ProductRepository;
import com.sephora.ecommerce.services.CartService;
import com.sephora.ecommerce.services.FileService;
import com.sephora.ecommerce.services.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class ProductServiceImpl implements ProductService {
    private ProductRepository productRepository;
    private CategoryRepository categoryRepository;
    private CartRepository cartRepository;
    private FileService fileService;
    private ModelMapper modelMapper;
    private CartService cartService;

    @Value("${project.image}")
    private String path;

    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository, CartRepository cartRepository, FileService fileService, ModelMapper modelMapper, CartService cartService) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.cartRepository = cartRepository;
        this.fileService = fileService;
        this.modelMapper = modelMapper;
        this.cartService = cartService;
    }

    @Override
    public ProductDTO addProduct(ProductDTO productDTO) {
        // Check if a product with the same name already exists
        if (productRepository.existsByProductName(productDTO.getProductName())) {
            throw new APIException("A product with the same name already exists.");
        }

        Product product = modelMapper.map(productDTO, Product.class);
        Set<Category> categoriesToAssociate = new HashSet<>();

        for (String categoryName : productDTO.getCategoryNames()) {
            Category category = categoryRepository.findByCategoryName(categoryName);
            if (category != null) {
                categoriesToAssociate.add(category);
            }
        }

        product.setCategories(categoriesToAssociate);
        Product savedProduct = productRepository.save(product);

        ProductDTO savedProductDTO = modelMapper.map(savedProduct, ProductDTO.class);
        savedProductDTO.setCategoryNames(mapCategoriesToCategoryNames(categoriesToAssociate));

        return savedProductDTO;
    }

    @Override
    public List<ProductDTO> getAllProducts() {
        List<Product> allProducts = productRepository.findAll();

        List<ProductDTO> productDTOs = allProducts.stream()
                .map(product -> {
                    ProductDTO dto = modelMapper.map(product, ProductDTO.class);
                    dto.setCategoryNames(mapCategoriesToCategoryNames(product.getCategories()));
                    return dto;
                }).collect(Collectors.toList());
        return productDTOs;
    }

    @Override
    public List<ProductDTO> searchByCategory(String categoryName) {
        Category savedCategory = categoryRepository.findByCategoryName(categoryName);
        if(savedCategory == null){
            throw new ResourceNotFoundException("Category","name", categoryName);
        }

        List<Product> products = productRepository.findByCategoriesCategoryName(categoryName);
        if(products.isEmpty()){
            throw new APIException("No products found in the category with name '" + categoryName + "'.");
        }

        List<ProductDTO> productDTOs = products.stream().map(product -> {
            Product productWithCategories = productRepository.findById(product.getProductId()).orElseThrow(() -> new APIException("Null Values"));
            ProductDTO dto = modelMapper.map(productWithCategories, ProductDTO.class);
            dto.setCategoryNames(mapCategoriesToCategoryNames(productWithCategories.getCategories()));
            return dto;
        }).collect(Collectors.toList());

        return productDTOs;
    }

    @Override
    public List<ProductDTO> searchByKeyword(String keyword) {
        List<Product> products = productRepository.findByProductNameLike("%" + keyword + "%");
        System.out.println(products);
        if(products.isEmpty()){
            throw new APIException("No products found with keyword '" + keyword + "'.");
        }

        List<ProductDTO> productDTOs = products.stream()
                .map(product -> {
                    ProductDTO dto = modelMapper.map(product, ProductDTO.class);
                    dto.setCategoryNames(mapCategoriesToCategoryNames(product.getCategories()));
                    return dto;
                }).collect(Collectors.toList());
        System.out.println("ProductDTOs: " + productDTOs);
        return productDTOs;
    }

    @Override
    public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        if (!existingProduct.getProductName().equals(productDTO.getProductName()) &&
                productRepository.existsByProductName(productDTO.getProductName())) {
            throw new APIException("A product with the same name already exists.");
        }

        existingProduct.setProductName(productDTO.getProductName());
        existingProduct.setImage(productDTO.getImage());
        existingProduct.setDescription(productDTO.getDescription());
        existingProduct.setQuantity(productDTO.getQuantity());
        existingProduct.setPrice(productDTO.getPrice());
        existingProduct.setDiscount(productDTO.getDiscount());

        double specialPrice = productDTO.getPrice() - ((productDTO.getDiscount() * 0.01) * productDTO.getPrice());
        existingProduct.setSpecialPrice(specialPrice);

        // Clear existing categories if categoryNames is provided; otherwise, keep existing categories
        if (productDTO.getCategoryNames() != null) {
            existingProduct.getCategories().clear();
            for (String categoryName : productDTO.getCategoryNames()) {
                Category category = categoryRepository.findByCategoryName(categoryName);
                if (category != null) {
                    existingProduct.getCategories().add(category);
                }
            }
        }

        Product updatedProduct = productRepository.save(existingProduct);

        List<Cart> carts = cartRepository.findCartsByProductId(productId);
        List<CartDTO> cartDTOs = carts.stream().map(cart -> {
            CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
            List<ProductDTO> productDTOS = cart.getCartItems().stream().map(p -> modelMapper.map(p.getProduct(), ProductDTO.class)).collect(Collectors.toList());
            cartDTO.setProductDTOList(productDTOS);
            return cartDTO;
        }).collect(Collectors.toList());

        cartDTOs.forEach(cart -> cartService.updateProductInCarts(cart.getCartId(), productId));

        ProductDTO updatedProductDTO = modelMapper.map(updatedProduct, ProductDTO.class);

        updatedProductDTO.setCategoryNames(mapCategoriesToCategoryNames(updatedProduct.getCategories()));
        return updatedProductDTO;
    }

    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        String fileName = fileService.uploadImage(path, image);
        product.setImage(fileName);
        Product updatedProduct = productRepository.save(product);
        ProductDTO updatedProductDTO = modelMapper.map(updatedProduct, ProductDTO.class);

        updatedProductDTO.setCategoryNames(mapCategoriesToCategoryNames(updatedProduct.getCategories()));
        return updatedProductDTO;
    }

    @Override
    public InputStreamResource showProductImage(String imageName) throws FileNotFoundException {
        String realpath = path; // Ensure this path is correct
        InputStream inputStream = fileService.getResource(realpath, imageName);
        return new InputStreamResource(inputStream);
    }

    @Override
    public String deleteProduct(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));
        product.getCategories().clear();
        productRepository.delete(product);
        return "Product with productId: " + productId + " and Name: "+product.getProductName()+" deleted successfully !!!";
    }

    private Set<String> mapCategoriesToCategoryNames(Set<Category> categories) {
        return categories.stream()
                .map(Category::getCategoryName)
                .collect(Collectors.toSet());
    }
}
