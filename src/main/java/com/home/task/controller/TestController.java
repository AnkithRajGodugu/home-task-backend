package com.home.task.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/tasks/test")
    public String test() {
        return "JWT ACCESS SUCCESS";
    }
}
