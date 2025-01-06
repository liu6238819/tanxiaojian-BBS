package com.example.bbs.service;

import com.example.bbs.pojo.vo.EditPlateInfoVO;

/**
 * 功能描述：
 *
 * @Author: lyc
 * @Date: 2022/2/14 3:02 下午
 */
public interface CommentManageService {
    Boolean judgeTokenValid(String token);
    Integer updateCommentInfo(String commentId,Integer commentState);
}
