package com.example.bbs.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.bbs.auto.UserTypeAutoEdit;
import com.example.bbs.mapper.UserMapper;
import com.example.bbs.mapper.UserSchoolMapper;
import com.example.bbs.pojo.User;
import com.example.bbs.pojo.UserSchool;
import com.example.bbs.result.CommonResult;
import com.example.bbs.service.*;
import com.example.bbs.util.HttpMethod;
import com.example.bbs.util.IpUtil;
import com.example.bbs.util.ScoreUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;

import static com.example.bbs.util.DateUtil.initToday;
import static com.example.bbs.util.DateUtil.initYesterday;

@Slf4j
@RestController
@RequestMapping("/login")
@Api(tags = "跨库登录相关操作")
public class loginController {
    @Autowired
    UserService userService;

    @Autowired
    UserSchoolService userSchoolService;
    @Autowired
    UserTypeAutoEdit userTypeAutoEdit;
    @Autowired
    UserMapper userMapper;
    @Autowired
    UserSchoolMapper userSchoolMapper;

    @Autowired
    FocusService focusService;

    @Autowired
    ScoreUtil scoreUtil;


    @Autowired
    LoginRecordService loginRecordService;

    @Autowired
    UserPhoneService userPhoneService;

    @Autowired
    TokenService tokenService;

    @Value("${spring.datasource.url}")
    private String JDBCDatabaseUrl;
    private String databaseUrl;

    @PostConstruct
    public void databaseUrlInit() {
        databaseUrl = processDatabaseUrl();
    }

    @Autowired
    private RedisTemplate<String, Object> redisTemplateJsonStr;

    //提取数据库名称
    private String processDatabaseUrl() {
        int index = JDBCDatabaseUrl.indexOf("bbs_");
        String processedUrl="";
        if (index != -1) {
            // 打印位置索引和提取的子字符串
            String dbName = JDBCDatabaseUrl.substring(index);
            String[] parts = dbName.split("\\?");
            processedUrl = parts[0];
        } else {
            System.out.println("未找到字符串'bbs_'");
        }
        return processedUrl;
    }

    private JSONObject getWXInfo(String APPID) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("APPID", APPID);
        if (APPID.equals("wx53ca9ca3a3bc4f65")) {//综合社区
            jsonObject.put("APPSECRET", "7d63eb6305aec18a67f603e9efca4364");
            jsonObject.put("testOpenId", "onQqV5KgOLUOjPXm3Bl6EU-CiVN8");
            jsonObject.put("replaceText", "#小程序://谈校间pro/");
            jsonObject.put("schoolId", 3);
        }
        return jsonObject;
    }

    //本地微信用户登录接口（redis）
    @PostMapping("/userLocalLoginWX")
    public CommonResult userLocalLoginWX(String openId, HttpServletRequest request) {
        //获取本次登录ip
        String ipAddr = IpUtil.getIpAddr(request);
        User user = userService.userLoginWX(openId);
        //数据库和redis查询都为null
        if (user == null) {
//            log.warn("用户登录失败！手机号：" + phone);
            return CommonResult.failed("登录异常，请联系客服！");
        } else {
            //日期数值初始化
            Date lastLoginTime = user.getLastLoginTime();
            Date todayZero = initToday();
            Date yesterdayZero = initYesterday();
            Date todayNow = new Date();
            if (null == lastLoginTime) lastLoginTime = user.getUpdateTime(); //无上次登录时间的情况
            if (!StringUtils.isBlank(user.getHeadimgUrl())) {//授权用户，获取每日登录积分
                //逻辑判断
                if (lastLoginTime.after(todayZero)) { //今日多次登录
                    System.out.println("今日多次登录，不获取积分");
                } else if (todayZero.after(lastLoginTime)) {//今日首次登录
                    System.out.println("今日首次登录，获取积分+10");
                    //此处只在score表中增加一行记录，需更新积分
                    scoreUtil.changeScore(user.getUserId(), 4, 20, null);
                    user.setScores(user.getScores() + 20);
                }
            }
            //更新记录
            user.setLastIp(ipAddr);
            user.setUpdateTime(new Date());
            user.setLastLoginTime(todayNow);
            userService.editUserInfos(user);
            log.info("用户登录成功！用户信息：" + user);
            //判断用户电话号是否为空
            JSONObject userInfoObj = new JSONObject();
            if (StringUtils.isBlank(user.getPhone())){
                userInfoObj.put("havePhone", 0);
            }else{
                userInfoObj.put("havePhone", 1);
            }
            //预警用户变为正常
            //userTypeAutoEdit.WarnToNormalUser(user);
            //去除用户敏感信息
//            user.setPhone(null);
            user.setPassword(null);
            user.setIdCardUrl(null);
            user.setLastIp(null);
            user.setOpenId(null);

            userInfoObj.put("user", user);
            return CommonResult.success(userInfoObj);
        }
    }









}
