package com.example.bbs.service;

import com.alibaba.fastjson.JSONObject;
import com.example.bbs.pojo.Comment;
import com.example.bbs.pojo.vo.RemindVO;

import java.util.List;

public interface RemindService {
    List<RemindVO> getRemindsPage(String userId,String queryType, Integer schoolId, int pageNum, int pageSize);
    int selectNums(String userId,String queryType);
    boolean readRemind(String userId,String remindId);
    boolean clearRemind(String userId,Integer type);
    JSONObject editRemindByContentId(String userId, String contentId);
    List<RemindVO> getRemindsPageWithoutschoolId0102(String userId,String queryType, int pageNum, int pageSize);
}
