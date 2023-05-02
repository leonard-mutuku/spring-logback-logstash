/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.logstash.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.LoggerFactory;

/**
 *
 * @author leonard
 */
public class Utils {
    
    public static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(Utils.class);
    
    public static String toJson(Object obj) {
        String jsonStr = "";
        try {
            ObjectMapper mapper = new ObjectMapper();
            jsonStr = mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            LOGGER.error(e.toString());
        }
        return jsonStr;
    }
    
}
