package com.example.DriverApp.Utility;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mapper {
    private static final Logger log = LoggerFactory.getLogger(Mapper.class);
    private static final ObjectMapper jsonMapper = JsonMapper.builder()
            .addModule(new JavaTimeModule()) 
            .build();

    public static <T> T stringToClass(String classString, Class<T> classValue) {
        T t = null;
        try {
            t = jsonMapper.readValue(classString, classValue);
        } catch (Exception e) {
            log.error("Error deserializing String to class: {}", e.getLocalizedMessage(), e);
        }
        return t;
    }

    public static String classToString(Object obj){
        try{
            return jsonMapper.writeValueAsString(obj);
        }catch(Exception e){
            log.error("Failed to stringify: {}", e.getLocalizedMessage());
            return null;
        }
        
    }
}
