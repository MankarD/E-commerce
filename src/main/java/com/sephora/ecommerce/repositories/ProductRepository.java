package com.sephora.ecommerce.repositories;

import com.sephora.ecommerce.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByProductName(String productName);
    List<Product> findByCategoriesCategoryName(String categoryName);
    List<Product> findByProductNameLike(String keyword);

//    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.categories")
//    List<Product> findAllWithCategories();
}
