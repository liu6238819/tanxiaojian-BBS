package com.example.bbs.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.bbs.mapper.TokenMapper;
import com.example.bbs.mapper.UserMapper;
import com.example.bbs.pojo.Token;
import com.example.bbs.pojo.User;
import com.example.bbs.service.TokenService;
import com.example.bbs.service.UserService;
import com.example.bbs.util.HttpMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

/**
 * 功能描述：
 *
 * @Author: lyc
 * @Date: 2022/2/4 11:26 上午
 */
@Service
public class TokenServiceImpl implements TokenService {

    @Autowired
    private TokenMapper tokenMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;


    @Override
    public Integer saveToken(String accessToken,String appId) {
        Token token = new Token();
        token.setAccessToken(accessToken);
        token.setAppId(appId);
        Integer result =tokenMapper.insert(token);
        return result;
    }

    @Override
    public Boolean judgeUsable(Date date,Token latestToken,String testOpenId) {
        if (latestToken==null)  return false;
        //时间比较
        Date preDate = latestToken.getCreateTime();
        long preApplyTime = preDate.getTime();
        long currentApplyTime = date.getTime();
        int minutes = (int) ((currentApplyTime - preApplyTime) / (1000 * 60));
        System.out.println("两个时间之间的分钟差为：" + minutes);
        //发一个请求，测试是否因切换环境而失效，开发时使用
        String url = "https://api.weixin.qq.com/wxa/msg_sec_check?access_token="+latestToken.getAccessToken();
        String result = HttpMethod.isSecurityTxt(url,testOpenId,1,2,"judgeTokenUsable");
        JSONObject jsonObject = JSONObject.parseObject(result);
        if (minutes > 120||jsonObject.getInteger("errcode")==40001) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public String acquireAccessToken(String APPID, String APPSECRET) {
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + APPID + "&secret=" + APPSECRET;
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);
        return response;
    }

    @Override
    public Integer updateToken(String id, String accessToken, String appId) {
        QueryWrapper<Token> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id).eq("appId",appId);
        Token token = tokenMapper.selectOne(queryWrapper);
        token.setAccessToken(accessToken);
        Integer res = tokenMapper.updateById(token);
        return res;
    }

    @Override
    public String acquireOpenId(String code, String APPID, String APPSECRET) {
        String getOpenIdUrl = "";
        getOpenIdUrl = "https://api.weixin.qq.com/sns/jscode2session?appid=" + APPID + "&secret=" + APPSECRET + "&js_code=" + code + "&grant_type=authorization_code";
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(getOpenIdUrl, String.class);
    }

    @Override
    public Integer createUser(String openId) {
        System.out.println(openId);
        User user = new User();
        user.setOpenId(openId);
        //将用户初始状态重置为0;
        user.setUserState(0);
        //用户初始类型设置为0;
        user.setUserType(0);
        return userMapper.insert(user);
    }

    @Override
    public String generateToken(String phone, String password) {
        String token = "";
        User user = userService.userLogin(phone, password);
        if (user != null) {
            token = JWT.create().withAudience(user.getUserId())
                    .sign(Algorithm.HMAC256(user.getPassword()));
        }
        return token;
    }

    @Override
    public String getLatestToken(String appId, JSONObject wxInfo) {
        String ACCESS_TOKEN = "";
        Token latestToken = null;
        QueryWrapper<Token> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("appId", wxInfo.getString("APPID")).orderByDesc("createTime").last("LIMIT 5");
        List<Token> tokenList = tokenMapper.selectList(queryWrapper);
        latestToken = tokenList.get(0);
        Date date = new Date();
        //判断此时的token是否有效
        Boolean res = judgeUsable(date, latestToken, wxInfo.getString("testOpenId"));
        //有效
        if (res.equals(true)) {
            ACCESS_TOKEN = latestToken.getAccessToken();
        }
        //无效。微信接口再获取一个Token，并存入mysql和redis
        else {
            String response = acquireAccessToken(appId, wxInfo.getString("APPSECRET"));
            JSONObject jsonObject = JSON.parseObject(response);
            saveToken(jsonObject.get("access_token").toString(), appId);
            if (response != null) {
                ACCESS_TOKEN = jsonObject.get("access_token").toString();
            } else {
                return null;
            }
        }
        return ACCESS_TOKEN;
    }
}
