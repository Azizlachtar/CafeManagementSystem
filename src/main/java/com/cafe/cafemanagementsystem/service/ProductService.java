package com.cafe.cafemanagementsystem.service;

import com.cafe.cafemanagementsystem.wrapper.ProductWrapper;
import com.cafe.cafemanagementsystem.wrapper.UserWrapper;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface ProductService {


    ResponseEntity<String> addNewProduct(Map<String, String> requestMap);

    ResponseEntity<List<ProductWrapper>> getAllProduct();

    ResponseEntity<String> updateProduct(Map<String, String> requestMap);

    ResponseEntity<String> deleteProduct(Integer id);

    ResponseEntity<String> updateStatus(Map<String, String> requestMap);

    ResponseEntity<List<ProductWrapper>> getByCategory(Integer id);

    ResponseEntity<ProductWrapper> getProductById(Integer id);
}
