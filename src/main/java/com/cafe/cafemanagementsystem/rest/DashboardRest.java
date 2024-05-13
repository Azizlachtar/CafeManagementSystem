package com.cafe.cafemanagementsystem.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RequestMapping(path = "/dashboard")
public interface DashboardRest {

    @GetMapping(path = "/details")
    ResponseEntity<Map<String,Object>> getCount();



}
