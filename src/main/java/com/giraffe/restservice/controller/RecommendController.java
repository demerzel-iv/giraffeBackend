package com.giraffe.restservice.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.giraffe.restservice.dao.MyEntityDAO;
import com.giraffe.restservice.pojo.MyEntity;
import com.giraffe.restservice.service.AlgorithmService;
import com.giraffe.restservice.service.JsonService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class RecommendController {
    private JsonService jsonService;
    private AlgorithmService algorithmService;
    private MyEntityDAO entityDAO;

    @Autowired
    RecommendController(JsonService jsonService, AlgorithmService algorithmService, MyEntityDAO entityDAO) {
        this.jsonService = jsonService;
        this.algorithmService = algorithmService;
        this.entityDAO = entityDAO;
    }

    @GetMapping("/api/entity/relative")
    public String getRelativeEntity(@RequestParam String course, @RequestParam String label,
            @RequestParam(defaultValue = "20") int limit) {
        HashMap<String,Object> ret = new HashMap<>();
        MyEntity entity = entityDAO.findByCourseAndLabel(course, label);

        if (entity != null) {
            List<MyEntity> list = algorithmService.getRelativeEntities(Arrays.asList(entity), limit);

            ret.put("result", "succeed");
            ret.put("errorMsg", "");
            ret.put("entityList", list);
        } else {
            ret.put("result", "failed");
            ret.put("errorMsg", "未找到相关知识");
        }

        return jsonService.writeString(ret);
    }

    @GetMapping("/api/entity/problems/relative")
    public String getRelativeProblems(@RequestParam String course, @RequestParam String label,
            @RequestParam(defaultValue = "5") int limit) {
        HashMap<String,Object> ret = new HashMap<>();
        MyEntity entity = entityDAO.findByCourseAndLabel(course, label);

        if (entity != null) {
            List<Object> list = algorithmService.getRelativeProblems(Arrays.asList(entity), limit);

            ret.put("result", "succeed");
            ret.put("errorMsg", "");
            ret.put("entityList", list);
        } else {
            ret.put("result", "failed");
            ret.put("errorMsg", "未找到相关知识");
        }

        return jsonService.writeString(ret);
    }

}
