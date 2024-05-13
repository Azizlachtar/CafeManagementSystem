package com.cafe.cafemanagementsystem.restImpl;

import com.cafe.cafemanagementsystem.POJO.Category;
import com.cafe.cafemanagementsystem.constants.CafeConstants;
import com.cafe.cafemanagementsystem.rest.CategoryRest;
import com.cafe.cafemanagementsystem.service.CategoryService;
import com.cafe.cafemanagementsystem.utils.CafeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@RestController
@Slf4j
public class CategoryRestIml implements CategoryRest {

    @Autowired
    CategoryService categoryService;
    @Override
    public ResponseEntity<String> addNewCategory(Map<String, String> requestMap) {
        try {
            return categoryService.addNewCategory(requestMap);

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOME_THING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<Category>> getAllCategory(String filterValue) {
        try {
            log.info("Inside if");
            return categoryService.getAllCategory(filterValue);

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<Category>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateCategory(Map<String, String> requestMap) {
        try {
            log.info("Inside if");
            return categoryService.updateCategory(requestMap);

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOME_THING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
