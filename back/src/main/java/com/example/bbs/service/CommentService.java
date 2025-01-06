package com.example.bbs.service;

import com.example.bbs.pojo.Comment;
import com.example.bbs.pojo.vo.ChildrenCommentVO;
import com.example.bbs.pojo.vo.HomeCommentVO;

import java.util.List;

public interface CommentService {
    int publishComment(Comment comment);

    List<HomeCommentVO> getHomeComments(String userId, String contentId, int pageNum, int pageSize);

    int upNumByCommentId(String commentId,int isLike);

    Comment getComment(String targetId);

    void updateCommentType(String targetId, int type);

    int getCommentType(String targetId);

    void informComment(String targetId);

    void informCommentWithSchoolId(String targetId, int schoolId);

    String getPlateId(Comment comment);

    List<HomeCommentVO> getHomeCommentsWithType(String userId, String contentId, int pageNum, int pageSize, String requestType);

    List<HomeCommentVO> getHomeCommentsWithTypeNew(String userId, String contentId, int pageNum, int pageSize, String requestType);

    List<ChildrenCommentVO> getChildrenCommentsVOPage(String userId ,String contentId,  String commentId, int pageNum,  int pageSize, String queryTime);

    List<HomeCommentVO> getHomeCommentsPageWithHighLight(String userId, String contentId,int pageNum, int pageSize, String requestType, String commentId);

}
