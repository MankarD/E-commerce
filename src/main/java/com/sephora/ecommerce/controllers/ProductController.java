package com.sephora.ecommerce.controllers;

import com.sephora.ecommerce.config.AppConstants;
import com.sephora.ecommerce.payloads.ProductDTO;
import com.sephora.ecommerce.payloads.ProductResponse;
import com.sephora.ecommerce.services.FileService;
import com.sephora.ecommerce.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ProductController {
    private ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/admin/product")
    public ResponseEntity<ProductDTO> addProduct(@RequestBody ProductDTO productDTO) {
        ProductDTO savedProductDTO = productService.addProduct(productDTO);
        return new ResponseEntity<>(savedProductDTO, HttpStatus.CREATED);
    }

    @GetMapping("/public/products")
    public ResponseEntity<ProductResponse> getAllProducts(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCTS_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder
    ) {
        ProductResponse allProducts = productService.getAllProducts(pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(allProducts, HttpStatus.FOUND);
    }

    @GetMapping("/public/categories/{categoryName}/products")
    public ResponseEntity<ProductResponse> getAllProductsByCategory(
            @PathVariable String categoryName,
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCTS_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder
    ) {
        ProductResponse productDTOs = productService.searchByCategory(categoryName, pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(productDTOs, HttpStatus.FOUND);
    }

    @GetMapping("/public/products/keyword/{keyword}")
    public ResponseEntity<ProductResponse> getProductByKeyword(
            @PathVariable String keyword,
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCTS_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder
    ) {
        ProductResponse productDTOs = productService.searchByKeyword(keyword, pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(productDTOs, HttpStatus.FOUND);
    }

    @PutMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> updateProductById(@PathVariable Long productId, ProductDTO productDTO) {
        ProductDTO savedProductDTO = productService.updateProduct(productId, productDTO);
        return new ResponseEntity<>(savedProductDTO, HttpStatus.OK);
    }

    @PatchMapping("/admin/products/{productId}/image")
    public ResponseEntity<ProductDTO> updateProductImage(@PathVariable Long productId, @RequestParam("image") MultipartFile image) throws IOException {
        ProductDTO updatedProductDTO = productService.updateProductImage(productId, image);
        return new ResponseEntity<>(updatedProductDTO, HttpStatus.OK);
    }

    @DeleteMapping("/admin/products/{productId}")
    public ResponseEntity<String> deleteProductById(@PathVariable Long productId) {
        String status = productService.deleteProduct(productId);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @GetMapping("/product/image/{imageName}")
    public ResponseEntity<Resource> showProductImage(@PathVariable String imageName) throws FileNotFoundException {
        Resource productImageResource = productService.showProductImage(imageName);

        String contentType = determineContentType(imageName);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(productImageResource);
    }

    private String determineContentType(String fileName) {
        String extension = StringUtils.getFilenameExtension(fileName).toLowerCase();
        switch (extension) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "webp":
                return "image/webp";
            // Add other cases as needed
            default:
                return "application/octet-stream"; // Default for unknown types
        }
    }
}
