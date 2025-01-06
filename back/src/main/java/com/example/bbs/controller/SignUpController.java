package com.example.bbs.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.bbs.mapper.SignMapper;
import com.example.bbs.mapper.SignUpInfoMapper;
import com.example.bbs.pojo.SignUpActivity;
import com.example.bbs.pojo.SignUpInfo;
import com.example.bbs.pojo.TaskOrder;
import com.example.bbs.pojo.vo.SignUpActivityVO;
import com.example.bbs.pojo.vo.TaskOrderVO;
import com.example.bbs.result.CommonResult;
import com.example.bbs.service.SignUpService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/signUp")
@Api(tags = "报名相关操作")
public class SignUpController {
    @Autowired
    SignUpService signUpService;

    @Autowired
    SignUpInfoMapper signUpInfoMapper;

    @ApiOperation("创建报名活动")
    @PostMapping("/createSignUpActivity")
    public CommonResult createSignUpActivity(@RequestBody SignUpActivityVO activityVO) {
        try {
            int result = signUpService.createSignUpActivity(activityVO);
            if (result < 0) return CommonResult.failed("活动创建失败");
            return CommonResult.success("活动创建成功！");
        } catch (Exception e) {
            log.warn("活动创建失败！活动信息：" + e);
            return CommonResult.failed("创建失败，接口异常！");
        }
    }

    @ApiOperation("获取活动列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "当前浏览用户id", required = true),
            @ApiImplicitParam(name = "ownerId", value = "拥有者id"),
            @ApiImplicitParam(name = "kind", value = "类型"),
            @ApiImplicitParam(name = "state", value = "状态"),
            @ApiImplicitParam(name = "schoolId", value = "所属学校"),
            @ApiImplicitParam(name = "alumniOnly", value = "仅校友可见"),
            @ApiImplicitParam(name = "pageNum", value = "当前页数", required = true),
            @ApiImplicitParam(name = "pageSize", value = "页面显示条数", required = true),

    })
    @GetMapping("/getActivityList")
    public CommonResult getActivityList(String userId,String ownerId, @RequestParam(defaultValue = "-1") int kind, @RequestParam(defaultValue = "-1") int state, @RequestParam(defaultValue = "-1") int schoolId, @RequestParam(defaultValue = "-1") int alumniOnly, int pageNum, int pageSize) {
        if (StringUtils.isBlank(userId)) {
            return CommonResult.failed("非法用户访问");
        }
        try {
            List<SignUpActivityVO> result = null;
            result = signUpService.getActivityList(ownerId,kind, state, schoolId, alumniOnly, pageNum, pageSize);
            return CommonResult.success("活动列表获取成功！", result);
        } catch (Exception e) {
            log.warn("申请失败！：" + e.getMessage());
            return CommonResult.failed("活动列表获取失败，接口异常！");
        }
    }

    @ApiOperation("获取报名信息")
    @GetMapping("/getSignUpInfo")
    public CommonResult getSignUpInfo(String activityId, String userId, String batchNumber) {
        try {
            JSONObject result = null;
            result = signUpService.getSignUpInfo(activityId, userId, batchNumber);
            return CommonResult.success("报名信息获取成功！", result);
        } catch (Exception e) {
            log.warn("申请失败！：" + e.getMessage());
            return CommonResult.failed("报名信息获取失败，接口异常！");
        }
    }

    @ApiOperation("获取报名列表")
    @GetMapping("/getSignUpList")
    public CommonResult getSignUpList(String activityId, String batchNumber) {
        try {
            List<SignUpInfo> result = null;
            result = signUpService.getSignUpList(activityId, batchNumber);
            return CommonResult.success("报名信息获取成功！", result);
        } catch (Exception e) {
            log.warn("申请失败！：" + e.getMessage());
            return CommonResult.failed("报名信息获取失败，接口异常！");
        }
    }

    @ApiOperation("获取报名数量")
    @GetMapping("/getSignUpNum")
    public CommonResult getSignUpNum(String activityId ,String batchNumber) {
        try {
            int result = -1;
            result = signUpService.getSignUpList(activityId, batchNumber).size();
            return CommonResult.success("报名列表获取成功！", result);
        } catch (Exception e) {
            log.warn("申请失败！：" + e.getMessage());
            return CommonResult.failed("报名列表获取失败，接口异常！");
        }
    }

    @ApiOperation("增加报名信息")
    @PostMapping("/addSignUpInfo")
    public CommonResult addSignUpInfo(@RequestBody SignUpInfo signUpInfo) {
        try {
            JSONObject result = signUpService.addSignUpInfo(signUpInfo);
            if (result.getInteger("result") == 2) return CommonResult.failed("申请失败，活动不存在！");
            if (result.getInteger("result") == 3) return CommonResult.failed("申请失败，报名人数已满！");
            return CommonResult.success("报名成功！", result.get("SignUpInfo"));
        } catch (Exception e) {
            log.warn("申请失败！：" + e.getMessage());
            return CommonResult.failed("创建失败，接口异常！");
        }
    }

    @ApiOperation("支付报名完成")
    @PostMapping("/payedSignUp")
    public CommonResult payedSignUp(@RequestBody SignUpInfo signUpInfo) {
        if (StringUtils.isBlank(signUpInfo.getUserId())) {
            return CommonResult.failed("非法用户访问");
        }
        if (!signUpInfo.getActivityId().equals("fromWX")) {
            log.warn("非法请求来源！");
            return CommonResult.failed("来源异常");
        }
        try {
            signUpService.payedSignUp(signUpInfo);
            return CommonResult.success("订单状态修改成功！");
        } catch (Exception e) {
            log.warn("订单状态修改失败！");
            return CommonResult.failed(e.getMessage());
        }
    }

    @ApiOperation("修改报名信息")
    @PostMapping("/updateSignUpInfo")
    public CommonResult updateSignUpInfo(@RequestBody SignUpInfo signUpInfo) {
        try {
            int result = signUpInfoMapper.updateById(signUpInfo);
            return CommonResult.success("报名信息修改成功！", result);
        } catch (Exception e) {
            log.warn("申请失败！：" + e.getMessage());
            return CommonResult.failed("创建失败，接口异常！");
        }
    }
}
