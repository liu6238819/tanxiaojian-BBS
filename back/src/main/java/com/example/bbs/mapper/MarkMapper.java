package com.example.bbs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.bbs.pojo.Mark;
import com.example.bbs.pojo.vo.MarkMessageVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MarkMapper extends BaseMapper<Mark> {
    List<MarkMessageVO> getMarkMessageVOListByUser(@Param("userId") String userId, @Param("pageNum") int pageNum, @Param("pageSize") int pageSize);
    List<MarkMessageVO> getMarkMessageVOListByUser1231(@Param("userId") String userId, @Param("pageNum") int pageNum, @Param("pageSize") int pageSize);

}
