package com.example.bbs.util;

import com.alibaba.fastjson.JSONObject;
import com.example.bbs.mapper.*;
import com.example.bbs.pojo.Content;
import com.example.bbs.result.CommonResult;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 功能描述：
 *
 * @Author: lyc
 * @Date: 2022/2/10 10:07 上午
 */
public class commonSearch {
    @Autowired
    private UserManageMapper userManageMapper ;
    @Autowired
    private PlateMapper plateMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private ContentMapper contentMapper;
    public CommonResult searchSimplified(JSONObject structInfo){
        Integer type= (Integer) structInfo.get("type");
        Integer mode = (Integer) structInfo.get("mode");
        if(mode.equals(1)){
            if(type.equals(0)){
            }
        }if(mode.equals(0)){

        }
        return CommonResult.success();
    }

}
