package com.giraffe.restservice.controllers;

import java.util.HashMap;

import com.giraffe.restservice.dao.UserDAO;
import com.giraffe.restservice.pojo.User;
import com.giraffe.restservice.service.JsonService;
import com.giraffe.restservice.service.JwtService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {
	private UserDAO userDAO;
	private JsonService jsonService;
	private JwtService jwtService;

	@Autowired
	LoginController(UserDAO userDAO, JsonService jsonService, JwtService jwtService) {
		this.userDAO = userDAO;
		this.jsonService = jsonService;
		this.jwtService = jwtService;
	}

	@PostMapping("/api/auth/register")
	public String register(@RequestParam(value = "phone") String phone,
			@RequestParam(value = "password") String password, @RequestParam(value = "name") String name,
			@RequestParam(value = "gender") String gender) {

		HashMap<String, Object> ret = new HashMap<String, Object>();
		if (userDAO.existsUserByPhone(phone)) {
			ret.put("result", "failed");
			ret.put("errorMsg", "手机号已注册");
		} else {
			User user = new User();
			user.setPhone(phone);
			user.setPassword(password);
			user.setGender(gender);
			try {
				userDAO.save(user);
				ret.put("result", "succeed");
				ret.put("errorMsg", "注册成功");
			} catch (Exception e) {
				ret.put("result", "failed");
				ret.put("errorMsg", e.getMessage());
			}
		}

		return jsonService.writeString(ret);
	}

	@PostMapping("/api/auth/login")
	public String login(@RequestParam(value = "phone") String phone,
			@RequestParam(value = "password") String password) {

		HashMap<String, Object> ret = new HashMap<String, Object>();
		User user = userDAO.findUserByPhone(phone);
		if (user != null) {
			if (user.getPassword().equals(password)) {
				ret.put("result", "succeed");
				ret.put("errorMsg", "登录成功");
				ret.put("authToken", jwtService.idToJwtToken(user.getId()));
			} else {
				ret.put("result", "failed");
				ret.put("errorMsg", "密码错误");
			}
		} else {
			ret.put("result", "failed");
			ret.put("errorMsg", "用户不存在");
		}
		return jsonService.writeString(ret);
	}

}