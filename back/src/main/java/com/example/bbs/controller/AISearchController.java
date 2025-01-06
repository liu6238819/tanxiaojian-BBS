package com.example.bbs.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.bbs.auto.PermissionCheck;
import com.example.bbs.mapper.CommentMapper;
import com.example.bbs.mapper.ContentMapper;
import com.example.bbs.pojo.Admire;
import com.example.bbs.pojo.Comment;
import com.example.bbs.pojo.Content;
import com.example.bbs.result.CommonResult;
import com.example.bbs.service.AISearchRecordService;
import com.example.bbs.service.AdmireService;
import com.example.bbs.service.FuzzySearchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/AISearch")
@Api(tags = "点赞相关操作")
public class AISearchController {

    @Autowired
    ContentMapper contentMapper;

    @Autowired
    CommentMapper commentMapper;

    @Autowired
    private FuzzySearchService fuzzySearchService;


    @ApiOperation("AI匹配近似帖子")
    @GetMapping("/AISelectContent")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "text", value = "搜索内容", required = true),
            @ApiImplicitParam(name = "schoolId", value = "学校id", required = true),
            @ApiImplicitParam(name = "userId", value = "userId", required = true),
            @ApiImplicitParam(name = "searchWay", value = "搜索方式", required = true),


    })
    public CommonResult AISelectContent(String text ,int schoolId,String userId,int searchWay) {
        try {
            Map<String, Object> result = fuzzySearchService.getSecondHandsContent(schoolId,text,userId,searchWay);
            return CommonResult.success("获取成功！",result);



        } catch (Exception e) {
            log.warn("更新失败！：" + e.getMessage());
            return CommonResult.failed("更新失败，接口异常！");
        }


    }




}
