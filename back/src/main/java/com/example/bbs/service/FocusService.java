package com.example.bbs.service;

import com.alibaba.fastjson.JSONObject;
import com.example.bbs.pojo.vo.HomeContentVO;
import com.example.bbs.result.CommonResult;

import java.util.List;

/**
 * 功能描述：
 *
 * @Author: lyc
 * @Date: 2022/2/21 8:25 下午
 */
public interface FocusService {
    void refreshFollowRecord(String userId, String targetId, Integer state);
    Integer addRemind(String userId, String targetId,Integer state);
    JSONObject searchFocusContentRecord0103(String userId, int schoolId);
    int updateFocusSearchTime(String userId, int schoolId);
}
