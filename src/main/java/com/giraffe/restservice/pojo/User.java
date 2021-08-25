package com.giraffe.restservice.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "user")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(length = 16)
    private String phone;

    @Column(length = 32)
    private String password;

    @Column(length = 128)
    private String name;

    @Column(length = 2)
    private String gender;

    @Column(length = 128)
    private String courseListString = "[\"biology\", \"chinese\", \"geo\", \"math\", \"politics\", \"chemistry\", \"english\", \"history\", \"physics\"]";

}