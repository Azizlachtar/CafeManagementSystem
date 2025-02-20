package com.cafe.cafemanagementsystem.POJO;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;


@NamedQuery(name = "Bill.getAllBills", query = "SELECT b FROM Bill b ORDER BY b.id DESC ")
@NamedQuery(name = "Bill.getBillByUserName", query = "SELECT b FROM Bill b WHERE b.createdBy=:username ORDER BY b.id DESC")

@Entity
@DynamicInsert
@DynamicUpdate
@Data
@Table(name = "bill")
public class Bill implements Serializable {

    private static final long serialVersionUID =1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "uuid")
    private String uuid;


    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "contactnumber")
    private String contactNumber;

    @Column(name = "paymentmethod")
    private String paymentMethod;

    @Column(name = "total")
    private Integer total;

    @Column(name = "productdetails",columnDefinition = "json")
    private String productDetails;

    @Column(name = "createdby")
    private String createdBy;

}
