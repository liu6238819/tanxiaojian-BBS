package com.example.bbs.service;

import com.example.bbs.pojo.Comment;
import com.example.bbs.pojo.Content;
import com.example.bbs.result.CommonResult;


public interface CheckRecordService {
    int addCheckRecord(String userId, String contentId, int schoolId, int checkState, int breakRuleState, String breakRuleReason, String checkText);
    CommonResult AICheckContent(Content content);
    String getAuditResFromBotApi0603(String contentText ,String token, String botId);

    int CheckContentSensitiveWord(Content content);

    int CheckCommentSensitiveWord(Comment comment);

}
