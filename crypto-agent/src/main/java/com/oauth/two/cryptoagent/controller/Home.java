package com.oauth.two.cryptoagent.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping
@RestController
public class Home {

    @GetMapping("/api/home")
    public String home(){
        return "Hi I am Nalin";
    }
}
