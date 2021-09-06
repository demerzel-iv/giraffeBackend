package com.giraffe.restservice.dao;

import java.util.List;

import com.giraffe.restservice.pojo.Edge;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EdgeDAO extends JpaRepository<Edge, Integer> {

    @Query(value = "select (eid2) from edge where eid1=?1", nativeQuery = true)
    List<Integer> findNeighborhood(int eid1);

}
