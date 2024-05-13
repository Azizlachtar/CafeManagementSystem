package com.cafe.cafemanagementsystem.restImpl;

import com.cafe.cafemanagementsystem.constants.CafeConstants;
import com.cafe.cafemanagementsystem.rest.ProductRest;
import com.cafe.cafemanagementsystem.rest.UserRest;
import com.cafe.cafemanagementsystem.service.ProductService;
import com.cafe.cafemanagementsystem.service.UserService;
import com.cafe.cafemanagementsystem.utils.CafeUtils;
import com.cafe.cafemanagementsystem.wrapper.ProductWrapper;
import com.cafe.cafemanagementsystem.wrapper.UserWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ProductRestImpl implements ProductRest {

    @Autowired
    ProductService productService;


    @Override
    public ResponseEntity<String> addNewProduct(Map<String, String> requestMap) {
        try {
            return productService.addNewProduct(requestMap);

        }catch (Exception exception){
            exception.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOME_THING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @Override
    public ResponseEntity<List<ProductWrapper>> getAllProduct() {
        try {
            return productService.getAllProduct();

        }catch (Exception exception){
            exception.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @Override
    public ResponseEntity<String> updateProduct(Map<String, String> requestMap) {
        try {
            return productService.updateProduct(requestMap);

        }catch (Exception exception){
            exception.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOME_THING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @Override
    public ResponseEntity<String> deleteProduct(Integer id) {
        try {
            return productService.deleteProduct(id);

        }catch (Exception exception){
            exception.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOME_THING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @Override
    public ResponseEntity<String> updateStatus(Map<String, String> requestMap) {
        try {
            return productService.updateStatus(requestMap);

        }catch (Exception exception){
            exception.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOME_THING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @Override
    public ResponseEntity<List<ProductWrapper>> getByCategory(Integer id) {
        try {
            return productService.getByCategory(id);

        }catch (Exception exception){
            exception.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @Override
    public ResponseEntity<ProductWrapper> getProductById(Integer id) {
        try {
            return productService.getProductById(id);

        }catch (Exception exception){
            exception.printStackTrace();
        }
        return new ResponseEntity<>(new ProductWrapper(), HttpStatus.INTERNAL_SERVER_ERROR);

    }
}
