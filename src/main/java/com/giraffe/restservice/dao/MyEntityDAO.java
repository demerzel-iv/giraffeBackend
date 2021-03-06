package com.giraffe.restservice.dao;

import java.util.List;

import com.giraffe.restservice.pojo.MyEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MyEntityDAO extends JpaRepository<MyEntity, Integer> {
    boolean existsByCourseAndLabel(String course, String label);

    @Query(value = "select * from entity where course = ?1 and label = ?2 limit 1", nativeQuery = true)
    MyEntity findByCourseAndLabel(String course, String label);

    @Query(value = "select * from entity where course = ?1 order by id", nativeQuery = true)
    List<MyEntity> getEntityByCourse(String course);
}
