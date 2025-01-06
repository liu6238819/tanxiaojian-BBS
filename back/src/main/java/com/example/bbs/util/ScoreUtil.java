package com.example.bbs.util;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.bbs.mapper.*;
import com.example.bbs.pojo.*;
import com.example.bbs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;

import static com.example.bbs.util.DateUtil.initToday;

@Service
public class ScoreUtil {
    @Autowired
    RemindMapper remindMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    ContentMapper contentMapper;
    @Autowired
    CommentMapper commentMapper;
    @Autowired
    AdmireMapper admireMapper;
    @Autowired
    ScoreRecordMapper scoreRecordMapper;
    @Autowired
    UserService userService;

    public int getActionNum(String userId, int actionType) {
        int num = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (actionType == 1) {//发帖
            QueryWrapper<Content> contentQueryWrapper = new QueryWrapper<>();
            contentQueryWrapper.eq("userId", userId);
            contentQueryWrapper.gt("createTime", sdf.format(initToday()));
            num = contentMapper.selectCount(contentQueryWrapper);
        } else if (actionType == 2) {//评论
            QueryWrapper<Comment> commentQueryWrapper = new QueryWrapper<>();
            commentQueryWrapper.eq("userId", userId);
            commentQueryWrapper.gt("createTime", sdf.format(initToday()));
            num = commentMapper.selectCount(commentQueryWrapper);
        } else if (actionType == 3) {//点赞
            QueryWrapper<Admire> admireQueryWrapper = new QueryWrapper<>();
            admireQueryWrapper.eq("userId", userId);
            admireQueryWrapper.gt("createTime", sdf.format(initToday()));
            num = admireMapper.selectCount(admireQueryWrapper);
        }
        System.out.println(actionType + "已发数量：" + num);
        return num;
    }

    public int changeScore(String userId, int changeType, int score, String relatedId) {
        int newScore = -1;
        User user = userService.getUserByUserId(userId);
        if (null == user) return newScore;
        //认证积分强制改为220
        if (changeType == 5) {
            score = 220;
        }
        //新建一条积分记录
        ScoreRecord scoreRecord = new ScoreRecord();
        scoreRecord.setUserId(userId);
        scoreRecord.setChangeNum(score);
        scoreRecord.setRelatedId(relatedId);
        //判断修改类型
        if (changeType == 1 || changeType == 2 || changeType == 3 || changeType == 4 || changeType == 5 || changeType == 6) {//增加积分操作：1发帖，2评论，3点赞，4登录，5认证，6支付失败返还
            user.setScores(user.getScores() + score);
            scoreRecord.setActionType(changeType);
            scoreRecord.setNewScore(user.getScores());
            System.out.println("积分修改成功：" + changeType);
        } else if (changeType == 11 || changeType == 12 || changeType == 13) {//减少积分操作：11删除帖子，12删除评论,13兑换物品
            scoreRecord.setActionType(changeType);
            if (user.getScores() - score < 0) { //积分扣除失败
                System.out.println("积分修改失败,余额不足" + changeType);
                return -1;
            } else {//积分扣除成功
                user.setScores(user.getScores() - score);
                scoreRecord.setNewScore(user.getScores());
                System.out.println("积分修改成功：" + changeType);
            }

        }
        //数据库修改，登陆时例外处理，不修改user表(login接口也执行update，会导致失效)
        if (changeType != 4) {
            userMapper.updateById(user);
        }
        scoreRecordMapper.insert(scoreRecord);

        newScore = user.getScores();
        return newScore;


    }

}
