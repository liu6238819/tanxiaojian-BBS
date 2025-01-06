package com.example.bbs.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.bbs.mapper.*;
import com.example.bbs.pojo.*;
import com.example.bbs.pojo.vo.*;
import com.example.bbs.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class RedisServiceImpl implements RedisService {


    @Autowired
    PlateMapper plateMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    //根据板块id获取板块
    @Override
    public Plate getPlateByIdUseRedis(String plateId, long expireInSeconds) {
        Plate plate = new Plate();
        //获取数据时排除 Redis 相关的异常
        try{
            plate = (Plate) redisTemplate.opsForValue().get("getPlateById:" + plateId );
        }
        catch (Exception e){
            System.out.println("redisError: " + e.getMessage());
            e.printStackTrace();
            //如果redis异常，访问数据库获取数据
            QueryWrapper<Plate> plateQueryWrapper = new QueryWrapper<>();
            plateQueryWrapper.eq("plateId", plateId);
            plate = plateMapper.selectOne(plateQueryWrapper);
            return plate;
        }
        //若redis中未获取到结果，调用原本的数据库查询
        if (plate == null) {
            QueryWrapper<Plate> plateQueryWrapper = new QueryWrapper<>();
            plateQueryWrapper.eq("plateId", plateId);
            plate = plateMapper.selectOne(plateQueryWrapper);
            //如果数据库有返回结果，将结果存在redis中
            if (plate != null) {
                try {
                    redisTemplate.opsForValue().set("getPlateById:" + plateId, plate ,expireInSeconds, TimeUnit.SECONDS);
                }
                catch (Exception e){
                    // 捕获 Redis 相关的异常
                    System.out.println("redisError: " + e.getMessage());
                    e.printStackTrace();
                }

            }
        }
        return plate;
    }



    //根据userId获取用户
    @Override
    public User getUSerByIdUseRedis(String userId, long expireInSeconds) {
        User user = new User();
        //获取数据时排除 Redis 相关的异常
        try{
            user = (User) redisTemplate.opsForValue().get("getUserById:" + userId );
        }
        catch (Exception e){
            System.out.println("redisError: " + e.getMessage());
            e.printStackTrace();
            //如果redis异常，访问数据库获取数据
            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.eq("userId", userId);
            user = userMapper.selectOne(userQueryWrapper);
            return user;
        }

        //若redis中未获取到结果，调用原本的数据库查询
        if (user == null) {
            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.eq("userId", userId);
            user = userMapper.selectOne(userQueryWrapper);
            //如果数据库有返回结果，将结果存在redis中
            if (user != null) {
                try {
                    redisTemplate.opsForValue().set("getUserById:" + userId, user ,expireInSeconds, TimeUnit.SECONDS);
                }
                catch (Exception e){
                    // 捕获 Redis 相关的异常
                    System.out.println("redisError: " + e.getMessage());
                    e.printStackTrace();
                }

            }
        }
        return user;
    }




}
