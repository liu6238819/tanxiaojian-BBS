package com.example.bbs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.bbs.pojo.CommentKeyWord;
import com.example.bbs.pojo.ContentKeyWord;

import java.util.List;

public interface CommentKeyWordMapper extends BaseMapper<CommentKeyWord> {
    List<CommentKeyWord> getCommentKeyWordList(int schoolId);
}
