package com.example.DriverApp.Utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.json.JsonMapper;


public class Mapper {
    private static Logger log = LoggerFactory.getLogger(Mapper.class);
    private static JsonMapper jsonMapper = new JsonMapper();

    public static <T> T stringToClass(String classString, Class<T> classValue){
        T t = null;
        try {
            t = jsonMapper.readValue(classString, classValue);
        } catch (Exception e) {
            log.error("Error deserializing String to class: {}", e.getLocalizedMessage());
        }
        return t;
    }
}
