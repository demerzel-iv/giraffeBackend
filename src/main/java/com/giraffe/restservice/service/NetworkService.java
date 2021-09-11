package com.giraffe.restservice.service;

import com.giraffe.restservice.dao.CacheDAO;
import com.giraffe.restservice.exception.NetworkRequestFailedException;
import com.giraffe.restservice.pojo.Cache;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.BaseRequest;
import com.mashape.unirest.request.HttpRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import lombok.Data;

@Service
@Data
@ConfigurationProperties(prefix = "network")
public class NetworkService {
    private CacheDAO cacheDAO;

    @Autowired
    NetworkService(CacheDAO cacheDAO) {
        this.cacheDAO = cacheDAO;
    }

    private final String contentType = "Content-Type";
    private final String form = "application/x-www-form-urlencoded";
    private final int maxRequests = 3;

    private String phone;
    private String password;
    private String id = "";

    private String loginUrl;
    private String searchUrl;
    private String entityInfoUrl;
    private String questionAnswerUrl;
    private String entityLinkUrl;
    private String problemUrl;

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
                        .queryString("searchKey", searchKey.replace(" ", "%20")).queryString("id", id);
                return sendRequest(request);
            } catch (Exception e) {
            }
        }
        throw new NetworkRequestFailedException();
    }

    public String getEntityInfo(String course, String name) throws NetworkRequestFailedException {
        for (int i = 0; i < maxRequests; i++) {
            try {
                HttpRequest request = Unirest.get(entityInfoUrl).queryString("course", course).queryString("name", name.replace(" ", "%20"))
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
                        .field("inputQuestion", inputQuestion.replace(" ", "%20")).field("id", id);
                return sendRequest(request);
            } catch (Exception e) {
            }
        }
        throw new NetworkRequestFailedException();
    }

    public String entityLink(String course, String context) throws NetworkRequestFailedException {
        for (int i = 0; i < maxRequests; i++) {
            try {
                BaseRequest request = Unirest.post(entityLinkUrl).field("course", course).field("context", context.replace(" ", "%20"))
                        .field("id", id);
                return sendRequest(request);
            } catch (Exception e) {
            }
        }
        throw new NetworkRequestFailedException();
    }

    public String problemList(String name) throws NetworkRequestFailedException {
        if (cacheDAO.existsBySearchKey(name)) {
            return cacheDAO.findBySearchKey(name).getResult();
        }

        for (int i = 0; i < maxRequests; i++) {
            try {
                BaseRequest request = Unirest.get(problemUrl).queryString("uriName", name.replace(" ", "%20")).queryString("id", id);
                String result = sendRequest(request);
                Cache cache = new Cache();
                cache.setSearchKey(name);
                cache.setResult(result);
                cacheDAO.save(cache);
                return result;
            } catch (Exception e) {
            }
        }
        throw new NetworkRequestFailedException();
    }
}
