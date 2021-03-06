package com.giraffe.restservice.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.giraffe.restservice.dao.MyEntityDAO;
import com.giraffe.restservice.dao.UserDAO;
import com.giraffe.restservice.pojo.MyEntity;
import com.giraffe.restservice.pojo.User;
import com.giraffe.restservice.service.JsonService;
import com.giraffe.restservice.service.RecordService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CourseController {
    private UserDAO userDAO;
    private JsonService jsonService;
    private MyEntityDAO entityDAO;
    private RecordService recordService;

    @Autowired
    CourseController(UserDAO userDAO, JsonService jsonService, MyEntityDAO entityDAO, RecordService recordService) {
        this.userDAO = userDAO;
        this.jsonService = jsonService;
        this.entityDAO = entityDAO;
        this.recordService = recordService;
    }

    @GetMapping("/api/course/getlist")
    public String getList(HttpServletRequest request) {
        HashMap<String, Object> ret = new HashMap<String, Object>();

        int id = (int) request.getAttribute("id");
        User user = userDAO.findById(id).get();

        ret.put("result", "succeed");
        ret.put("errorMsg", "");
        ret.put("courseList", jsonService.toList(user.getCourseListString()));

        return jsonService.writeString(ret);
    }

    @PostMapping("/api/course/postlist")
    public String postList(HttpServletRequest request, @RequestParam String courseListString) {
        HashMap<String, Object> ret = new HashMap<String, Object>();

        int id = (int) request.getAttribute("id");
        User user = userDAO.findById(id).get();
        user.setCourseListString(courseListString);
        userDAO.save(user);

        ret.put("result", "succeed");
        ret.put("errorMsg", "????????????");

        return jsonService.writeString(ret);
    }

    @GetMapping("/api/course/entity/count")
    public String countEntity(HttpServletRequest request, @RequestParam String course) {
        HashMap<String, Object> ret = new HashMap<String, Object>();
        ret.put("result", "succeed");
        ret.put("errorMsg", "");
        ret.put("number", entityDAO.getEntityByCourse(course).size());
        return jsonService.writeString(ret);
    }

    @GetMapping("/api/course/entity/list")
    public String listEntity(HttpServletRequest request, @RequestParam String course, @RequestParam int num,
            @RequestParam int page) {
        int uid = (int) request.getAttribute("id");
        HashMap<String, Object> ret = new HashMap<String, Object>();
        List<MyEntity> list = entityDAO.getEntityByCourse(course);

        if (list.size() > 0) {
            if (num * page < list.size()) {
                list = list.subList(num * (page - 1), num * page);
            } else {
                list = list.subList(num * (page - 1), list.size() - 1);
            }
        }

        ArrayList<Object> entityList = new ArrayList<>();
        for (MyEntity item : list) {
            HashMap<String, Object> entity = new HashMap<String, Object>();
            String label = item.getLabel();
            entity.put("label", label);
            entity.put("star", recordService.isStarred(uid, course, label));
            entityList.add(entity);
        }

        ret.put("entityList", entityList);
        ret.put("result", "succeed");
        ret.put("errorMsg", "");
        return jsonService.writeString(ret);
    }
}
