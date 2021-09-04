package com.giraffe.restservice.dao;

import java.util.List;

import com.giraffe.restservice.pojo.EntityRecord;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EntityRecordDAO extends JpaRepository<EntityRecord, Integer> {
    void deleteByUidAndEid(int uid, int eid);

    @Query(value = "select * from entity_record where uid = ?1 order by id desc limit 100", nativeQuery = true)
    List<EntityRecord> getByUid(int uid);
}
