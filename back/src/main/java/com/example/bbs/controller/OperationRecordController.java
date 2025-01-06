package com.example.bbs.controller;

import com.example.bbs.pojo.OperationRecord;
import com.example.bbs.pojo.User;
import com.example.bbs.result.CommonResult;
import com.example.bbs.service.CommentService;
import com.example.bbs.service.ContentService;
import com.example.bbs.service.OperationRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/OperationRecord")
@Api(tags = "操作记录相关操作")
public class OperationRecordController {

    @Autowired
    OperationRecordService operationRecordService;


    @ApiOperation("判断是否到达解除禁言时间")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id"),
            @ApiImplicitParam(name = "schoolId", value = "学校id"),
    })
    @GetMapping("/isReachBannedTime")
    public CommonResult isReachBannedTime(String userId,int schoolId) {
        try {
            long bannedTimeStamp = operationRecordService.isReachBannedTime(userId,schoolId);
            if(bannedTimeStamp==1){
                return CommonResult.success("解除禁言成功！");
            }
            else{
                return CommonResult.failed("解除禁言失败！");
            }
        } catch (Exception e) {
            log.info(e.getMessage());
            return CommonResult.failed(e.getMessage());
        }
    }

    @ApiOperation("新增操作记录")
    @PostMapping("/addOperationRecord")
    public CommonResult addOperationRecord(OperationRecord operationRecord) {
        if (operationRecordService.addOperationRecord(operationRecord) == 1) {
            return CommonResult.success("操作记录创建成功！");
        } else {
            return CommonResult.failed("操作记录创建失败");
        }
    }

    @ApiOperation("新增操作记录，使用请求体")
    @PostMapping("/addOperationRecord0619")
    public CommonResult addOperationRecord0619( @RequestBody OperationRecord operationRecord) {
        if (operationRecordService.addOperationRecord(operationRecord) == 1) {
            return CommonResult.success("操作记录创建成功！");
        } else {
            return CommonResult.failed("操作记录创建失败");
        }
    }

    @ApiOperation("获取用户禁言记录列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "targetId", value = "目标用户id"),
            @ApiImplicitParam(name = "schoolId", value = "学校id"),
    })
    @GetMapping("/selectBannedList")
    public CommonResult selectBannedList(String targetId, int schoolId) {
        try {
            List<OperationRecord> list;
            list = operationRecordService.selectBannedList(schoolId,targetId);
            return CommonResult.success("用户禁言列表获取成功！", list);
        } catch (Exception e) {
            log.info("用户禁言列表获取失败！");
            log.info(e.getMessage());
            return CommonResult.failed(e.getMessage());
        }
    }


}
