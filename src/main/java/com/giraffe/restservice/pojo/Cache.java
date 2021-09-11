package com.giraffe.restservice.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Table(name = "cache")
@Entity
@Data
public class Cache {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(length = 256)
    private String searchKey;

    @Column(columnDefinition = "TEXT")
    private String result;
}
