package com.example.bbs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.bbs.pojo.Content;
import com.example.bbs.pojo.User_Content;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface User_ContentMapper extends BaseMapper<User_Content> {
    boolean batchUpdateReadNumById(@Param("userContentList") List<User_Content> userContentList);
    boolean batchInsertUserContent(@Param("userContentList") List<User_Content> userContentList);
}
