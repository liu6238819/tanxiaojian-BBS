package com.example.bbs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.bbs.pojo.ContentKeyWord;
import com.example.bbs.pojo.User_Plate;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ContentKeyWordMapper extends BaseMapper<ContentKeyWord> {
    List<ContentKeyWord> getSecondContentKeyWordList(@Param("schoolId") int schoolId, @Param("duration") int duration);
}
