//package com.sephora.ecommerce.repositories;
//
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.PersistenceContext;
//import org.springframework.stereotype.Repository;
//import org.springframework.transaction.annotation.Transactional;
//
//@Repository
//public class CustomProductRepositoryImpl implements CustomProductRepository{
//
//    @PersistenceContext
//    private EntityManager entityManager;
//
//    @Override
//    @Transactional
//    public void deleteProductCategories(Long productId) {
//        entityManager.createNativeQuery("DELETE FROM product_category WHERE product_id = :productId")
//                .setParameter("productId", productId)
//                .executeUpdate();
//    }
//
//    @Override
//    @Transactional
//    public void addProductCategory(Long productId, Long categoryId) {
//        entityManager.createNativeQuery("INSERT INTO product_category (product_id, category_id) VALUES (:productId, :categoryId)")
//                .setParameter("productId", productId)
//                .setParameter("categoryId", categoryId)
//                .executeUpdate();
//    }
//}
