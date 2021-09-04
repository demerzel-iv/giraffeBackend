package com.giraffe.restservice.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Table(name = "searchRecord")
@Entity
@Data
public class SearchRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column
    private int uid;

    @Column(length = 16)
    private String course;

    @Column(length = 256)
    private String searchKey;
}
