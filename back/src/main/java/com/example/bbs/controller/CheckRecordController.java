package com.example.bbs.controller;

import com.example.bbs.mapper.ContentMapper;
import com.example.bbs.pojo.Content;
import com.example.bbs.result.CommonResult;
import com.example.bbs.service.CheckRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/checkRecord")
@Api(tags = "审核相关操作")
public class CheckRecordController {
    @Autowired
    CheckRecordService checkRecordService;
    @Autowired
    ContentMapper contentMapper;

    @ApiOperation("AI审核帖子")
    @PostMapping("/AIContentCheck")
    public CommonResult AIContentCheck(@RequestBody Content content) {
        try {

            //审核
            CommonResult result = checkRecordService.AICheckContent(content);
            return result;
        } catch (Exception e) {
            log.info(e.getMessage());
            return CommonResult.failed(e.getMessage());
        }
    }

}
