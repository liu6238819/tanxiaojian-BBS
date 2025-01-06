package com.example.bbs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.bbs.pojo.Comment;
import com.example.bbs.pojo.vo.ChildrenCommentVO;
import com.example.bbs.pojo.vo.HomeCommentVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CommentMapper extends BaseMapper<Comment> {
    List<HomeCommentVO> getHomeCommentsPage(@Param("contentId") String contentId, @Param("pageNum") int pageNum, @Param("pageSize") int pageSize);
    List<ChildrenCommentVO> getChildrenCommentsVO(@Param("contentId") String contentId,@Param("commentId") String commentId);
    List<HomeCommentVO> getMainCommentsByContentId(@Param("contentId") String contentId);
    List<ChildrenCommentVO> getTwoChildrenCommentsVO(@Param("contentId") String contentId,@Param("commentId") String commentId);
    List<HomeCommentVO> getHomeCommentsPageWithType(@Param("contentId") String contentId, @Param("pageNum") int pageNum, @Param("pageSize") int pageSize,@Param("requestType") String requestType);
    List<ChildrenCommentVO> getChildrenCommentsVOPage(@Param("contentId") String contentId,@Param("commentId") String commentId,@Param("pageNum") int pageNum, @Param("pageSize") int pageSize,@Param("queryTime") String queryTime);

    List<HomeCommentVO> getHomeCommentsPageWithTypeNew(@Param("contentId") String contentId, @Param("pageNum") int pageNum, @Param("pageSize") int pageSize,@Param("requestType") String requestType,@Param("userId") String userId, @Param("schoolId") int schoolId);
    List<ChildrenCommentVO> getTwoChildrenCommentsVONew(@Param("contentId") String contentId,@Param("commentId") String commentId,@Param("userId") String userId, @Param("schoolId") int schoolId);

    List<HomeCommentVO> getAllMainCommentsByContentId(@Param("contentId") String contentId);

    List<ChildrenCommentVO> getAllChildrenCommentVo(@Param("contentId") String contentId,@Param("commentId") String commentId);
    HomeCommentVO getCommentVoByCommentId(@Param("commentId") String commentId);

    //2024.2.23 获取评论列表,分评论类型,过滤冻结
    List<HomeCommentVO> getHomeCommentsPageFilterFreeze(@Param("contentId") String contentId, @Param("pageNum") int pageNum, @Param("pageSize") int pageSize,@Param("requestType") String requestType,@Param("userId") String userId);
    List<ChildrenCommentVO> getThreeChildrenCommentsFilterFreeze(@Param("contentId") String contentId,@Param("commentId") String commentId,@Param("userId") String userId);
    List<ChildrenCommentVO> getChildrenCommentsVOPageFilterFreeze(@Param("contentId") String contentId,@Param("commentId") String commentId,@Param("pageNum") int pageNum, @Param("pageSize") int pageSize,@Param("queryTime") String queryTime,@Param("userId") String userId);

    boolean logOutCommentByUserId(@Param("userId") String userId);

    List<ChildrenCommentVO> getAllChildrenCommentVoFilterFreeze(@Param("contentId") String contentId,@Param("commentId") String commentId);

}
