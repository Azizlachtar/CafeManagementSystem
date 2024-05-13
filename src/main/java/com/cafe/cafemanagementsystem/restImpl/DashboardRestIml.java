package com.cafe.cafemanagementsystem.restImpl;

import com.cafe.cafemanagementsystem.POJO.Bill;
import com.cafe.cafemanagementsystem.constants.CafeConstants;
import com.cafe.cafemanagementsystem.rest.BillRest;
import com.cafe.cafemanagementsystem.rest.DashboardRest;
import com.cafe.cafemanagementsystem.service.BillService;
import com.cafe.cafemanagementsystem.service.DashboardService;
import com.cafe.cafemanagementsystem.utils.CafeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;


@RestController
@Slf4j
public class DashboardRestIml implements DashboardRest {

    @Autowired
    DashboardService dashboardService;

    @Override
    public ResponseEntity<Map<String, Object>> getCount() {
        return dashboardService.getCount();
    }
}
