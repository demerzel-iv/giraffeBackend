package com.giraffe.restservice.controller;

import java.util.ArrayList;
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
public class EntityLinkController {
    NetworkService networkService;
    JsonService jsonService;

    @Autowired
    EntityLinkController(NetworkService networkService, JsonService jsonService) {
        this.networkService = networkService;
        this.jsonService = jsonService;
    }

    @PostMapping("/api/link")
    public String entityLink(@RequestParam String course, @RequestParam String context) {
        HashMap<String, Object> ret = new HashMap<String, Object>();
        try {
            JsonNode tree = jsonService.readTree(networkService.entityLink(course, context));
            tree = tree.get("data").get("results");
            ret.put("result", "succeed");
            ret.put("errorMsg", "");
            ArrayList<Object> list = new ArrayList<>();
            for (int i = 0; i < tree.size(); i++) {
                HashMap<String, Object> item = new HashMap<>();
                item.put("label", tree.get(i).get("entity").asText());
                item.put("startIndex", tree.get(i).get("start_index").asInt());
                item.put("endIndex", tree.get(i).get("end_index").asInt() + 1);
                list.add(item);
            }
            ret.put("data", list);
        } catch (NetworkRequestFailedException e) {
            ret.put("result", "failed");
            ret.put("errorMsg", "服务器网络错误");
        }
        return jsonService.writeString(ret);
    }
}
