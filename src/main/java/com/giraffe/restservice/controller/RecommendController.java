package com.giraffe.restservice.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.giraffe.restservice.dao.MyEntityDAO;
import com.giraffe.restservice.pojo.MyEntity;
import com.giraffe.restservice.service.AlgorithmService;
import com.giraffe.restservice.service.JsonService;
import com.giraffe.restservice.service.RecordService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class RecommendController {
    private JsonService jsonService;
    private AlgorithmService algorithmService;
    private RecordService recordService;
    private MyEntityDAO entityDAO;

    @Autowired
    RecommendController(JsonService jsonService, AlgorithmService algorithmService, RecordService recordsService,
            MyEntityDAO entityDAO) {
        this.jsonService = jsonService;
        this.algorithmService = algorithmService;
        this.recordService = recordsService;
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
            ret.put("problemList", list);
        } else {
            ret.put("result", "failed");
            ret.put("errorMsg", "未找到相关知识");
        }

        return jsonService.writeString(ret);
    }


    @GetMapping("/api/entity/problems/recommend")
    public String recommendProblems(HttpServletRequest request) {
        HashMap<String, Object> ret = new HashMap<>();
        int uid = (int) request.getAttribute("id");

        List<MyEntity> entityList = entityDAO.findAllById(recordService.getStarredList(uid));

        List<Object> list = algorithmService.getRelativeProblems(entityList, 10);
        ret.put("result", "succeed");
        ret.put("errorMsg", "");
        ret.put("problemList", list);

        return jsonService.writeString(ret);
    }
}
