package com.cafe.cafemanagementsystem.restImpl;

import com.cafe.cafemanagementsystem.POJO.Bill;
import com.cafe.cafemanagementsystem.constants.CafeConstants;
import com.cafe.cafemanagementsystem.rest.BillRest;
import com.cafe.cafemanagementsystem.service.BillService;
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
public class BillRestIml implements BillRest {

    @Autowired
    BillService billService;


    @Override
    public ResponseEntity<String> generateReport(Map<String, Object> requestMap) {
        try {
            log.info("Inside if");
            return billService.generateReport(requestMap);

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOME_THING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<Bill>> getBills() {
        try {
            log.info("Inside if");
            return billService.getBills();

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public ResponseEntity<byte[]> getPdf(Map<String, Object> requestMap) {
        try {
            log.info("Inside if");
            return billService.getPdf(requestMap);

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public ResponseEntity<String> deleteBill(Integer id) {
        try {
            log.info("Inside if");
            return billService.deleteBill(id);

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOME_THING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
