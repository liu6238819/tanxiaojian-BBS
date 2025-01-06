package com.example.bbs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.bbs.pojo.Token;
import com.example.bbs.pojo.User;
import org.springframework.stereotype.Repository;

/**
 * 功能描述：
 *
 * @Author: lyc
 * @Date: 2022/2/4 11:26 上午
 */
@Repository //代表持久层
public interface TokenMapper extends BaseMapper<Token> {
}
