package com.example.bbs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.bbs.pojo.LoginRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LoginRecordMapper extends BaseMapper<LoginRecord> {
    boolean batchInsertLoginRecord(@Param("loginRecordList") List<LoginRecord> loginRecordList);
}
