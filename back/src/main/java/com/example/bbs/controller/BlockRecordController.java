package com.example.bbs.controller;

import com.example.bbs.pojo.vo.BlockRecordVO;
import com.example.bbs.result.CommonResult;
import com.example.bbs.service.BlockRecordService;
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
@RequestMapping("/blockRecord")
@Api(tags = "拉黑相关操作")
public class BlockRecordController {
    @Autowired
    BlockRecordService blockRecordService;


    @ApiOperation("用户拉黑操作")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "举报人id"),
            @ApiImplicitParam(name = "targetId", value = "目标id"),
    })
    @PostMapping("/addBlockRecord")
    public CommonResult addBlockRecord(String userId,String targetId) {
        try {
            if(userId==null || targetId==null){
                log.info("id异常，拉黑失败！");
                return CommonResult.failed("id异常，拉黑失败！");
            }
            //添加拉黑记录
            blockRecordService.createBlockRecord(userId, targetId);
            return CommonResult.success("拉黑成功！");
        } catch (Exception e) {
            log.info(e.getMessage());
            return CommonResult.failed(e.getMessage());
        }
    }

    @ApiOperation("用户修改拉黑记录状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "拉黑记录主键id"),
            @ApiImplicitParam(name = "targetState", value = "拉黑状态"),
    })
    @PostMapping("/editBlockRecord")
    public CommonResult editBlockRecord(String id,int targetState) {
        try {
            //添加拉黑记录
            blockRecordService.updateBlockRecord(id, targetState);
            return CommonResult.success("修改拉黑记录状态成功！");
        } catch (Exception e) {
            log.info(e.getMessage());
            return CommonResult.failed(e.getMessage());
        }
    }

    @ApiOperation("用户拉黑操作")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "举报人id"),
            @ApiImplicitParam(name = "targetId", value = "目标id"),
            @ApiImplicitParam(name = "targetState", value = "拉黑状态"),
    })
    @PostMapping("/editBlockRecordByUserId")
    public CommonResult editBlockRecordByUserId(String userId,String targetId,int targetState) {
        try {
            //添加拉黑记录
            int result = blockRecordService.updateBlockRecordByUserId(userId, targetId,targetState);
            if (result==1){
                return CommonResult.success("拉黑成功！");
            }
            else{
                return CommonResult.failed("修改拉黑状态失败");
            }
        } catch (Exception e) {
            log.info(e.getMessage());
            return CommonResult.failed(e.getMessage());
        }
    }

    @ApiOperation("获取用户拉黑记录列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "当前浏览用户id"),
            @ApiImplicitParam(name = "pageNum", value = "当前页数", required = true),
            @ApiImplicitParam(name = "pageSize", value = "页面显示条数", required = true),
    })
    @GetMapping("/getBlockRecordList")
    public CommonResult getBlockRecordList(String userId, int pageNum, int pageSize) {
        try {
            List<BlockRecordVO> list;
            list = blockRecordService.getUserBlockList(userId,pageNum,pageSize);
            //log.info("首页内容列表获取成功！");
            return CommonResult.success("用户拉黑列表获取成功！", list);
        } catch (Exception e) {
            log.info("用户拉黑列表获取失败！");
            log.info(e.getMessage());
            return CommonResult.failed(e.getMessage());
        }
    }

    @ApiOperation("判断是否被查询用户拉黑")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "当前用户id"),
            @ApiImplicitParam(name = "searchUserId", value = "查询用户id"),
    })
    @PostMapping("/judgeHaveBeBlocked")
    public CommonResult judgeHaveBeBlocked(String userId,String searchUserId) {
        try {
            //判断是否被查询用户拉黑
            int result = blockRecordService.judgeBeBlockedByUser(userId, searchUserId);
            return CommonResult.success("是否被拉黑",result);
        } catch (Exception e) {
            log.info(e.getMessage());
            return CommonResult.failed(e.getMessage());
        }
    }
}
