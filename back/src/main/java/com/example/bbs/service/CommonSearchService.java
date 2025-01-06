package com.example.bbs.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.bbs.pojo.Plate;
import com.example.bbs.pojo.vo.CommonSearchVO;
import com.example.bbs.pojo.vo.SearchPlateVO;
import com.example.bbs.result.CommonResult;

/**
 * 功能描述：
 *
 * @Author: lyc
 * @Date: 2022/2/10 10:23 上午
 */
public interface CommonSearchService {
    String parseObject(CommonSearchVO jsonObject);
    CommonResult getSearchList(String name, Integer mode, String conditionObj,Integer pageNum, Integer pageSize,String sortType);
    int addSearchRecord(String userId, String text, int schoolId );
}
