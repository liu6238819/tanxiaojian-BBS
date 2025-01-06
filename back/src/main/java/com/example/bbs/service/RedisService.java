package com.example.bbs.service;

import com.alibaba.fastjson.JSONObject;
import com.example.bbs.pojo.*;
import com.example.bbs.pojo.vo.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface RedisService {
    Plate getPlateByIdUseRedis(String plateId, long expireInSeconds);
    User getUSerByIdUseRedis(String userId, long expireInSeconds);

}
