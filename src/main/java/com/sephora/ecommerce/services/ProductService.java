package com.sephora.ecommerce.services;

import com.sephora.ecommerce.entities.Product;
import com.sephora.ecommerce.payloads.ProductDTO;
import com.sephora.ecommerce.payloads.ProductResponse;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface ProductService {
    ProductDTO addProduct(ProductDTO productDTO);
    ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
    ProductResponse searchByCategory(String categoryName, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
    ProductResponse searchByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
    ProductDTO updateProduct(Long productId, ProductDTO productDTO);
    ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException;
    InputStreamResource showProductImage(String imageName) throws FileNotFoundException;
    String deleteProduct(Long productId);
}
