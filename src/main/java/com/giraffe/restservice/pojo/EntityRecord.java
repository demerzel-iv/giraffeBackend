package com.giraffe.restservice.pojo;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Data;

@Table(name = "entityRecord")
@Data
@Entity
public class EntityRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @CreationTimestamp
    private Date timestamp;

    @Column
    private int uid;

    @Column
    private int eid;
}
