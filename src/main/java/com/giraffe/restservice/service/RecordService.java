package com.giraffe.restservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import com.giraffe.restservice.dao.EntityRecordDAO;
import com.giraffe.restservice.dao.MyEntityDAO;
import com.giraffe.restservice.dao.SearchRecordDAO;
import com.giraffe.restservice.dao.StarRecordDAO;
import com.giraffe.restservice.pojo.EntityRecord;
import com.giraffe.restservice.pojo.MyEntity;
import com.giraffe.restservice.pojo.SearchRecord;
import com.giraffe.restservice.pojo.StarRecord;
import com.giraffe.restservice.service.RecordService;

@Service
public class RecordService {
    private MyEntityDAO myEntityDAO;
    private StarRecordDAO starRecordDAO;
    private EntityRecordDAO entityRecordDAO;
    private SearchRecordDAO searchRecordDAO;

    @Autowired
    RecordService(MyEntityDAO myEntityDAO, StarRecordDAO starRecordDAO, EntityRecordDAO entityRecordDAO,
            SearchRecordDAO searchRecordDAO) {
        this.myEntityDAO = myEntityDAO;
        this.starRecordDAO = starRecordDAO;
        this.entityRecordDAO = entityRecordDAO;
        this.searchRecordDAO = searchRecordDAO;
    }

    public int getEidByCourseLabel(String course, String label) {
        if (!myEntityDAO.existsByCourseAndLabel(course, label)) {
            MyEntity entity = new MyEntity();
            entity.setCourse(course);
            entity.setLabel(label);
            myEntityDAO.save(entity);
        }
        MyEntity entity = myEntityDAO.findByCourseAndLabel(course, label);
        return entity.getId();
    }

    public boolean isStarred(int uid, String course, String label) {
        int eid = getEidByCourseLabel(course, label);
        return starRecordDAO.existsByUidAndEid(uid, eid);
    }

    public void star(int uid, String course, String label) {
        int eid = getEidByCourseLabel(course, label);

        if (!starRecordDAO.existsByUidAndEid(uid, eid)) {
            StarRecord starRecord = new StarRecord();
            starRecord.setUid(uid);
            starRecord.setEid(eid);
            starRecordDAO.save(starRecord);
        }
    }

    public void visit(int uid, String course, String label) {
        int eid = getEidByCourseLabel(course, label);
        entityRecordDAO.deleteByUidAndEid(uid, eid);
        EntityRecord entityRecord = new EntityRecord();
        entityRecord.setUid(uid);
        entityRecord.setEid(eid);
        entityRecordDAO.save(entityRecord);
    }

    public void search(int uid, String course, String searchKey) {
        searchRecordDAO.deleteByUidAndCourseAndSearchKey(uid, course, searchKey);
        SearchRecord searchRecord = new SearchRecord();
        searchRecord.setUid(uid);
        searchRecord.setCourse(course);
        searchRecord.setSearchKey(searchKey);
        searchRecordDAO.save(searchRecord);
    }

    public List<Integer> getStarredList(int uid) {
        List<StarRecord> starList = starRecordDAO.getByUid(uid);
        List<Integer> retList = new ArrayList<>();
        for (StarRecord record : starList) {
            retList.add(record.getEid());
        }
        return retList;
    }
}

