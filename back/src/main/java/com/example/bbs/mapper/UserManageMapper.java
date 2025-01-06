package com.example.bbs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.bbs.pojo.User;
import com.example.bbs.pojo.vo.HomeContentVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 功能描述：
 *
 * @Author: lyc
 * @Date: 2022/2/3 11:09 上午
 */
@Repository //代表持久层
public interface UserManageMapper extends BaseMapper<User> {
}
