package com.example.bbs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.bbs.pojo.Content;
import com.example.bbs.pojo.vo.HomeContentVO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ContentMapper extends BaseMapper<Content> {

    List<HomeContentVO> getContentsPageByTime(@Param("schoolId") int schoolId,@Param("searchUserId") String searchUserId,@Param("plateId") String plateId, @Param("pageNum") int pageNum, @Param("pageSize") int pageSize,@Param("contentType") int contentType);

    List<HomeContentVO> getContentsPageByHot(@Param("schoolId") int schoolId,@Param("searchUserId") String searchUserId,@Param("plateId") String plateId, @Param("pageNum") int pageNum, @Param("pageSize") int pageSize,@Param("duration") int duration);

    List<HomeContentVO> getContentsPageByFollow(@Param("schoolId") int schoolId,@Param("userId") String userId, @Param("pageNum") int pageNum, @Param("pageSize") int pageSize);

    HomeContentVO getContentById(@Param("contentId") String contentId);

    List<HomeContentVO> getTopContents(@Param("schoolId") int schoolId,@Param("plateId") String plateId,@Param("pageNum") int pageNum, @Param("pageSize") int pageSize);

    List<HomeContentVO> getContentsPageBeforeTime(@Param("schoolId") int schoolId, @Param("pageNum") int pageNum, @Param("pageSize") int pageSize,@Param("contentType") int contentType,@Param("duration") int duration);

    List<HomeContentVO> getContentsPageHistoryHot(@Param("schoolId") int schoolId, @Param("pageNum") int pageNum, @Param("pageSize") int pageSize,@Param("duration") int duration,@Param("hotStandardNum") int hotStandardNum);

    List<HomeContentVO> getContentsPageWithQueryTime(@Param("schoolId") int schoolId,@Param("searchUserId") String searchUserId,@Param("plateId") String plateId, @Param("pageNum") int pageNum, @Param("pageSize") int pageSize,@Param("contentType") int contentType,@Param("queryTime") String queryTime);

    List<HomeContentVO> getContentsPageWithQueryTimeNew(@Param("schoolId") int schoolId,@Param("searchUserId") String searchUserId,@Param("plateId") String plateId, @Param("pageNum") int pageNum, @Param("pageSize") int pageSize,@Param("contentType") int contentType,@Param("queryTime") String queryTime);

    //2024.2.21 获取帖子列表,携带请求时间,非联表，过滤冻结
    List<HomeContentVO> getContentsPageFilterFreeze(@Param("schoolId") int schoolId,@Param("searchUserId") String searchUserId,@Param("plateId") String plateId, @Param("pageNum") int pageNum, @Param("pageSize") int pageSize,@Param("contentType") int contentType,@Param("queryTime") String queryTime);

    List<HomeContentVO> getContentsPageFilterFAndS(@Param("schoolId") int schoolId,@Param("searchUserId") String searchUserId,@Param("plateId") String plateId, @Param("pageNum") int pageNum, @Param("pageSize") int pageSize,@Param("contentType") int contentType,@Param("queryTime") String queryTime);

    List<HomeContentVO> getSimilarContents(@Param("query") String query);

    List<Content> getImageBase64WithQueryTime(@Param("schoolId") int schoolId,@Param("plateId") String plateId,@Param("pageNum") int pageNum, @Param("pageSize") int pageSize,@Param("contentType") int contentType,@Param("queryTime") String queryTime);

    List<HomeContentVO> searchContentsPageByContentType(@Param("schoolId") int schoolId,@Param("searchUserId") String searchUserId, @Param("pageSize") int pageSize,@Param("contentType") int contentType,@Param("queryTime") String queryTime,@Param("keywords") String keywords);

    List<HomeContentVO> getAllContentsPage(@Param("schoolId") int schoolId,@Param("searchUserId") String searchUserId, @Param("pageNum") int pageNum, @Param("pageSize") int pageSize);

    boolean logOutContentByUserId(@Param("userId") String userId);

    Map<String, Object> selectFocusContentStatistics(@Param("targetIdList") List<String> targetIdList,
                                                     @Param("schoolId") int schoolId,
                                                     @Param("lastLoginTime") Date lastLoginTime);

    List<HomeContentVO>  getFocusContentPage(@Param("focusUserIdList") List<String> focusUserIdList,
                                             @Param("schoolId") int schoolId,
                                             @Param("queryTime") String queryTime,
                                             @Param("pageSize") int pageSize);
}
