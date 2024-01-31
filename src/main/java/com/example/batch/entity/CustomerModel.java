package com.example.batch.entity;

import jakarta.persistence.Column;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name= "record")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomerModel {

    @XmlAttribute(name="id")
    private Integer id;

    @XmlAttribute(name="firstName")
    private String firstName;

    @XmlAttribute(name="lastName")
    private String lastName;

    @XmlAttribute(name="email")
    private String email;

    @XmlAttribute(name="gender")
    private String gender;

    @XmlAttribute(name="contactNo")
    private String contactNo;

    @XmlAttribute(name="country")
    private String country;

    @XmlAttribute(name="dob")
    private String dob;
}
