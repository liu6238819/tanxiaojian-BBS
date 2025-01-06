package com.example.bbs.controller;


import com.alibaba.fastjson.JSONObject;
import com.example.bbs.pojo.AdvSignUp;
import com.example.bbs.pojo.Advertisement;
import com.example.bbs.result.CommonResult;
import com.example.bbs.service.AdvertisementService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/advertisement")
@Api(tags = "广告相关操作")
@EnableCaching
public class AdvertisementController {

    @Autowired
    AdvertisementService advertisementService;

    @ApiOperation("创建广告")
    @PostMapping("/createAd")
    public CommonResult createAdv(@RequestBody Advertisement advertisement) {
        try {
            int result = advertisementService.createOneAdv(advertisement);
            if (result < 0) return CommonResult.failed("广告创建失败");
            return CommonResult.success("广告创建成功！");
        } catch (Exception e) {
            log.warn("广告创建失败！广告信息：" + e);
            return CommonResult.failed("创建失败，接口异常！");
        }
    }

    @ApiOperation("获取广告列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "schoolId", value = "学校id", required = true),
            @ApiImplicitParam(name = "advPosition", value = "广告所在位置"),
    })
    @GetMapping("/getAdvList")
    public CommonResult getAdvList( @RequestParam(defaultValue = "0") int schoolId, String advPosition) {
        try {
            List<Advertisement> list = advertisementService.getAdvList(schoolId,advPosition);
            log.info("广告列表获取成功！");
            return CommonResult.success("广告列表获取成功！", list);
        } catch (Exception e) {
            log.info("广告列表获取失败！");
            log.info(e.getMessage());
            return CommonResult.failed(e.getMessage());
        }
    }

    @ApiOperation("获取广告列表0507版")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "schoolId", value = "学校id", required = true),
            @ApiImplicitParam(name = "advPosition", value = "广告所在位置"),
    })
    @GetMapping("/getAdvList0507")
    public CommonResult getAdvList( @RequestParam(defaultValue = "0") int schoolId, int advPosition) {
        try {
            List<Advertisement> list = advertisementService.getAdvList0507(schoolId,advPosition);
            log.info("广告列表获取成功！");
            return CommonResult.success("广告列表获取成功！", list);
        } catch (Exception e) {
            log.info("广告列表获取失败！");
            log.info(e.getMessage());
            return CommonResult.failed(e.getMessage());
        }
    }

    @ApiOperation("获取单个广告")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "广告id", required = true),
            @ApiImplicitParam(name = "userId", value = "用户id", required = true),
    })
    @GetMapping("/getOneAdvById")
    public CommonResult getOneAdvById( String id, String userId) {
        try {
            Advertisement advertisement = advertisementService.getOneAdvById(id,userId);
            log.info("单个广告获取成功！");
            return CommonResult.success("单个广告获取成功！", advertisement);
        } catch (Exception e) {
            log.info("单个广告列表获取失败！");
            log.info(e.getMessage());
            return CommonResult.failed(e.getMessage());
        }
    }

    @ApiOperation("创建广告报名")
    @PostMapping("/createAdvSignUp")
    public CommonResult createAdvSignUp(@RequestBody AdvSignUp advSignUp) {
        try {
            JSONObject result = advertisementService.createAdvSignUp(advSignUp);
            return CommonResult.success("广告报名创建成功！", result.get("SignUpInfo"));
        } catch (Exception e) {
            log.warn("广告报名创建失败！广告信息：" + e.getMessage());
            return CommonResult.failed("创建失败，接口异常！");
        }
    }

    @ApiOperation("获取报名信息")
    @GetMapping("/getAdvSignUp")
    public CommonResult getAdvSignUp(String advId, String userId) {
        try {
            JSONObject result = null;
            result = advertisementService.getAdvSignUp(advId, userId);
            return CommonResult.success("报名信息获取成功！", result);
        } catch (Exception e) {
            log.warn("申请失败！：" + e.getMessage());
            return CommonResult.failed("报名信息获取失败，接口异常！");
        }
    }

    @ApiOperation("获取广告列表(图片为base64)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "schoolId", value = "学校id", required = true),
            @ApiImplicitParam(name = "advPosition", value = "广告所在位置"),
    })
    @GetMapping("/getAdvListWithImageBase64")
    public CommonResult getAdvListWithImageBase64( @RequestParam(defaultValue = "0") int schoolId, String advPosition) {
        try {
            List<Advertisement> list = advertisementService.getAdvListWithImageBase64(schoolId,advPosition);
            log.info("广告列表获取成功！");
            return CommonResult.success("广告列表获取成功！", list);
        } catch (Exception e) {
            log.info("广告列表获取失败！");
            log.info(e.getMessage());
            return CommonResult.failed(e.getMessage());
        }
    }

    @ApiOperation("创建多业务广告报名")
    @PostMapping("/createMultiBusinessAdvSignUp")
    public CommonResult createMultiBusinessAdvSignUp(@RequestBody AdvSignUp advSignUp) {
        try {
            JSONObject result = advertisementService.createMultiBusinessAdvSignUp(advSignUp);
            return CommonResult.success("广告报名创建成功！", result.get("SignUpInfo"));
        } catch (Exception e) {
            log.warn("广告报名创建失败！广告信息：" + e.getMessage());
            return CommonResult.failed("创建失败，接口异常！");
        }
    }



}
