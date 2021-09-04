package com.giraffe.restservice.dao;

import java.util.List;

import com.giraffe.restservice.pojo.StarRecord;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StarRecordDAO extends JpaRepository<StarRecord, Integer> {
    void deleteByUidAndEid(int uid, int eid);
    boolean existsByUidAndEid(int uid, int eid);

    @Query(value = "select * from star_record where uid = ?1 order by id desc", nativeQuery = true)
    List<StarRecord> getByUid(int uid);
}
