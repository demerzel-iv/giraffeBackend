package com.giraffe.restservice.dao;

import com.giraffe.restservice.pojo.Cache;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CacheDAO extends JpaRepository<Cache, Integer> {
    Cache findBySearchKey(String searchKey);

    boolean existsBySearchKey(String searchKey);
}
