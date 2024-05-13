package com.cafe.cafemanagementsystem.service;

import com.cafe.cafemanagementsystem.POJO.Bill;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface DashboardService {


    ResponseEntity<Map<String, Object>> getCount();

}
