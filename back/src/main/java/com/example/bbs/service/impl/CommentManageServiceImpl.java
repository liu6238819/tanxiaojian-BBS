package com.example.bbs.service.impl;

import com.auth0.jwt.JWT;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.bbs.mapper.CommentManageMapper;
import com.example.bbs.mapper.ContentMapper;
import com.example.bbs.mapper.UserManageMapper;
import com.example.bbs.pojo.Comment;
import com.example.bbs.pojo.User;
import com.example.bbs.service.CommentManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 功能描述：
 *
 * @Author: lyc
 * @Date: 2022/2/14 3:01 下午
 */
@Service
public class CommentManageServiceImpl implements CommentManageService {

    @Autowired
    private UserManageMapper userManageMapper;

    @Autowired
    private CommentManageMapper commentManageMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    ContentMapper contentMapper;


    @Override
    public Boolean judgeTokenValid(String token) {

        String userId= JWT.decode(token).getAudience().get(0);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId",userId);
        User user = userManageMapper.selectOne(queryWrapper);
        if(user!=null){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public Integer updateCommentInfo(String commentId,Integer commentState) {
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("commentId",commentId);
        Comment comment = commentManageMapper.selectOne(queryWrapper);
        comment.setCommentState(commentState);
        int result =commentManageMapper.updateById(comment);
        return result;

    }
}
