package com.example.bbs.controller;

import com.example.bbs.pojo.Content;
import com.example.bbs.service.CommentService;
import com.example.bbs.service.InformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    CommentService commentService;
    @Autowired
    InformService informService;
    @PostMapping("/test")
    public void test(@RequestBody Content content){
        System.out.println(content);
    }

}
