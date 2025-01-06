package com.example.bbs.service.impl;

import com.auth0.jwt.JWT;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.bbs.mapper.ContentManageMapper;
import com.example.bbs.mapper.UserManageMapper;
import com.example.bbs.pojo.Content;
import com.example.bbs.pojo.User;
import com.example.bbs.service.ContentManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;


@Service
public class ContentManageServiceImpl implements ContentManageService {
    @Autowired
    private UserManageMapper userManageMapper;

    @Autowired
    private ContentManageMapper contentManageMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

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
    public Integer updateContentInfo(String contentId, Integer contentState) {
        QueryWrapper<Content> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("contentId",contentId);
        Content content = contentManageMapper.selectOne(queryWrapper);
        content.setContentState(contentState);
        int result = contentManageMapper.updateById(content);
        return result;
    }

    @Override
    public int manageContent(Content content) {
        QueryWrapper<Content> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("contentId",content.getContentId());
        Content contentInSQL = contentManageMapper.selectOne(queryWrapper);
        if (contentInSQL.getContentState() != content.getContentState()){
            contentInSQL.setContentState(content.getContentState());
        }
        if (contentInSQL.getVisibleRange() != content.getVisibleRange()){
            contentInSQL.setVisibleRange(content.getVisibleRange());
        }
        if (contentInSQL.getNoComment() != content.getNoComment()){
            contentInSQL.setNoComment(content.getNoComment());
        }
        if (contentInSQL.getAlumniOnly() != content.getAlumniOnly()) {
            contentInSQL.setAlumniOnly(content.getAlumniOnly());
        }
        if (contentInSQL.getRealReadNum() != content.getRealReadNum()) {
            contentInSQL.setRealReadNum(content.getRealReadNum());
        }
        int result = contentManageMapper.updateById(contentInSQL);
        return result;
    }
}
