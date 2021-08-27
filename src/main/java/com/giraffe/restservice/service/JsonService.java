package com.giraffe.restservice.service;

import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Service;

@Service
public class JsonService {
	private ObjectMapper mapper = new ObjectMapper();
    
    public String writeString(Object object) {
		try {
			return mapper.writeValueAsString(object);
		} catch (JsonProcessingException e){
			return "{}";
        }
	}

	public JsonNode readTree(String jsonString){
		try{
		return mapper.readTree(jsonString);
		} catch(Exception e){
			return null;
		}
	}

	public ArrayList<String> toList(String jsonString) {
		try {
			JsonNode jsonNode = mapper.readTree(jsonString);
			ArrayList<String> list = new ArrayList<String>();
			for (int i = 0; i < jsonNode.size(); i++) {
				list.add(jsonNode.get(i).asText());
			}
			return list;
		} catch (Exception e) {
			return new ArrayList<>();
		}
	}
}
