package com.giraffe.restservice.dao;

import java.util.List;

import com.giraffe.restservice.pojo.MyEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MyEntityDAO extends JpaRepository<MyEntity, Integer> {
    MyEntity findEntityByLabel(String label);
    boolean existsEntityByLabel(String label);

    @Query(value = "select * from entity where course = ?1 order by id", nativeQuery = true)
    List<MyEntity> getEntityByCourse(String course);
}
