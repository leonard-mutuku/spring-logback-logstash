/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.logstash.controller;

import com.github.javafaker.Faker;
import com.logstash.model.User;
import com.logstash.utils.Utils;
import java.util.Locale;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author leonard
 */
@RestController
//@EnableScheduling
public class AppController {
    
    private final Faker faker = new Faker(Locale.getDefault());
    
    public org.slf4j.Logger APP_LOGGER = LoggerFactory.getLogger("APP_LOG");
    
    @GetMapping(path = "/app/test")
    public ResponseEntity getTestLog() {
        User user = new User(faker.name().firstName(), faker.name().lastName(), faker.phoneNumber().phoneNumber(), faker.internet().emailAddress());
        APP_LOGGER.info("test user => " + Utils.toJson(user));
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }
    
//    @Scheduled(fixedDelay = 15000L, initialDelay = 15000L)
//    public void test() {
//        getTestLog();
//    }
    
}
