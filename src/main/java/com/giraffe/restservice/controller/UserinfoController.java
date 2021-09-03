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
public class UserinfoController {
    UserDAO userDAO;
    JsonService jsonService;

    @Autowired
    UserinfoController(UserDAO userDAO, JsonService jsonService) {
        this.userDAO = userDAO;
        this.jsonService = jsonService;
    }

    @GetMapping("/api/userinfo")
    public String getUserinfo(HttpServletRequest request) {
        HashMap<String, Object> ret = new HashMap<>();
        int id = (int) request.getAttribute("id");
        User user = userDAO.findById(id).get();
        ret.put("result", "succeed");
        ret.put("errorMsg", "");
        ret.put("name", user.getName());
        ret.put("gender", user.getGender());
        return jsonService.writeString(ret);
    }

    @PostMapping("/api/userinfo/update")
    public String updateUserinfo(HttpServletRequest request, @RequestParam String password, @RequestParam String name,
            @RequestParam String gender) {
        HashMap<String, Object> ret = new HashMap<>();
        int id = (int) request.getAttribute("id");
        User user = userDAO.findById(id).get();
        user.setPassword(password);
        user.setName(name);
        user.setGender(gender);
        userDAO.save(user);
        ret.put("result", "succeed");
        ret.put("errorMsg", "");
        return jsonService.writeString(ret);
    }
}
