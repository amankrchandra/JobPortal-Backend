package com.zidio.jobportal.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {


    @GetMapping("/hello")
    public String sayHello() {
        return "Hello from Zidio Backend to Postman!";
    }
}
