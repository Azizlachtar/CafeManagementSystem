package com.cafe.cafemanagementsystem.serviceImpl;

import com.cafe.cafemanagementsystem.POJO.Category;
import com.cafe.cafemanagementsystem.POJO.Product;
import com.cafe.cafemanagementsystem.constants.CafeConstants;
import com.cafe.cafemanagementsystem.dao.CategoryDao;
import com.cafe.cafemanagementsystem.dao.ProductDao;
import com.cafe.cafemanagementsystem.jwt.JwtFilter;
import com.cafe.cafemanagementsystem.service.CategoryService;
import com.cafe.cafemanagementsystem.service.ProductService;
import com.cafe.cafemanagementsystem.utils.CafeUtils;
import com.cafe.cafemanagementsystem.wrapper.ProductWrapper;
import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductDao productDao;

    @Autowired
    JwtFilter jwtFilter;


    @Override
    public ResponseEntity<String> addNewProduct(Map<String, String> requestMap) {
        try {
            if (jwtFilter.isAdmin()) {
                if (validateProductMap(requestMap, false)) {
                    productDao.save(getProductFromMap(requestMap,false));
                    return CafeUtils.getResponseEntity("product added successfully", HttpStatus.OK);

                }
                return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);

                }else
                    return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
            catch(Exception exception){
                exception.printStackTrace();
            }
            return CafeUtils.getResponseEntity(CafeConstants.SOME_THING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }




    private boolean validateProductMap(Map<String, String> requestMap, boolean validateId) {
        if (requestMap.containsKey("name")) {
            if (requestMap.containsKey("id") && validateId) {
                return true;
            } else if (!validateId) {
                return true;
            }
        }
        return false;
    }
    private Product getProductFromMap(Map<String, String> requestMap, boolean isAdd) {
        Category category=new Category();
        category.setId(Integer.parseInt(requestMap.get("categoryId")));

        Product product=new Product();
        if(isAdd){
            product.setId(Integer.parseInt(requestMap.get("id")));
        }else {
            product.setStatus("true");
        }
        product.setCategory(category);
        product.setName(requestMap.get("name"));
        product.setDescription(requestMap.get("description"));
        product.setPrice(Integer.parseInt(requestMap.get("price")));
        return product;
    }

    @Override
    public ResponseEntity<List<ProductWrapper>> getAllProduct() {
        try {

            return new ResponseEntity<>(productDao.getAllProduct(),HttpStatus.OK);

        }
        catch(Exception exception){
            exception.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateProduct(Map<String, String> requestMap) {
        try {
            if (jwtFilter.isAdmin()) {
                if (validateProductMap(requestMap, true)) {
                    Optional<Product> optional=productDao.findById(Integer.parseInt(requestMap.get("id")));
                    if (!optional.isEmpty()){
                        Product product=getProductFromMap(requestMap,true);
                        product.setStatus(optional.get().getStatus());
                        productDao.save(product);
                        return CafeUtils.getResponseEntity("product updated successfully", HttpStatus.OK);
                    }else {
                        return CafeUtils.getResponseEntity("product id is not exist", HttpStatus.OK);
                    }
                }else {
                    return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
                }

            }else
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
        }
        catch(Exception exception){
            exception.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOME_THING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> deleteProduct(Integer id) {
        try {
            if (jwtFilter.isAdmin()) {
                Optional optional=productDao.findById(id);
                if(!optional.isEmpty()){
                    productDao.deleteById(id);
                    return CafeUtils.getResponseEntity("product is deleted successfully", HttpStatus.OK);

                }
                return CafeUtils.getResponseEntity("product id is not exist", HttpStatus.OK);


            }else
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
        }
        catch(Exception exception){
            exception.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOME_THING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateStatus(Map<String, String> requestMap) {
        try {
            if (jwtFilter.isAdmin()) {
                Optional optional=productDao.findById(Integer.parseInt(requestMap.get("id")));
                if(!optional.isEmpty()){
                    productDao.updateProductStatus(requestMap.get("status"),Integer.parseInt(requestMap.get("id")));
                    return CafeUtils.getResponseEntity("product status is updated successfully", HttpStatus.OK);

                }
                return CafeUtils.getResponseEntity("product id is not exist", HttpStatus.OK);


            }else
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
        }
        catch(Exception exception){
            exception.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOME_THING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<ProductWrapper>> getByCategory(Integer id) {
        try {
            return new ResponseEntity<>(productDao.getProductByCategory(id),HttpStatus.OK);

        }
        catch(Exception exception){
            exception.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<ProductWrapper> getProductById(Integer id) {
        try {
            return new ResponseEntity<>(productDao.getProductById(id),HttpStatus.OK);

        }
        catch(Exception exception){
            exception.printStackTrace();
        }
        return new ResponseEntity<>(new ProductWrapper(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
