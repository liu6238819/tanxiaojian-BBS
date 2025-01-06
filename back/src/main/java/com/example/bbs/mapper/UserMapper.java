package com.example.bbs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.bbs.pojo.User;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface UserMapper extends BaseMapper<User> {
    boolean batchUpdateUserInfo (@Param("userInfoList") List<User> userInfoList);
}
