package com.example.bbs.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.bbs.mapper.ChatMapper;
import com.example.bbs.mapper.TaskChatMapper;
import com.example.bbs.mapper.TaskOrderApplicantMapper;
import com.example.bbs.mapper.TaskOrderMapper;
import com.example.bbs.pojo.*;
import com.example.bbs.pojo.vo.ChatDetailVO;
import com.example.bbs.pojo.vo.MessageListVO;
import com.example.bbs.pojo.vo.TaskOrderVO;
import com.example.bbs.service.ChatService;
import com.example.bbs.service.UserService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ChatServiceImpl implements ChatService {
    @Autowired
    TaskOrderMapper taskOrderMapper;
    @Autowired
    TaskOrderApplicantMapper taskOrderApplicantMapper;
    @Autowired
    ChatMapper chatMapper;
    @Autowired
    TaskChatMapper taskChatMapper;
    @Autowired
    UserService userService;

    @Override
    public List<MessageListVO> getMessageList(String userId) {
        //普通私信
        List<MessageListVO> messageList = chatMapper.getMessageList(userId);
        for (MessageListVO chatVO : messageList)  {
            //获取私信者的认证状态，当前写死
            int currentUserState = userService.getUserStateBySchool(chatVO.getUserId(),1).getInteger("userState");
            chatVO.setUserState(currentUserState);
        }
        //订单私信,需更改消息类型
        List<MessageListVO> taskChatList = chatMapper.getTaskChatList(userId);
        for (MessageListVO taskChatVO : taskChatList) {
            taskChatVO.setReturnType("taskChatList");
            taskChatVO.setIsOpen(isChatOpen(userId, taskChatVO.getUserId(), taskChatVO.getOrderId()));
            //获取订单
            QueryWrapper<TaskOrder> taskOrderQueryWrapper = new QueryWrapper<>();
            taskOrderQueryWrapper.eq("order_id", taskChatVO.getOrderId());
            TaskOrder order = taskOrderMapper.selectOne(taskOrderQueryWrapper);
            if (null == order) continue;
            //赋值订单类型
            taskChatVO.setOrderKind(order.getOrderKind());

        }
        List<MessageListVO> result = new ArrayList<MessageListVO>();
        result.addAll(messageList);
        result.addAll(taskChatList);
        result.sort(Comparator.comparing(MessageListVO::getCreateTime).reversed());
        return result;
    }

    @Override
    public List<ChatDetailVO> getChatDetails(String userId, String senderId, int pageNum, int pageSize) {
        //获取所有聊天信息
        List<ChatDetailVO> chatDetails = chatMapper.getChatDetail(userId, senderId, (pageNum - 1) * pageSize, pageSize);
        //将未读消息设置为已读
        chatMapper.resetUnReadNum(userId, senderId);
        return chatDetails;
    }

    @Override
    public List<ChatDetailVO> getTaskChatDetail(String userId, String senderId, String orderId, int pageNum, int pageSize) {
        //获取所有聊天信息
        List<ChatDetailVO> chatDetails = chatMapper.getTaskChatDetail(userId, senderId, orderId, (pageNum - 1) * pageSize, pageSize);
        //将未读消息设置为已读
        chatMapper.resetUnReadNumTask(userId, senderId, orderId);
        return chatDetails;
    }

    @Override
    public void insertChat(Chat chat) {
        chatMapper.insert(chat);

    }

    @Override
    public void insertTaskChat(TaskChat taskChat) {
        taskChatMapper.insert(taskChat);

    }

    @Override
    public void deleteChat(String userId, String chaterId, String chatType, String orderId) {
        if (chatType.equals("messageList")) {
            chatMapper.deleteChat(userId, chaterId);

        } else if (chatType.equals("taskChatList")) {
            chatMapper.deleteTaskChat(userId, chaterId, orderId);
        }


    }

    @Override
    public int getUnReadNum(String userId) {
        QueryWrapper<Chat> chatQueryWrapper = new QueryWrapper<>();
        chatQueryWrapper
                .eq("receiverId", userId)
                .eq("messageType", 1)
                .eq("state", 1);
        List<Chat> chats = chatMapper.selectList(chatQueryWrapper);
        return chats.size();
    }

    private int isChatOpen(String userId, String chatterId, String orderId) {
        //获取订单
        QueryWrapper<TaskOrder> taskOrderQueryWrapper = new QueryWrapper<>();
        taskOrderQueryWrapper.eq("order_id", orderId);
        TaskOrder order = taskOrderMapper.selectOne(taskOrderQueryWrapper);
        if (null == order) return 0;
        TaskOrderVO orderVO = JSON.parseObject(JSON.toJSONString(order), TaskOrderVO.class);
        //获取订单接取关系
        QueryWrapper<TaskOrderApplicant> releation = new QueryWrapper<>();
        releation.eq("order_id", orderId);
        List<TaskOrderApplicant> records = taskOrderApplicantMapper.selectList(releation);
        //只有状态为0、1、2、3的订单可私信
        if (order.getOrderState() > 3) return 0;
        //两个id必有一个为订单发布者，另一个为接取者
        if (order.getPublisherId().equals(userId)) {
            for (int i = 0; i < records.size(); i++) {
                //寻人订单，订单状态为0，对话方在接取列表中
                if (order.getOrderKind() >= 0 && order.getOrderKind() <= 9 && order.getOrderState() == 0 && records.get(i).getApplicantId().equals(chatterId))
                    return 1;
                //寻人订单，订单状态为1,2,3，对话方在接取列表中且接取状态为1
                if (order.getOrderKind() >= 0 && order.getOrderKind() <= 9 && (order.getOrderState() == 1||order.getOrderState() == 2||order.getOrderState() == 3) && records.get(i).getApplicantId().equals(chatterId) && records.get(i).getApplicantState() == 1)
                    return 1;
                //非寻人订单，对话方在接取列表中且接取状态为1
                if (order.getOrderKind() > 9 && records.get(i).getApplicantId().equals(chatterId) && records.get(i).getApplicantState() == 1)
                    return 1;
            }
        }
        if (order.getPublisherId().equals(chatterId)) {
            for (int i = 0; i < records.size(); i++) {
                //寻人订单，订单状态为0，对话方在接取列表中
                if (order.getOrderKind() >= 0 && order.getOrderKind() <= 9 && order.getOrderState() == 0 && records.get(i).getApplicantId().equals(userId))
                    return 1;
                //寻人订单，订单状态为1,2,3，对话方在接取列表中且接取状态为1
                if (order.getOrderKind() >= 0 && order.getOrderKind() <= 9 && (order.getOrderState() == 1||order.getOrderState() == 2||order.getOrderState() == 3) && records.get(i).getApplicantId().equals(userId) && records.get(i).getApplicantState() == 1)
                    return 1;
                //非寻人订单，对话方在接取列表中且接取状态为1
                if (order.getOrderKind() > 9 && records.get(i).getApplicantId().equals(userId) && records.get(i).getApplicantState() == 1)
                    return 1;
            }
        }

        return 0;
    }


}
