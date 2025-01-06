package com.example.bbs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.bbs.pojo.Chat;
import com.example.bbs.pojo.vo.ChatDetailVO;
import com.example.bbs.pojo.vo.MessageListVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ChatMapper extends BaseMapper<Chat> {
    List<MessageListVO> getMessageList(@Param("userId") String userId);

    List<MessageListVO> getTaskChatList(@Param("userId") String userId);

    List<ChatDetailVO> getChatDetail(@Param("userId") String userId, @Param("senderId") String senderId, @Param("pageNum") int pageNum, @Param("pageSize") int pageSize);

    List<ChatDetailVO> getTaskChatDetail(@Param("userId") String userId, @Param("senderId") String senderId, @Param("orderId") String orderId, @Param("pageNum") int pageNum, @Param("pageSize") int pageSize);

    void resetUnReadNum(@Param("userId") String userId, @Param("senderId") String senderId);

    void resetUnReadNumTask(@Param("userId") String userId, @Param("senderId") String senderId, @Param("orderId") String orderId);

    void deleteChat(@Param("userId") String userId, @Param("chaterId") String chaterId);

    void deleteTaskChat(@Param("userId") String userId, @Param("chaterId") String chaterId, @Param("orderId") String orderId);

}
