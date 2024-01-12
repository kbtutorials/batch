package com.example.batch.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name="CUSTOMER_INFO_BATCH")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Customer {

    @Id
    @Column(name="id")
    private Integer id;

    @Column(name="first-name")
    private String firstName;

    @Column(name="last-name")
    private String lastName;

    @Column(name="email")
    private String email;

    @Column(name="gender")
    private String gender;

    @Column(name="contact-number")
    private String contactNo;

    @Column(name="country")
    private String country;

    @Column(name="dob")
    private String dob;
}
