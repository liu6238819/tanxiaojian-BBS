package com.example.bbs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.bbs.pojo.SignUpActivity;
import com.example.bbs.pojo.TaskOrder;
import com.example.bbs.pojo.vo.RankingVO;
import com.example.bbs.pojo.vo.SignUpActivityVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TaskOrderMapper extends BaseMapper<TaskOrder> {
    List<TaskOrder> selectTaskOrderList(@Param("orderKind") int orderKind, @Param("orderState") int orderState, @Param("schoolId") int schoolId, @Param("publisherId") String publisherId, @Param("pageNum") int pageNum, @Param("pageSize") int pageSize);

    List<SignUpActivityVO> selectActivityList(@Param("ownerId") String ownerId, @Param("kind") int kind, @Param("state") int state, @Param("schoolId") int schoolId, @Param("alumniOnly") int alumniOnly, @Param("pageNum") int pageNum, @Param("pageSize") int pageSize);

    List<TaskOrder> selectProfitOrderList(@Param("userId") String userId, @Param("schoolId") int schoolId, @Param("queryType") int queryType, @Param("duration") int duration);

    List<RankingVO> selectProfitRanking(@Param("publisherId") String userId,  @Param("schoolId") int schoolId, @Param("duration") int duration, @Param("pageNum") int pageNum, @Param("pageSize") int pageSize);

}
