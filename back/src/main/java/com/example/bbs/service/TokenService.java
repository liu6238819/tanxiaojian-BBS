package com.example.bbs.service;

import com.alibaba.fastjson.JSONObject;
import com.example.bbs.pojo.Token;

import java.util.Date;

/**
 * 功能描述：
 *
 * @Author: lyc
 * @Date: 2022/2/4 11:26 上午
 */
public interface TokenService {
    public Integer saveToken(String token, String appId);
    public Boolean judgeUsable(Date date,Token latestToken,String testOpenId);
    public String acquireAccessToken(String AppId,String secret);
    public Integer updateToken(String id,String token,String appId);
    public String acquireOpenId(String Code,String AppId,String secret);
    public Integer createUser(String openId);
    public String generateToken(String phone,String password);
    public String getLatestToken(String appId , JSONObject wxInfo);

}
