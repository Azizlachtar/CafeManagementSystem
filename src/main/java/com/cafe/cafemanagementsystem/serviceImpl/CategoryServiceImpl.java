package com.cafe.cafemanagementsystem.serviceImpl;

import com.cafe.cafemanagementsystem.POJO.Category;
import com.cafe.cafemanagementsystem.constants.CafeConstants;
import com.cafe.cafemanagementsystem.dao.CategoryDao;
import com.cafe.cafemanagementsystem.jwt.JwtFilter;
import com.cafe.cafemanagementsystem.service.CategoryService;
import com.cafe.cafemanagementsystem.utils.CafeUtils;
import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    CategoryDao categoryDao;

    @Autowired
    JwtFilter jwtFilter;
    @Override
    public ResponseEntity<String> addNewCategory(Map<String, String> requestMap) {
        try {
            if(jwtFilter.isAdmin()){
                if(validateCategoryMap(requestMap,false)){
                    categoryDao.save(getCategoryFromMap(requestMap,false));
                    return CafeUtils.getResponseEntity("Category added successfully", HttpStatus.OK);

                }

            }else {
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOME_THING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private boolean validateCategoryMap(Map<String, String> requestMap, boolean validateId) {
        if (requestMap.containsKey("name")){
            if (requestMap.containsKey("id") && validateId){
                return true;
            }else if(!validateId){
                return true;
            }
        }
        return false;
    }
    private Category getCategoryFromMap(Map<String, String> requestMap, Boolean isAdd){
        Category category=new Category();
        if(isAdd){
            category.setId(Integer.parseInt(requestMap.get("id")));
        }
        category.setName(requestMap.get("name"));
        return category;
    }

    @Override
    public ResponseEntity<List<Category>> getAllCategory(String filterValue) {
       try {
           if (!Strings.isNullOrEmpty(filterValue) && filterValue.equalsIgnoreCase("true")){
               return new ResponseEntity<List<Category>>(categoryDao.getAllCategory(),HttpStatus.OK);
           }
           return new ResponseEntity<List<Category>>(categoryDao.findAll(),HttpStatus.OK);


       }catch (Exception exception){
           exception.printStackTrace();
       }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @Override
    public ResponseEntity<String> updateCategory(Map<String, String> requestMap) {
        try {
            if (jwtFilter.isAdmin()){
                if(validateCategoryMap(requestMap,true)) {
                    Optional optional = categoryDao.findById(Integer.parseInt(requestMap.get("id")));
                    if (!optional.isEmpty()) {
                        categoryDao.save(getCategoryFromMap(requestMap, true));
                        return CafeUtils.getResponseEntity("Category is updated successfully", HttpStatus.OK);
                    } else {
                        return CafeUtils.getResponseEntity("Category id is not exist", HttpStatus.OK);
                    }
                }
                return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);

            }else {
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception exception){
            exception.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOME_THING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
