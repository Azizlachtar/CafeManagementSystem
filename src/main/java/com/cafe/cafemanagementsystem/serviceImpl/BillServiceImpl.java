package com.cafe.cafemanagementsystem.serviceImpl;

import com.cafe.cafemanagementsystem.POJO.Bill;
import com.cafe.cafemanagementsystem.constants.CafeConstants;
import com.cafe.cafemanagementsystem.dao.BillDao;
import com.cafe.cafemanagementsystem.jwt.JwtFilter;
import com.cafe.cafemanagementsystem.service.BillService;
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

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;


@Service
@Slf4j
public class BillServiceImpl implements BillService {

    @Autowired
    JwtFilter jwtFilter;

    @Autowired
    BillDao billdao;



    @Override
    public ResponseEntity<String> generateReport(Map<String, Object> requestMap) {
        try {
            String fileName;
            if(validateRequestMap(requestMap)){
                if(requestMap.containsKey("isGenerate") && !(Boolean)requestMap.get("isGenerate")){
                    fileName= (String) requestMap.get("uuid");
                }else {
                    fileName=CafeUtils.getUUID();
                    requestMap.put("uuid",fileName);
                    insertBill(requestMap);
                }

                String data="Name"+ requestMap.get("name")+"\n"+"Contact Number"+requestMap.get("contactNumber")
                        +"\n"+"Email"+requestMap.get("email")+"\n"+"Payment Method "+requestMap.get("paymentMethod");

                Document document =new Document();
                PdfWriter.getInstance(document,new FileOutputStream(CafeConstants.STORE_LOCATION+"\\"+fileName+".pdf"));

                document.open();
                setRectangleInPdf(document);
                Paragraph chunk= new Paragraph("Cafe management System",getFont("Header"));
                chunk.setAlignment(Element.ALIGN_CENTER);
                document.add(chunk);

                Paragraph paragraph =new Paragraph(data+ "\n \n",getFont("Data"));
                document.add(paragraph);

                PdfPTable table=new PdfPTable(5);
                table.setWidthPercentage(100);
                addTableHeader(table);

                JSONArray jsonArray=CafeUtils.getJsonArrayFromString((String) requestMap.get("productDetails"));
                for (int i=0;i<jsonArray.length();i++){
                    addRows(table,CafeUtils.getMapFromJson(jsonArray.getString(i)));
                }
                document.add(table);

                Paragraph footer= new Paragraph("Total :"+ requestMap.get("totalAmount")+"\n"+
                        "Thank you for visiting.Please contact again",getFont("DATA"));
                document.add(footer);
                document.close();
                return new ResponseEntity<>("{\"uui\":\""+ fileName+ "\"}",HttpStatus.OK);


            }
            return CafeUtils.getResponseEntity("Required data not found", HttpStatus.BAD_REQUEST);


        } catch (Exception exception){
            exception.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOME_THING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }



    private void addRows(PdfPTable table, Map<String, Object> data) {
        table.addCell((String) data.get("name"));
        table.addCell((String) data.get("category"));
        table.addCell((String) data.get("quantity"));
        table.addCell(Double.toString((Double) data.get("price")));
        table.addCell(Double.toString((Double) data.get("total")));
    }

    private void addTableHeader(PdfPTable table) {
        Stream.of("name","Category","Quantity", "Price","Sub Total")
                .forEach(columnTile ->{
                    PdfPCell header =new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTile));
                    header.setHorizontalAlignment(Element.ALIGN_CENTER);
                    header.setVerticalAlignment(Element.ALIGN_CENTER);
                    table.addCell(header);

                });
    }

    private Font getFont(String type) {
        switch (type){
            case "Header":
                Font headerFont= FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE,18,BaseColor.BLACK);
                headerFont.setStyle(Font.BOLD);
                return headerFont;
            case "Data":
                Font dataFont= FontFactory.getFont(FontFactory.TIMES_ROMAN,11,BaseColor.BLACK);
                dataFont.setStyle(Font.BOLD);
                return dataFont;
            default:
                return new Font();
        }
    }

    private void setRectangleInPdf(Document document) throws DocumentException {
        Rectangle rec=new Rectangle(557,825,18,15 );
        rec.enableBorderSide(1);
        rec.enableBorderSide(2);
        rec.enableBorderSide(4);
        rec.enableBorderSide(8);
        rec.setBorderColor(BaseColor.BLACK);
        rec.enableBorderSide(1);
        document.add(rec);



    }

    private void insertBill(Map<String, Object> requestMap) {
        try {
            Bill bill=new Bill();
            bill.setUuid((String) requestMap.get("uuid"));
            bill.setName((String)requestMap.get("name"));
            bill.setEmail((String)requestMap.get("email"));
            bill.setContactNumber((String)requestMap.get("contactNumber"));
            bill.setPaymentMethod((String)requestMap.get("paymentMethod"));
            bill.setTotal(Integer.parseInt((String) requestMap.get("totalAmount")));
            bill.setProductDetails((String) requestMap.get("productDetails"));
            bill.setCreatedBy(jwtFilter.getCurrentUser());
            billdao.save(bill);

        }catch (Exception exception){
            exception.printStackTrace();
        }
    }

    private boolean validateRequestMap(Map<String, Object> requestMap) {
        return requestMap.containsKey("name") && requestMap.containsKey("contactNumber") &&
                requestMap.containsKey("email") && requestMap.containsKey("paymentMethod") &&
                requestMap.containsKey("productDetails") && requestMap.containsKey("totalAmount");
    }

    @Override
    public ResponseEntity<List<Bill>> getBills() {
       List<Bill> list=new ArrayList<>();
       if (jwtFilter.isAdmin()){
           list=billdao.getAllBills();
       }else {
           list=billdao.getBillByUserName(jwtFilter.getCurrentUser());
       }
       return new ResponseEntity<>(list,HttpStatus.OK);
    }

    @Override
    public ResponseEntity<byte[]> getPdf(Map<String, Object> requestMap) {
        try {
            byte[] byteArray=new byte[0];
            if(!requestMap.containsKey("uuid") && validateRequestMap(requestMap))
                return new ResponseEntity<>(byteArray,HttpStatus.BAD_REQUEST);
            String filePath=CafeConstants.STORE_LOCATION +"\\"+ (String) requestMap.get("uuid")+".pdf";
            if (CafeUtils.isFileExist(filePath)){
                byteArray =getByteArray(filePath);
                return new ResponseEntity<>(byteArray,HttpStatus.OK);
            }else {
                requestMap.put("isGenerate",false);
                generateReport(requestMap);
                byteArray =getByteArray(filePath);
                return new ResponseEntity<>(byteArray,HttpStatus.OK);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private byte[] getByteArray(String filePath) throws Exception {
        File initialFile =new File(filePath);
        InputStream targetStream =new FileInputStream(initialFile);
        byte[] byteArray = IOUtils.toByteArray(targetStream);
        targetStream.close();
        return byteArray;

    }

    @Override
    public ResponseEntity<String> deleteBill(Integer id) {
        try {
            Optional optional= billdao.findById(id);
            if(!optional.isEmpty()){
                billdao.deleteById(id);
                return CafeUtils.getResponseEntity("bill is deleted successfully", HttpStatus.OK);

            }
            return CafeUtils.getResponseEntity("bill id is not exist", HttpStatus.OK);

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOME_THING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
