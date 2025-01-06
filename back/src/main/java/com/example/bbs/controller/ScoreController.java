package com.example.bbs.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.bbs.mapper.BbsConfigMapper;
import com.example.bbs.mapper.UserMapper;
import com.example.bbs.pojo.*;
import com.example.bbs.pojo.vo.TaskOrderVO;
import com.example.bbs.result.CommonResult;
import com.example.bbs.service.UserService;
import com.example.bbs.util.HttpMethod;
import com.example.bbs.util.ScoreUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/score")
@Api(tags = "积分相关操作")
public class ScoreController {
    @Autowired
    ScoreUtil scoreUtil;
    @Autowired
    UserMapper userMapper;
    @Autowired
    BbsConfigMapper bbsConfigMapper;
    @Autowired
    UserService userService;
    @ApiOperation("获取今日交互行为数量")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", required = true),

    })
    @GetMapping("/getActionNumToday")
    public CommonResult getActionNumToday(String userId) {
        if (StringUtils.isBlank(userId)) {
            return CommonResult.failed("非法用户访问");
        }
        try {
            JSONObject result = new JSONObject();
            result.put("contentNum", scoreUtil.getActionNum(userId, 1));
            result.put("commentNum", scoreUtil.getActionNum(userId, 2));
            result.put("AdmireNum", scoreUtil.getActionNum(userId, 3));
            log.info("今日交互数获取成功！");
            return CommonResult.success("今日交互数获取成功！", result);
        } catch (Exception e) {
            log.info("今日交互数获取失败！");
            log.info(e.getMessage());
            return CommonResult.failed(e.getMessage());
        }
    }

    @ApiOperation("获取用户积分")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", required = true),

    })
    @GetMapping("/getUserScore")
    public CommonResult getUserScore(String userId) {
        if (StringUtils.isBlank(userId)) {
            return CommonResult.failed("非法用户访问");
        }
        try {
            User user = userService.getUserByUserId(userId);
            if (null == user) return CommonResult.failed("不存在该用户");
            JSONObject result = new JSONObject();
            result.put("userId", user.getUserId());
            result.put("score", user.getScores());
            log.info("用户积分获取成功！");
            return CommonResult.success("用户积分获取成功！", result);
        } catch (Exception e) {
            log.info("用户积分获取失败！");
            log.info(e.getMessage());
            return CommonResult.failed(e.getMessage());
        }
    }

    @ApiOperation("兑换物品")
    @PostMapping("/redeemScore")
    public CommonResult redeemScore(@RequestBody ScoreRecord scoreRecord) {
        try {
            JSONObject result = new JSONObject();
            //除兑换外也临时用于增加分享积分
            int newScore = scoreUtil.changeScore(scoreRecord.getUserId(), 13, scoreRecord.getChangeNum(), scoreRecord.getRelatedId());
            if (newScore == -1) return CommonResult.failed("积分使用失败！");
            result.put("userId", scoreRecord.getUserId());
            result.put("score", newScore);
            return CommonResult.success("积分使用成功！", result);

        } catch (Exception e) {
            log.warn("积分使用失败！：" + e.getMessage());
            return CommonResult.failed("积分使用失败，接口异常！");
        }
    }


    @ApiOperation("修改用户积分")
    @PostMapping("/changeUserScore")
    //
    @ApiImplicitParams({
            @ApiImplicitParam(name = "source", value = "来源", required = true),
            @ApiImplicitParam(name = "userId", value = "用户Id", required = true),
            @ApiImplicitParam(name = "changeType", value = "修改类型", required = true),
            @ApiImplicitParam(name = "changeNum", value = "改变数量", required = true),
    })
    public CommonResult changeUserScore(String source,String userId,int changeType, int changeNum) {
        if (!(source.equals("BBSadmin")||source.equals("payFailed"))) return CommonResult.failed("非法请求！");
        try {
            JSONObject result = new JSONObject();
            int newScore = scoreUtil.changeScore(userId, changeType,changeNum,source);
            if (newScore == -1) return CommonResult.failed("积分改变失败！");
            result.put("userId", userId);
            result.put("score", newScore);
            return CommonResult.success("积分改变成功！", result);

        } catch (Exception e) {
            log.warn("积分改变失败！：" + e.getMessage());
            return CommonResult.failed("积分改变失败，接口异常！");
        }
    }

    @ApiOperation("获取用户积分和匿名开放情况")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", required = true),
            @ApiImplicitParam(name = "schoolId", value = "学校id", required = true),

    })
    @GetMapping("/getUserScoreAndAnonymousState")
    public CommonResult getUserScoreAndAnonymousState(String userId, int schoolId) {
        if (StringUtils.isBlank(userId)) {
            return CommonResult.failed("非法用户访问");
        }
        try {
            User user = userService.getUserByUserId(userId);
            if (null == user) return CommonResult.failed("不存在该用户");
            //获取匿名配置状态
            QueryWrapper<BbsConfig> bbsConfigQueryWrapper = new QueryWrapper<>();
            bbsConfigQueryWrapper
                    .eq("school_id", schoolId)
                    .eq("config_type", "functionConfig");
            BbsConfig bbsConfig = bbsConfigMapper.selectOne(bbsConfigQueryWrapper);
            //json处理
            Object configJson = bbsConfig.getConfigJson();
            JSONObject configJsonObject = JSONObject.parseObject(configJson.toString());
            JSONArray configDataList = configJsonObject.getJSONArray("data");
            int anonymousState =-1;
            //找到配置项中的匿名发帖状态
            for (Object configData: configDataList) {
                JSONObject configDataJO = JSONObject.parseObject(configData.toString());
                if (configDataJO.get("function_name").equals("chatGPT")){
                    anonymousState =configDataJO.getInteger("anonymous_state");
                }
            }
            JSONObject result = new JSONObject();
            result.put("userId", user.getUserId());
            result.put("score", user.getScores());
            result.put("anonymousState", anonymousState);
            log.info("用户积分获取成功！");
            return CommonResult.success("用户积分获取成功！", result);
        } catch (Exception e) {
            log.info("用户积分获取失败！");
            log.info(e.getMessage());
            return CommonResult.failed(e.getMessage());
        }
    }

}
