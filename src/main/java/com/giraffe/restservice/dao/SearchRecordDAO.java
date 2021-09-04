package com.giraffe.restservice.dao;

import java.util.List;

import com.giraffe.restservice.pojo.SearchRecord;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SearchRecordDAO extends JpaRepository<SearchRecord, Integer> {
    void deleteByUidAndCourseAndSearchKey(int uid, String course, String searchKey);

    @Query(value = "select * from search_record where uid = ?1 order by id desc limit 100", nativeQuery = true)
    List<SearchRecord> getByUid(int uid);
}
