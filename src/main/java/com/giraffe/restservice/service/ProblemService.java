package com.giraffe.restservice.service;

import java.util.ArrayList;
import java.util.HashMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.giraffe.restservice.exception.NetworkRequestFailedException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProblemService {
    NetworkService networkService;
    JsonService jsonService;

    @Autowired
    ProblemService(NetworkService networkService, JsonService jsonService) {
        this.networkService = networkService;
        this.jsonService = jsonService;
    }

    private HashMap<String, Object> processQBody(String qBody) {
        final String opts = "ABCDEF";

        try {
            qBody = qBody.replace("．", ".");
            HashMap<String, Object> ret = new HashMap<>();
            ArrayList<Integer> dividePos = new ArrayList<>();
            for (int i = 0; i < 6; i++) {
                int ind = qBody.indexOf(opts.substring(i, i + 1) + ".");
                if (ind != -1) {
                    dividePos.add(ind);
                }
            }

            ArrayList<String> optionList = new ArrayList<>();
            ret.put("queryBody", qBody.substring(0, dividePos.get(0)));
            for (int i = 0; i < dividePos.size() - 1; i++) {
                optionList.add(qBody.substring(dividePos.get(i), dividePos.get(i + 1)));
            }
            optionList.add(qBody.substring(dividePos.get(dividePos.size() - 1)));
            ret.put("options", optionList);

            return ret;
        } catch (Exception e) {
            System.err.println("processing qBody error : " + qBody);
            return null;
        }
    }

    public ArrayList<Object> getProblemList(String label) throws NetworkRequestFailedException {
        ArrayList<Object> list = new ArrayList<>();
        JsonNode tree = jsonService.readTree(networkService.problemList(label)).get("data");
        for (int i = 0; i < tree.size(); i++) {
            HashMap<String, Object> problem = processQBody(tree.get(i).get("qBody").asText());
            if (problem == null) {
                continue;
            }
            problem.put("answer", tree.get(i).get("qAnswer").asText());
            list.add(problem);
        }
        return list;
    }
}
