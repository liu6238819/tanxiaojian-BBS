package com.example.bbs.service;

import com.example.bbs.pojo.Content;

/**
 * 功能描述：
 *
 * @Author: lyc
 * @Date: 2022/2/14 3:40 下午
 */
public interface ContentManageService {
    Boolean judgeTokenValid(String token);
    Integer updateContentInfo(String commentId,Integer commentState);
    int manageContent(Content content);
}
