package com.example.bbs.service;

import com.example.bbs.pojo.Chat;
import com.example.bbs.pojo.TaskChat;
import com.example.bbs.pojo.vo.ChatDetailVO;
import com.example.bbs.pojo.vo.MessageListVO;

import java.util.List;

public interface ChatService {

    List<MessageListVO> getMessageList(String userId);

    List<ChatDetailVO> getChatDetails(String userId, String senderId, int pageNum, int pageSize);

    List<ChatDetailVO> getTaskChatDetail(String userId, String senderId,String orderId, int pageNum, int pageSize);

    void insertChat(Chat chat);
    void insertTaskChat(TaskChat taskChat);

    void deleteChat(String userId,String chaterId,String chatType,String orderId);
    int getUnReadNum(String userId);
}
