package com.example.bbs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.bbs.pojo.UserPhone;
import com.example.bbs.pojo.UserSchool;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserPhoneMapper extends BaseMapper<UserPhone> {
    boolean insertBatchUserPhone(@Param("userPhoneList") List<UserPhone> userPhoneList);
}
