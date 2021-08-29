package com.giraffe.restservice.controller;

import java.util.HashMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.giraffe.restservice.exception.NetworkRequestFailedException;
import com.giraffe.restservice.service.JsonService;
import com.giraffe.restservice.service.NetworkService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QuestionAnswerController {

    NetworkService networkService;
    JsonService jsonService;

    @Autowired
    QuestionAnswerController(NetworkService networkService, JsonService jsonService) {
        this.networkService = networkService;
        this.jsonService = jsonService;
    }

    @PostMapping("/api/query")
    public String questionAnswer(@RequestParam String course, @RequestParam String inputQuestion) {
        HashMap<String, Object> ret = new HashMap<String, Object>();
        try {
            JsonNode tree = jsonService.readTree(networkService.questionAnswer(course, inputQuestion));
            ret.put("result", "succeed");
            ret.put("errorMsg", "");

            if (tree.get("data").size() == 0) {
                ret.put("answer", "未找到答案");
                ret.put("relativeEntity", "");
            } else {
                ret.put("answer", tree.get("data").get(0).get("value").asText());
                ret.put("relativeEntity", tree.get("data").get(0).get("subject").asText());
            }
        } catch (NetworkRequestFailedException e) {
            ret.put("result", "failed");
            ret.put("errorMsg", "服务器网络错误");
        }
        return jsonService.writeString(ret);
    }
}
