package com.giraffe.restservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
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
}
