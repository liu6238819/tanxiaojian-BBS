package com.example.bbs.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.bbs.mapper.*;
import com.example.bbs.pojo.*;
import com.example.bbs.result.CommonResult;
import com.example.bbs.service.ShortUrlService;
import com.example.bbs.service.TokenService;
import com.example.bbs.service.UserManageService;
import com.example.bbs.service.UserService;
import com.example.bbs.util.HttpMethod;
import com.example.bbs.util.SchedulerUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 功能描述：
 *
 * @Author: lyc
 * @Date: 2022/2/3 2:02 下午
 */
@Slf4j
@RestController
@Api(tags = "微信平台相关接口")
@RequestMapping("/commonAPIs")
//commonAPIs
public class WeiXinController {

    @Autowired
    TokenService tokenService;

    @Autowired
    UserManageService userManageService;

    @Autowired
    private TokenMapper tokenMapper;

    @Autowired
    UserMapper userMapper;
    @Autowired
    ContentMapper contentMapper;
    @Autowired
    BbsConfigMapper bbsConfigMapper;
    @Autowired
    ShortUrlService shortUrlService;
    @Autowired
    FocusMapper focusMapper;
    @Autowired
    UserService userService;


    private JSONObject getWXInfo(String APPID) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("APPID", APPID);

        //替换为自己的小程序id和APPSECRET
        if (APPID.equals("")) {//综合社区
            jsonObject.put("APPSECRET", "");
            jsonObject.put("testOpenId", "");
            jsonObject.put("schoolId", 1);
        }
        return jsonObject;
    }


    @GetMapping("/getAccessTokenWX")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "APPID", value = "小程序ID", required = true),
    })
    public CommonResult auth(String APPID) {
        JSONObject wxInfo = getWXInfo(APPID);
        JSONObject jsonObject = new JSONObject();
        String ACCESS_TOKEN = tokenService.getLatestToken(APPID, wxInfo);
        if (ACCESS_TOKEN == null) {
            return CommonResult.failed("获取token失败");
        } else {
            jsonObject.put("access_token", ACCESS_TOKEN);
            return CommonResult.success(jsonObject);
        }
    }

    @GetMapping("/getAccessTokenWXFromAdmin")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "APPID", value = "小程序ID", required = true),
    })
    public CommonResult getAccessTokenWXFromAdmin(String APPID) {
        JSONObject wxInfo = getWXInfo(APPID);
        JSONObject jsonObject = new JSONObject();
        String ACCESS_TOKEN = tokenService.getLatestToken(APPID, wxInfo);
        if (ACCESS_TOKEN == null) {
            return CommonResult.failed("获取token失败");
        } else {
            jsonObject.put("access_token", ACCESS_TOKEN);
            return CommonResult.success(jsonObject);
        }
    }


    @GetMapping("/code2SessionWX")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "code", value = "字符码", required = true, dataType = "string"),
            @ApiImplicitParam(name = "APPID", value = "小程序ID", required = true),
    })
    public CommonResult code2SessionWX(@RequestParam("code") String code, String APPID) throws IOException {
        JSONObject wxInfo = getWXInfo(APPID);
        String res = tokenService.acquireOpenId(code, wxInfo.getString("APPID"), wxInfo.getString("APPSECRET"));
        JSONObject jsonObject = JSON.parseObject(res);
        //做一个判断，如果有用户就不再创建了
        if (jsonObject.get("unionid") == null) {
            jsonObject.put("unionid", "");
        }
        if (jsonObject.get("openid") != null) {
            Integer result = userManageService.findUserByOpenId(jsonObject.get("openid").toString(), jsonObject.get("unionid").toString());
            if (result == 0) {
                tokenService.createUser(jsonObject.get("openid").toString());
            } else {
                jsonObject.put("code", 201);
                jsonObject.put("msg", "该用户已经存在");
            }
            return CommonResult.success(jsonObject);
        } else {
            return CommonResult.failed(jsonObject.toString());
        }

    }


}
