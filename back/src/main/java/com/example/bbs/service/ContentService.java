package com.example.bbs.service;

import com.alibaba.fastjson.JSONArray;
import com.example.bbs.pojo.Content;
import com.example.bbs.pojo.vo.HomeContentVO;

import java.util.List;

public interface ContentService {
    int publishContent(Content content);

    int publishContent0605(Content content);

    List<HomeContentVO> getHomeContents(String userId,String searchUserId,String plateId, int pageNum, int pageSize,int schoolId,String requestType, int contentType,int duration);

    int upAndDownNumEditByContentId(String contentId, int isLike);

    Content getContent(String targetId);

    void updateContentType(String targetId, int type);

    int getContentType(String targetId);

    void informContent(String contentId);

    void informContentWithSchoolId(String contentId,int schoolId);

    void addReadNum(String contentId, String userId);

    HomeContentVO getContentById(String contentId,String userId);

    HomeContentVO dealAnonymous(HomeContentVO content);

    List<HomeContentVO> getHistoryContents(String userId, int pageNum, int pageSize, int schoolId, String requestType, int contentType,int duration);

    JSONArray getImageBase64ByContentIds(String ContentIds);

    JSONArray getImageBase64WithQueryTime(int pageNum, int pageSize,int schoolId, int contentType,String queryTime,String plateId);

    List<HomeContentVO> searchContentsByContentType(String userId, int pageNum, int pageSize, int schoolId, int contentType, String keywords, String queryTime);

    List<HomeContentVO> getHomeContentsWithQueryTimeUseRedis0511(String userId, String searchUserId, String plateId, int pageNum, int pageSize, int schoolId, String requestType, int contentType, int duration, String queryTime, int hideSecondHand);

    List<HomeContentVO> getAllHomeContents(String userId, int pageNum, int pageSize,int schoolId);

    List<HomeContentVO> getFocusContentPage(String userId, int schoolId, String queryTime, int pageSize);
}
