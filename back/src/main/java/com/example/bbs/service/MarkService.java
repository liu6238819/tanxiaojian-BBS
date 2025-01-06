package com.example.bbs.service;

import com.alibaba.fastjson.JSONObject;
import com.example.bbs.pojo.Comment;
import com.example.bbs.pojo.vo.MarkMessageVO;
import com.example.bbs.result.CommonResult;

import java.util.List;

public interface MarkService {
    int editMark(String userId, String contentId, int isMark);
    int addMarkMessage(String userId, Comment comment);
    int getMarkMessageListVo(String userId,int pageNum, int pageSize);
    int editReadState(String userId, String contentId);
    int editMark_1(String userId, String contentId, int isMark);
    int editReadState_1(String userId, String contentId);
    JSONObject getMarkMessageListVo_1(String userId, int pageNum, int pageSize);
    JSONObject getMarkMessageListVo1231(String userId, int pageNum, int pageSize);


}
