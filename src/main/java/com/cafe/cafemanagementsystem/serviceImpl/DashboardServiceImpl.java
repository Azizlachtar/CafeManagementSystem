package com.cafe.cafemanagementsystem.serviceImpl;

import com.cafe.cafemanagementsystem.POJO.Bill;
import com.cafe.cafemanagementsystem.constants.CafeConstants;
import com.cafe.cafemanagementsystem.dao.BillDao;
import com.cafe.cafemanagementsystem.dao.CategoryDao;
import com.cafe.cafemanagementsystem.dao.ProductDao;
import com.cafe.cafemanagementsystem.jwt.JwtFilter;
import com.cafe.cafemanagementsystem.rest.DashboardRest;
import com.cafe.cafemanagementsystem.service.BillService;
import com.cafe.cafemanagementsystem.service.DashboardService;
import com.cafe.cafemanagementsystem.utils.CafeUtils;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.io.IOUtils;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;


@Service
@Slf4j
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    CategoryDao categoryDao;

    @Autowired
    ProductDao productDao;

    @Autowired
    BillDao billDao;


    @Override
    public ResponseEntity<Map<String, Object>> getCount() {
        Map<String,Object> map=new HashMap<>();
        map.put("category",categoryDao.count());
        map.put("product",productDao.count());
        map.put("bill",billDao.count());
        return new ResponseEntity<>(map,HttpStatus.OK);

    }
}
