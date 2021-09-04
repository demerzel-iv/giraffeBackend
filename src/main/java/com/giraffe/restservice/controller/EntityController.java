package com.giraffe.restservice.controller;

import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.giraffe.restservice.dao.EntityRecordDAO;
import com.giraffe.restservice.dao.MyEntityDAO;
import com.giraffe.restservice.dao.SearchRecordDAO;
import com.giraffe.restservice.exception.NetworkRequestFailedException;
import com.giraffe.restservice.pojo.EntityRecord;
import com.giraffe.restservice.pojo.MyEntity;
import com.giraffe.restservice.pojo.SearchRecord;
import com.giraffe.restservice.service.JsonService;
import com.giraffe.restservice.service.NetworkService;
import com.giraffe.restservice.service.ProblemService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@Transactional
public class EntityController {
    NetworkService networkService;
    JsonService jsonService;
    ProblemService problemService;
    MyEntityDAO myEntityDAO;
    EntityRecordDAO entityRecordDAO;
    SearchRecordDAO searchRecordDAO;

    @Autowired
    EntityController(NetworkService networkService, JsonService jsonService, ProblemService problemService,
            MyEntityDAO myEntityDAO, EntityRecordDAO entityRecordDAO, SearchRecordDAO searchRecordDAO) {
        this.networkService = networkService;
        this.jsonService = jsonService;
        this.problemService = problemService;
        this.myEntityDAO = myEntityDAO;
        this.entityRecordDAO = entityRecordDAO;
        this.searchRecordDAO = searchRecordDAO;
    }

    private int getEidByCourseLabel(String course, String label) {
        if (!myEntityDAO.existsByCourseAndLabel(course, label)) {
            MyEntity entity = new MyEntity();
            entity.setCourse(course);
            entity.setLabel(label);
            myEntityDAO.save(entity);
        }
        MyEntity entity = myEntityDAO.findByCourseAndLabel(course, label);
        return entity.getId();
    }

    @GetMapping("/api/entity/search")
    public String searchEntity(HttpServletRequest request, @RequestParam String course,
            @RequestParam String searchKey) {
        HashMap<String, Object> ret = new HashMap<String, Object>();

        try {
            JsonNode tree = jsonService.readTree(networkService.searchEntity(course, searchKey));
            ArrayList<Object> entityList = new ArrayList<>();
            for (int i = 0; i < tree.get("data").size(); i++) {
                HashMap<String, Object> entity = new HashMap<String, Object>();
                entity.put("label", tree.get("data").get(i).get("label").asText());
                entity.put("category", tree.get("data").get(i).get("category").asText());
                entityList.add(entity);
            }
            ret.put("result", "succeed");
            ret.put("errorMsg", "");
            ret.put("entityList", entityList);
        } catch (NetworkRequestFailedException e) {
            ret.put("result", "failed");
            ret.put("errorMsg", "服务器网络错误");
        }

        int uid = (int) request.getAttribute("id");
        searchRecordDAO.deleteByUidAndCourseAndSearchKey(uid, course, searchKey);
        SearchRecord searchRecord = new SearchRecord();
        searchRecord.setUid(uid);
        searchRecord.setCourse(course);
        searchRecord.setSearchKey(searchKey);
        searchRecordDAO.save(searchRecord);

        return jsonService.writeString(ret);
    }

    @GetMapping("/api/entity/info")
    public String getEntityInfo(HttpServletRequest request, @RequestParam String course, @RequestParam String label) {
        HashMap<String, Object> ret = new HashMap<String, Object>();
        try {
            JsonNode tree = jsonService.readTree(networkService.getEntityInfo(course, label));
            ret.put("result", "succeed");
            ret.put("errorMsg", "");
            ret.put("label", label);

            ArrayList<Object> knowledgeCard = new ArrayList<>();
            JsonNode propertyList = tree.get("data").get("property");
            for (int i = 0; i < propertyList.size(); i++) {
                if (!propertyList.get(i).get("object").asText().startsWith("http")) {
                    HashMap<String, Object> property = new HashMap<String, Object>();
                    property.put("property", propertyList.get(i).get("predicateLabel").asText());
                    property.put("content", propertyList.get(i).get("object").asText());
                    knowledgeCard.add(property);
                }
            }
            ret.put("knowledgeCard", knowledgeCard);

            ArrayList<Object> content = new ArrayList<>();
            JsonNode contentList = tree.get("data").get("content");
            for (int i = 0; i < contentList.size(); i++) {
                HashMap<String, Object> relation = new HashMap<String, Object>();
                relation.put("predicateLabel", contentList.get(i).get("predicate_label"));
                if (contentList.get(i).has("object_label")) {
                    relation.put("type", "object");
                    relation.put("label", contentList.get(i).get("object_label").asText());
                } else {
                    relation.put("type", "subject");
                    relation.put("label", contentList.get(i).get("subject_label").asText());
                }
                content.add(relation);
            }
            ret.put("content", content);

            //ret.put("problemList", problemService.getProblemList(label));

        } catch (NetworkRequestFailedException e) {
            ret.put("result", "failed");
            ret.put("errorMsg", "服务器网络错误");
        }

        int uid = (int) request.getAttribute("id");
        int eid = getEidByCourseLabel(course, label);
        entityRecordDAO.deleteByUidAndEid(uid, eid);
        EntityRecord entityRecord = new EntityRecord();
        entityRecord.setUid(uid);
        entityRecord.setEid(eid);
        entityRecordDAO.save(entityRecord);

        return jsonService.writeString(ret);
    }

}
