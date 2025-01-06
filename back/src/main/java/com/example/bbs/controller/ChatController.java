package com.example.bbs.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.bbs.component.SpringContext;
import com.example.bbs.pojo.Chat;
import com.example.bbs.pojo.TaskChat;
import com.example.bbs.pojo.vo.ChatDetailVO;
import com.example.bbs.pojo.vo.MessageListVO;
import com.example.bbs.result.CommonResult;
import com.example.bbs.service.ChatService;
import com.example.bbs.util.WebSocketUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@ServerEndpoint("/chat/{userId}") //声明创建websocket的endpoint
public class ChatController {

    ChatService chatService = (ChatService) SpringContext.getBean("chatServiceImpl");


    @OnOpen
    public void openSession(@PathParam("userId") String userId, Session session) {
        //存储用户
        WebSocketUtil.USERS_ONLINE.put(userId, session);
        //用户连接后台
        String message = "[" + userId + "]连接后台";
        System.out.println(message);
        log.info(message);
    }


    @OnClose
    public void closeSession(@PathParam("userId") String userId, Session session) {
        //删除用户
        WebSocketUtil.USERS_ONLINE.remove(userId);
        //向所有在线用户发送用户下线通知消息
        String message = "[" + userId + "]断开连接";
        log.info(message);
        //下线后关闭session
        try {
            session.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //接收前端发送的消息
    @OnMessage
    public void onMessage(@PathParam("userId") String userId, String message) {
        JSONObject messageObject = JSON.parseObject(message);
        String requestType = messageObject.getString("requestType");
        System.out.println(requestType);
        //用户建立连接后，给当前用户返回私信列表
        if ("messageList".equals(requestType)) {
            getMessageList(userId);
        }
        //打开聊天对话框，加载聊天历史记录
        if ("openWindow".equals(requestType)) {
            openWindow(userId, messageObject);
        }
        //在聊天窗口中发送消息
        if ("sendChat".equals(requestType)) {
            sendChat(userId, messageObject);
        }

        //打开任务聊天对话框，加载聊天历史记录
        if ("openTaskChat".equals(requestType)) {
            openTaskChat(userId, messageObject);
        }
        //在任务聊天窗口中发送消息
        if ("sendTaskChat".equals(requestType)) {
            sendTaskChat(userId, messageObject);
        }

        //删除聊天记录
        if ("deleteChat".equals(requestType)) {
            deleteChat(userId, messageObject);
        }
    }

    public void getMessageList(String userId) {
        //获取私信列表
        List<MessageListVO> messageListVOList = chatService.getMessageList(userId);
        CommonResult<List<MessageListVO>> rs = CommonResult.success(messageListVOList);
        JSONObject rsObject = JSONObject.parseObject(JSONObject.toJSON(rs).toString());
        rsObject.put("returnType", "messageList");
        String json = rsObject.toJSONString();
        WebSocketUtil.sendMessage(userId, json);
        log.info("获取用户:" + userId + "的消息" + json);
    }

    public void openWindow(String userId, JSONObject messageObject) {
        //私信发送者id
        String senderId = messageObject.getString("senderId");
        int pageNum = messageObject.getInteger("pageNum");
        int pageSize = messageObject.getInteger("pageSize");
        List<ChatDetailVO> chats = chatService.getChatDetails(userId, senderId, pageNum, pageSize);
        CommonResult<List<MessageListVO>> tmp = CommonResult.success(chats);
        JSONObject rsObject = JSONObject.parseObject(JSONObject.toJSON(tmp).toString());
        rsObject.put("returnType", "chatDetails");
        String chatDetaisJson = rsObject.toJSONString();
        WebSocketUtil.sendMessage(userId, chatDetaisJson);
        log.info("userId：" + userId + "获取了senderId：" + senderId + "的聊天详情。" + chatDetaisJson);
    }


    public void sendChat(String userId, JSONObject messageObject) {
        //获取接收者id
        String receiverId = messageObject.getString("receiverId");
        //获取发送消息内容
        String messageContent = messageObject.getString("messageContent");
        //消息入库,所有者为发送方
        Chat chat = new Chat();
        chat.setSenderId(userId);
        chat.setReceiverId(receiverId);
        chat.setUserId(userId);
        chat.setChaterId(receiverId);
        chat.setMessageContent(messageContent);
        chat.setMessageType(1);
        chat.setState(1);
        chatService.insertChat(chat);
        //消息入库,所有者为接收方
        Chat chat1 = new Chat();
        chat1.setSenderId(userId);
        chat1.setReceiverId(receiverId);
        chat1.setUserId(receiverId);
        chat1.setChaterId(userId);
        chat1.setMessageContent(messageContent);
        chat1.setMessageType(1);
        chat1.setState(1);
        chatService.insertChat(chat1);
        //消息入库,所有者为接收方
        JSONObject rsObject = JSONObject.parseObject(JSONObject.toJSON(chat).toString());
        rsObject.put("returnType", "newMessage");
        String newMessage = rsObject.toJSONString();
        WebSocketUtil.sendMessage(receiverId, newMessage);
        log.info("用户：" + userId + "给" + receiverId + "发了一条信息：" + newMessage);
        //更新接收者的消息列表
        getMessageList(receiverId);
    }

    public void openTaskChat(String userId, JSONObject messageObject) {
        //私信发送者id
        String senderId = messageObject.getString("senderId");
        String orderId = messageObject.getString("orderId");
        int pageNum = messageObject.getInteger("pageNum");
        int pageSize = messageObject.getInteger("pageSize");
        List<ChatDetailVO> chats = chatService.getTaskChatDetail(userId, senderId, orderId, pageNum, pageSize);
        CommonResult<List<MessageListVO>> tmp = CommonResult.success(chats);
        JSONObject rsObject = JSONObject.parseObject(JSONObject.toJSON(tmp).toString());
        rsObject.put("returnType", "taskChatDetails");
        String chatDetaisJson = rsObject.toJSONString();
        WebSocketUtil.sendMessage(userId, chatDetaisJson);
        log.info("userId：" + userId + "获取了senderId：" + senderId + "的聊天详情。" + chatDetaisJson);
    }

    public void sendTaskChat(String userId, JSONObject messageObject) {
        //获取接收者id
        String receiverId = messageObject.getString("receiverId");
        //获取发送消息内容
        String messageContent = messageObject.getString("messageContent");
        //获取订单id
        String orderId = messageObject.getString("orderId");
        //消息入库，所有者为发布方
        TaskChat chat = new TaskChat();
        chat.setSenderId(userId);
        chat.setReceiverId(receiverId);
        chat.setUserId(userId);
        chat.setChaterId(receiverId);
        chat.setMessageContent(messageContent);
        chat.setMessageType(1);
        chat.setMessageState(2);
        chat.setOrderId(orderId);
        chatService.insertTaskChat(chat);
        //消息入库，所有者为接收方
        TaskChat chat1 = new TaskChat();
        chat1.setSenderId(userId);
        chat1.setReceiverId(receiverId);
        chat1.setUserId(receiverId);
        chat1.setChaterId(userId);
        chat1.setMessageContent(messageContent);
        chat1.setMessageType(1);
        chat1.setMessageState(1);
        chat1.setOrderId(orderId);
        chatService.insertTaskChat(chat1);
        JSONObject rsObject = JSONObject.parseObject(JSONObject.toJSON(chat).toString());
        rsObject.put("returnType", "newMessage");
        String newMessage = rsObject.toJSONString();
        WebSocketUtil.sendMessage(receiverId, newMessage);
        log.info("用户：" + userId + "给" + receiverId + "发了一条订单私信：" + newMessage);
        //更新接收者的消息列表
        getMessageList(receiverId);
    }

    public void deleteChat(String userId, JSONObject messageObject) {
        //获取聊天对象id
        String chaterId = messageObject.getString("chaterId");
        //获取聊天类型
        String chatType = messageObject.getString("chatType");
        //获取订单id
        String orderId = messageObject.getString("orderId");
        //删除聊天记录
        chatService.deleteChat(userId, chaterId, chatType, orderId);
        //更新用户的消息列表
        getMessageList(userId);
    }

    @OnError
    public void sessionError(Session session, Throwable throwable) {
        try {
            session.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info("WebSocket连接发生异常，message:" + throwable.getMessage());
    }

}