package com.giraffe.restservice.service;

import com.giraffe.restservice.exception.NetworkRequestFailedException;
import com.mashape.unirest.http.HttpMethod;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;
import com.mashape.unirest.request.HttpRequestWithBody;

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

    public String sendRequest(HttpRequest request) throws NetworkRequestFailedException, UnirestException {
        if (request.getHttpMethod() == HttpMethod.GET) {
            request.queryString("id", id);
        } else {
            ((HttpRequestWithBody) request).field("id", id);
        }
        JsonNode jsonNode = request.asJson().getBody();

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
                HttpRequest request = Unirest.get(searchUrl).queryString("course", course).queryString("searchKey",
                        searchKey);
                return sendRequest(request);
            } catch (Exception e) {
            }
        }
        throw new NetworkRequestFailedException();
    }

    public String getEntityInfo(String course, String name) throws NetworkRequestFailedException {
        for (int i = 0; i < maxRequests; i++) {
            try {
                HttpRequest request = Unirest.get(entityInfoUrl).queryString("course", course).queryString("name",
                        name);
                return sendRequest(request);
            } catch (Exception e) {
            }
        }
        throw new NetworkRequestFailedException();
    }

}
