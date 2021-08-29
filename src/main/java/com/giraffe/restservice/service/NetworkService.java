package com.giraffe.restservice.service;

import com.giraffe.restservice.exception.NetworkRequestFailedException;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.BaseRequest;
import com.mashape.unirest.request.HttpRequest;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import lombok.Data;

@Service
@Data
@ConfigurationProperties(prefix = "network")
public class NetworkService {
    final String contentType = "Content-Type";
    final String form = "application/x-www-form-urlencoded";
    final int maxRequests = 3;

    String phone;
    String password;
    String id = "";

    String loginUrl;
    String searchUrl;
    String entityInfoUrl;
    String questionAnswerUrl;

    public String sendRequest(BaseRequest request) throws NetworkRequestFailedException, UnirestException {
        JsonNode jsonNode = request.asJson().getBody();

        System.err.println(jsonNode.toString());

        if (jsonNode.getObject().getString("code").equals("0")) {
            return jsonNode.toString();
        } else {
            id = Unirest.post(loginUrl).header(contentType, form).field("password", password).field("phone", phone)
                    .asJson().getBody().getObject().getString("id");
            throw new NetworkRequestFailedException();
        }
    }

    public String searchEntity(String course, String searchKey) throws NetworkRequestFailedException {
        for (int i = 0; i < maxRequests; i++) {
            try {
                HttpRequest request = Unirest.get(searchUrl).queryString("course", course)
                        .queryString("searchKey", searchKey).queryString("id", id);
                return sendRequest(request);
            } catch (Exception e) {
            }
        }
        throw new NetworkRequestFailedException();
    }

    public String getEntityInfo(String course, String name) throws NetworkRequestFailedException {
        for (int i = 0; i < maxRequests; i++) {
            try {
                HttpRequest request = Unirest.get(entityInfoUrl).queryString("course", course).queryString("name", name)
                        .queryString("id", id);
                return sendRequest(request);
            } catch (Exception e) {
            }
        }
        throw new NetworkRequestFailedException();
    }


    public String questionAnswer(String course, String inputQuestion) throws NetworkRequestFailedException {
        for (int i = 0; i < maxRequests; i++) {
            try {
                BaseRequest request = Unirest.post(questionAnswerUrl).field("course", course)
                        .field("inputQuestion", inputQuestion).field("id", id);
                return sendRequest(request);
            } catch (Exception e) {
            }
        }
        throw new NetworkRequestFailedException();
    }

}
