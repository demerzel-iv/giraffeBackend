package com.giraffe.restservice.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import com.giraffe.restservice.dao.EntityRecordDAO;
import com.giraffe.restservice.dao.MyEntityDAO;
import com.giraffe.restservice.dao.SearchRecordDAO;
import com.giraffe.restservice.dao.StarRecordDAO;
import com.giraffe.restservice.pojo.EntityRecord;
import com.giraffe.restservice.pojo.MyEntity;
import com.giraffe.restservice.pojo.SearchRecord;
import com.giraffe.restservice.pojo.StarRecord;
import com.giraffe.restservice.service.JsonService;
import com.giraffe.restservice.service.RecordService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;

@Transactional
@RestController
public class RecordController {
    private JsonService jsonService;
    private RecordService recordService;
    private MyEntityDAO myEntityDAO;
    private StarRecordDAO starRecordDAO;
    private EntityRecordDAO entityRecordDAO;
    private SearchRecordDAO searchRecordDAO;

    @Autowired
    RecordController(JsonService jsonService, RecordService recordService,MyEntityDAO myEntityDAO, StarRecordDAO starRecordDAO,
            EntityRecordDAO entityRecordDAO, SearchRecordDAO searchRecordDAO) {
        this.jsonService = jsonService;
        this.recordService = recordService;
        this.myEntityDAO = myEntityDAO;
        this.starRecordDAO = starRecordDAO;
        this.entityRecordDAO = entityRecordDAO;
        this.searchRecordDAO = searchRecordDAO;
    }

    @PostMapping("/api/entity/star")
    public String starEntity(HttpServletRequest request, @RequestParam String course, @RequestParam String label) {
        int uid = (int) request.getAttribute("id");
        recordService.star(uid, course, label);

        HashMap<String, Object> ret = new HashMap<>();
        ret.put("result", "succeed");
        ret.put("errorMsg", "");
        return jsonService.writeString(ret);
    }

    @PostMapping("/api/entity/unstar")
    public String unstarEntity(HttpServletRequest request, @RequestParam String course, @RequestParam String label) {
        int uid = (int) request.getAttribute("id");
        int eid = recordService.getEidByCourseLabel(course, label);

        starRecordDAO.deleteByUidAndEid(uid, eid);

        HashMap<String, Object> ret = new HashMap<>();
        ret.put("result", "succeed");
        ret.put("errorMsg", "");
        return jsonService.writeString(ret);
    }

    @GetMapping("/api/entity/liststar/count")
    public String countStarList(HttpServletRequest request) {
        HashMap<String, Object> ret = new HashMap<>();
        int uid = (int) request.getAttribute("id");
        List<StarRecord> list = starRecordDAO.getByUid(uid);

        ret.put("result", "succeed");
        ret.put("errorMsg", "");
        ret.put("number", list.size());
        return jsonService.writeString(ret);
    }

    @GetMapping("/api/entity/liststar")
    public String getStarList(HttpServletRequest request, @RequestParam int num, @RequestParam int page) {
        HashMap<String, Object> ret = new HashMap<>();
        int uid = (int) request.getAttribute("id");
        List<StarRecord> list = starRecordDAO.getByUid(uid);
        List<MyEntity> entityList = new ArrayList<>();

        for (StarRecord star : list) {
            int eid = star.getEid();
            System.out.println(eid);
            try {
                entityList.add(myEntityDAO.findById(eid).get());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        if (entityList.size() > 0) {
            if (num * page < entityList.size()) {
                entityList = entityList.subList(num * (page - 1), num * page);
            } else {
                entityList = entityList.subList(num * (page - 1), entityList.size());
            }
        }

        ret.put("result", "succeed");
        ret.put("errorMsg", "");
        ret.put("entityList", entityList);
        return jsonService.writeString(ret);
    }

    @GetMapping("/api/history/visit")
    public String visitHistory(HttpServletRequest request) {
        HashMap<String, Object> ret = new HashMap<>();
        int uid = (int) request.getAttribute("id");
        List<EntityRecord> list = entityRecordDAO.getByUid(uid);
        List<HashMap<String, Object>> retList = new ArrayList<>();

        for (EntityRecord star : list) {
            HashMap<String, Object> item = new HashMap<>();
            int eid = star.getEid();
            MyEntity entity = myEntityDAO.findById(eid).get();
            item.put("timestamp", star.getTimestamp().getTime());
            item.put("course", entity.getCourse());
            item.put("label", entity.getLabel());
            retList.add(item);
        }

        ret.put("result", "succeed");
        ret.put("errorMsg", "");
        ret.put("visitHistory", retList);
        return jsonService.writeString(ret);
    }

    @PostMapping("/api/history/visit/delete")
    public String deleteVisitHistory(HttpServletRequest request, @RequestParam String course, @RequestParam String label) {
        HashMap<String, Object> ret = new HashMap<>();
        int uid = (int) request.getAttribute("id");
        int eid = recordService.getEidByCourseLabel(course, label);

        entityRecordDAO.deleteByUidAndEid(uid, eid);

        ret.put("result", "succeed");
        ret.put("errorMsg", "");
        return jsonService.writeString(ret);
    }

    @GetMapping("/api/history/search")
    public String searchHistory(HttpServletRequest request, @RequestParam String course) {
        HashMap<String, Object> ret = new HashMap<>();
        int uid = (int) request.getAttribute("id");
        List<SearchRecord> list = searchRecordDAO.getByUid(uid);
        List<String> retList = new ArrayList<>();

        for (SearchRecord record : list) {
            if (record.getCourse().equals(course)) {
                retList.add(record.getSearchKey());
            }
        }

        ret.put("result", "succeed");
        ret.put("errorMsg", "");
        ret.put("searchHistory", retList);
        return jsonService.writeString(ret);
    }

    @PostMapping("/api/history/search/delete")
    public String deleteSearchHistory(HttpServletRequest request, @RequestParam String course, @RequestParam String searchKey) {
        HashMap<String, Object> ret = new HashMap<>();
        int uid = (int) request.getAttribute("id");

        searchRecordDAO.deleteByUidAndCourseAndSearchKey(uid, course, searchKey);

        ret.put("result", "succeed");
        ret.put("errorMsg", "");
        return jsonService.writeString(ret);
    }
}
