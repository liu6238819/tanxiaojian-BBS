package com.example.bbs.service;

import com.example.bbs.pojo.Comment;
import com.example.bbs.pojo.Content;
import com.example.bbs.pojo.vo.HomeContentVO;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface FuzzySearchService {
    public List<HomeContentVO> getSimilarContent(String text, int schoolId);
    public Map<String, Object> getSecondHandsContent(int schoolId, String text ,String userId,int searchWay);

}
