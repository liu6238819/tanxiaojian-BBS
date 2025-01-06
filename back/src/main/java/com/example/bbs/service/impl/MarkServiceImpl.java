package com.example.bbs.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.bbs.mapper.*;
import com.example.bbs.pojo.*;
import com.example.bbs.pojo.vo.HomeContentVO;
import com.example.bbs.pojo.vo.MarkMessageVO;
import com.example.bbs.result.CommonResult;
import com.example.bbs.service.MarkService;
import com.example.bbs.util.WebSocketUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MarkServiceImpl implements MarkService {

    @Autowired
    MarkMapper markMapper;

    @Autowired
    ContentMapper contentMapper;

    @Autowired
    RemindMapper remindMapper;


    @Autowired
    CommentMapper commentMapper;

    @Override
    public int editMark(String userId, String contentId, int isMark) {
        int rs = 0;
        if (isMark == 1) {
            rs = addInfo(userId, contentId, isMark);
            //重新获取mark信息列表
            if (rs == 1 || rs == 2) {
                getMarkMessageList(userId, 1, 10);
            }
        }
        if (isMark == 0) {
            rs = delInfo(userId, contentId, isMark);
            int pageNum = 0;
            //重新获取mark信息列表
            if (rs == 0) {
                int listCount = 10;
                //该循环保证用户在点击取消追踪时，不会只获取到前10个mark记录，因而跳转到别的位置
                do {
                    pageNum = pageNum + 1;
                    listCount = getMarkMessageList(userId, pageNum, 10);
                }
                while (listCount == 10);
            }

        }
        return rs;
    }

    @Override
    public int addMarkMessage(String userId, Comment comment) {
        //获取帖子Id
        String contentId = comment.getContentId();
        //判断该帖子是否有用户在mark
        List<Mark> markList = null;
        QueryWrapper<Mark> markQueryWrapper = new QueryWrapper<>();
        markQueryWrapper
                .eq("content_id", contentId)
                .eq("is_mark", 1);
        markList = markMapper.selectList(markQueryWrapper);
        if (null == markList) {
            return 0;
        }
        //有用户mark这个帖子
        else {
            List<Remind> remindList = new ArrayList<>();
            for (Mark mark : markList) {
                String receiverId = mark.getUserId();
                Date nowDate = new Date();
                //在互动行为表中创建一条记录,6代表马住消息
                Remind remind = new Remind();
                String newUUID = UUID.randomUUID().toString().replace("-", "");
                remind.setId(newUUID);
                remind.setState(0);
                remind.setType(6);
                remind.setUserId(receiverId);
                remind.setRelateId(contentId);
                remind.setFromUserId(userId);
                remind.setCreateTime(nowDate);
                //本人在其马住的帖子在下评论不会触发
                if (!receiverId.equals(userId)) {
                    int oldReadState = mark.getIsRead() + 0;
                    //构建insert列表
                    remindList.add(remind);
                    //将mark表中记录变为未读
                    mark.setIsRead(0);
                    mark.setLastCommnetTime(nowDate);
                    markMapper.updateById(mark);
                }
            }
            //数量不为0，一次性写入remind
            if (remindList.size() > 0) {
                remindMapper.saveBatchOfRemind(remindList);
            }
        }
        return 1;
    }


    @Override
    public int getMarkMessageListVo(String userId, int pageNum, int pageSize) {
        getMarkMessageList(userId, pageNum, pageSize);
        return 1;
    }

    @Override
    public int editReadState(String userId, String contentId) {
        //全部已读
        if (contentId.equals("ALL")) {
            QueryWrapper<Mark> markQueryWrapper1 = new QueryWrapper<>();
            markQueryWrapper1
                    .eq("user_id", userId);
            List<Mark> markList = markMapper.selectList(markQueryWrapper1);
            if (null == markList) return -1;
            for (Mark mark : markList) {
                mark.setIsRead(1);
                markMapper.updateById(mark);
            }
        }
        //单个已读
        else {
            QueryWrapper<Mark> markQueryWrapper2 = new QueryWrapper<>();
            markQueryWrapper2
                    .eq("user_id", userId)
                    .eq("content_id", contentId);
            List<Mark> MarkList = markMapper.selectList(markQueryWrapper2);
            Mark oneMark = new Mark();
            if (MarkList.size()>0) {
                oneMark = MarkList.get(0);
            }
            else{
                oneMark = null;
            }
            if (null == oneMark) return -1;
            oneMark.setIsRead(1);
            markMapper.updateById(oneMark);
        }
        int pageNum = 0;
        //重新获取mark信息列表
        int listCount = 10;
        //该循环保证用户在点击时，不会只获取到前10个mark记录，因而跳转到别的位置
        do {
            pageNum = pageNum + 1;
            listCount = getMarkMessageList(userId, pageNum, 10);
        }
        while (listCount == 10);
        return 1;
    }

    //增加马住信息
    private int addInfo(String userId, String contentId, int isMark) {
        //初始化
        int result = -1;
        Mark mark = new Mark();
        mark.setUserId(userId);
        mark.setContentId(contentId);
        mark.setIsMark(isMark);
        mark.setIsRead(1);
        QueryWrapper<Mark> markQueryWrapper = new QueryWrapper<>();
        markQueryWrapper
                .eq("user_id", userId)
                .eq("content_id", contentId);
        List<Mark> MarkList = markMapper.selectList(markQueryWrapper);
        Mark record = new Mark();
        if (MarkList.size()>0) {
            record = MarkList.get(0);
        }
        else{
            record = null;
        }
        if (record == null) {
            //获取帖子发布时间
            QueryWrapper<Content> contentQueryWrapper = new QueryWrapper<>();
            contentQueryWrapper.eq("contentId", mark.getContentId());
            Content content = contentMapper.selectOne(contentQueryWrapper);
            mark.setLastCommnetTime(content.getCreateTime());
            markMapper.insert(mark);
            result = 1;
        } else { //已有记录，更改状态
            record.setIsMark(isMark);
            record.setIsRead(1);
            markMapper.updateById(record);
            result = 2;//再次马住
        }
        return result;

    }

    //取消马住信息
    private int delInfo(String userId, String contentId, int isMark) {
        QueryWrapper<Mark> markQueryWrapper = new QueryWrapper<>();
        markQueryWrapper
                .eq("user_id", userId)
                .eq("content_id", contentId);
        List<Mark> MarkList = markMapper.selectList(markQueryWrapper);
        Mark mark = new Mark();
        if (MarkList.size()>0) {
            mark = MarkList.get(0);
        }
        else{
            mark = null;
        }
        if (null == mark) return -1;
        mark.setIsMark(isMark);
        markMapper.updateById(mark);
        return 0;
    }

    //获取某个用户的mark消息列表
    private int getMarkMessageList(String userId, int pageNum, int pageSize) {
        List<MarkMessageVO> markMessageList = new ArrayList<>();
        //获取该用户所有mark的帖子
        QueryWrapper<Mark> markQueryWrapper = new QueryWrapper<>();
        markQueryWrapper
                .eq("user_id", userId)
                .eq("is_mark", 1)
                .orderByDesc("last_comment_time");
        Page<Mark> markPage = new Page<>(pageNum, pageSize);
        IPage<Mark> marks = markMapper.selectPage(markPage, markQueryWrapper);
        if (null == marks) {
            return 0;
        } else {
            List<Mark> markList = marks.getRecords();
            for (Mark mark : markList) {
                MarkMessageVO markMessageVo = new MarkMessageVO();
                //获取该帖子，该用户的Mark通知
                QueryWrapper<Remind> remindQueryWrapper = new QueryWrapper<>();
                remindQueryWrapper
                        .eq("userId", userId)
                        .eq("relateId", mark.getContentId())
                        .eq("type", 6)
                        .orderByDesc("createTime");
                List<Remind> remindMarkList = remindMapper.selectList(remindQueryWrapper);
                //根据mark的isRead属性判断，是否已读
                markMessageVo.setIsRead(mark.getIsRead());
                markMessageVo.setCommentCount(remindMarkList.size());
                markMessageVo.setLastCommentTime(mark.getLastCommnetTime());
                //获取帖子信息
                QueryWrapper<Content> contentQueryWrapper = new QueryWrapper<>();
                contentQueryWrapper.eq("contentId", mark.getContentId());
                Content content = contentMapper.selectOne(contentQueryWrapper);
                markMessageVo.setContentId(mark.getContentId());
                if (null == content.getTitle() || "".equals(content.getTitle())) {
                    markMessageVo.setContentText(content.getContentText()); //内容
                } else {
                    markMessageVo.setContentText(content.getTitle() + " " + content.getContentText()); //内容
                }
                markMessageVo.setContentImage(content.getContentUrl()); //图片
                markMessageList.add(markMessageVo);

            }
            CommonResult<List<MarkMessageVO>> rs = CommonResult.success(markMessageList);
            JSONObject rsObject = JSONObject.parseObject(JSONObject.toJSON(rs).toString());

            if (pageNum > 1) {
                rsObject.put("returnType", "moreMarkMessageList");
            } else {
                rsObject.put("returnType", "MarkMessageList");
            }
            String json = rsObject.toJSONString();
            WebSocketUtil.sendMessage(userId, json);
            return markList.size();
        }
    }

    //23/11/17:改变mark状态，不返回Mark记录列表
    @Override
    public int editMark_1(String userId, String contentId, int isMark) {
        int rs = 0;
        if (isMark == 1) {
            rs = addInfo(userId, contentId, isMark);

        }
        if (isMark == 0) {
            rs = delInfo(userId, contentId, isMark);
        }
        return rs;
    }

    @Override
    public int editReadState_1(String userId, String contentId) {
        //全部已读
        if (contentId.equals("ALL")) {
            QueryWrapper<Mark> markQueryWrapper1 = new QueryWrapper<>();
            markQueryWrapper1
                    .eq("user_id", userId);
            List<Mark> markList = markMapper.selectList(markQueryWrapper1);
            if (null == markList) return -1;
            for (Mark mark : markList) {
                mark.setIsRead(1);
                markMapper.updateById(mark);
            }
        }
        //单个已读
        else {
            QueryWrapper<Mark> markQueryWrapper2 = new QueryWrapper<>();
            markQueryWrapper2
                    .eq("user_id", userId)
                    .eq("content_id", contentId);
            List<Mark> MarkList = markMapper.selectList(markQueryWrapper2);
            Mark oneMark = new Mark();
            if (MarkList.size()>0) {
                oneMark = MarkList.get(0);
            }
            else{
                oneMark = null;
            }
            if (null == oneMark) return -1;
            oneMark.setIsRead(1);
            markMapper.updateById(oneMark);
        }
        return 1;
    }

    @Override
    public JSONObject getMarkMessageListVo_1(String userId, int pageNum, int pageSize) {
        //返回的结果结构
        JSONObject rsObject = new JSONObject();
        //获取该用户未读的mark记录数量
        int noReadCount= 0;
        QueryWrapper<Mark> markQueryWrapper2 = new QueryWrapper<>();
        markQueryWrapper2
                .eq("user_id", userId)
                .eq("is_mark", 1)
                .eq("is_read",0);
        noReadCount = markMapper.selectCount(markQueryWrapper2);
        rsObject.put("noReadCount",noReadCount);
        //如果是启动时的请求，只返回未读条数
        if(pageNum==-1){
            rsObject.put("markMessageList",null);
            return rsObject;
        }
        //分页获取该用户mark的帖子
        List<MarkMessageVO> markMessageList = new ArrayList<>();
        QueryWrapper<Mark> markQueryWrapper = new QueryWrapper<>();
        markQueryWrapper
                .eq("user_id", userId)
                .eq("is_mark", 1)
                .orderByDesc("last_comment_time");
        Page<Mark> markPage = new Page<>(pageNum, pageSize);
        IPage<Mark> marks = markMapper.selectPage(markPage, markQueryWrapper);
        if (null == marks) {
            return null;
        } else {
            //构建每个mark记录的MarkMessageVO
            List<Mark> markList = marks.getRecords();
            for (Mark mark : markList) {
                MarkMessageVO markMessageVo = new MarkMessageVO();
                //获取该帖子，该用户的Mark通知
                QueryWrapper<Remind> remindQueryWrapper = new QueryWrapper<>();
                remindQueryWrapper
                        .eq("userId", userId)
                        .eq("relateId", mark.getContentId())
                        .eq("type", 6)
                        .orderByDesc("createTime");
                Integer commentCount = remindMapper.selectCount(remindQueryWrapper);
                //根据mark的isRead属性判断，是否已读
                markMessageVo.setIsRead(mark.getIsRead());
                markMessageVo.setCreateTime(mark.getCreateTime());
                markMessageVo.setCommentCount(commentCount);
                markMessageVo.setLastCommentTime(mark.getLastCommnetTime());
                //获取帖子信息
                QueryWrapper<Content> contentQueryWrapper = new QueryWrapper<>();
                contentQueryWrapper.eq("contentId", mark.getContentId());
                Content content = contentMapper.selectOne(contentQueryWrapper);
                //过滤删除贴
                if (content.getContentState()==7) continue;
                //过滤私密贴和冻结贴
                if ( (content.getContentState()==6 ||content.getContentState()==12) && !Objects.equals(content.getUserId(), userId)) continue;
                markMessageVo.setContentId(mark.getContentId());
                if (null == content.getTitle() || "".equals(content.getTitle())) {
                    markMessageVo.setContentText(content.getContentText()); //内容
                } else {
                    markMessageVo.setContentText(content.getTitle() + " " + content.getContentText()); //内容
                }
                markMessageVo.setContentImage(content.getContentUrl()); //图片
                markMessageList.add(markMessageVo);
            }//构建完成
            //根据最新回复时间排序
            Collections.sort(markMessageList, new Comparator<MarkMessageVO>() {
                @Override
                public int compare(MarkMessageVO p1, MarkMessageVO p2) {
                    return (int) (p2.getCreateTime().getTime() - p1.getCreateTime().getTime());
                }
            });
            rsObject.put("markMessageList",markMessageList);
            return rsObject;
        }
    }

    @Override
    public JSONObject getMarkMessageListVo1231(String userId, int pageNum, int pageSize) {
        //返回的结果结构
        JSONObject rsObject = new JSONObject();
        //获取该用户未读的mark记录数量
        int noReadCount= 0;
        QueryWrapper<Mark> markQueryWrapper2 = new QueryWrapper<>();
        markQueryWrapper2
                .eq("user_id", userId)
                .eq("is_mark", 1)
                .eq("is_read",0);
        noReadCount = markMapper.selectCount(markQueryWrapper2);
        rsObject.put("noReadCount",noReadCount);
        //如果是启动时的请求，只返回未读条数
        if(pageNum==-1){
            rsObject.put("markMessageList",null);
            return rsObject;
        }
        //分页获取该用户mark的帖子
        List<MarkMessageVO> markMessageList = new ArrayList<>();
        System.out.println("开始查询"+new Date().getTime());
        markMessageList = markMapper.getMarkMessageVOListByUser1231(userId, (pageNum-1)*pageSize, pageSize);
        System.out.println("结束查询"+new Date().getTime());
        //对获取到的列表获取帖子信息
        for (MarkMessageVO markMessageVO : markMessageList){
            String contentId = markMessageVO.getContentId();
            HomeContentVO homeContentVO = contentMapper.getContentById(contentId);
            String title = homeContentVO.getTitle() == null ? "" : homeContentVO.getTitle();
            String contentText = homeContentVO.getContentText() == null ? "" : homeContentVO.getContentText();
            markMessageVO.setContentText(title + " " + contentText);
            markMessageVO.setContentImage(homeContentVO.getContentUrl());
        }
        if (null == markMessageList) {
            return null;
        } else {
            //构建数组
            String[] contentIdList = markMessageList.stream()
                    .map(MarkMessageVO::getContentId)
                    .toArray(String[]::new);
            //获取每个帖子对这个用户发了几条消息
            QueryWrapper<Remind> remindQueryWrapper = new QueryWrapper<>();
            remindQueryWrapper
                    .select("relateId", "count(*) as remindCount") // 选择relateId和计数，这里count(*)需要根据数据库进行调整
                    .eq("userId", userId)
                    .in("relateId", contentIdList)
                    .eq("type", 6)
                    .groupBy("relateId"); // 按照relateId分组

            List<Map<String, Object>> remindCountList = remindMapper.selectMaps(remindQueryWrapper);
            for (MarkMessageVO markMessageVO : markMessageList){
                String contentId = markMessageVO.getContentId();
                // 在remindCountList中寻找对应的relateId和其remindCount
                Integer count = remindCountList.stream()
                        .filter(map -> map.get("relateId").equals(contentId))
                        .findFirst()
                        .map(map -> ((Long) map.get("remindCount")).intValue()) // 从map中获取remindCount并将其转换为Integer
                        .orElse(0); // 如果没有匹配的结果，则默认为0

                // 将找到的remindCount值赋给commentCount
                markMessageVO.setCommentCount(count);
            }
            rsObject.put("markMessageList",markMessageList);
            return rsObject;
        }
    }


}
