package com.giraffe.restservice.controller;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.giraffe.restservice.dao.UserDAO;
import com.giraffe.restservice.pojo.User;
import com.giraffe.restservice.service.JsonService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CourseController {
    private UserDAO userDAO;
    private JsonService jsonService;
    
    @Autowired
    CourseController(UserDAO userDAO, JsonService jsonService) {
        this.userDAO = userDAO;
        this.jsonService = jsonService;
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
    public String postList(HttpServletRequest request, @RequestParam(value = "courseList") String courseListString) {
        HashMap<String, Object> ret = new HashMap<String, Object>();

        int id = (int) request.getAttribute("id");
        User user = userDAO.findById(id).get();
        user.setCourseListString(courseListString);
        userDAO.save(user);

        ret.put("result", "succeed");
        ret.put("errorMsg", "修改成功");

        return jsonService.writeString(ret);
    }
}
