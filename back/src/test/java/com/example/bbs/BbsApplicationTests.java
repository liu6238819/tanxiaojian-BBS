package com.example.bbs;

import com.example.bbs.mapper.ContentMapper;
import com.example.bbs.service.CommentService;
import com.example.bbs.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BbsApplicationTests {
    @Autowired
    UserService userService;
    @Autowired
    ContentMapper contentMapper;
    @Autowired
    CommentService commentService;

    @Test
    void contextLoads() {
        commentService.updateCommentType("1",1);
    }

}
