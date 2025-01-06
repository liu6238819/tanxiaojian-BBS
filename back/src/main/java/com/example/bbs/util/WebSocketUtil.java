package com.example.bbs.util;


import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketUtil {
 
    /**
     * 模拟存储   在线用户
     */
    public static final Map<String, Session> USERS_ONLINE = new ConcurrentHashMap<>();
 
    /**
     * 向所有在线用户发送消息(遍历 向每一个用户发送)
     * @param message
     */
    public static void sendMessageToAllOnlineUser(String message){
        USERS_ONLINE.forEach((userId, Session) -> sendMessage(userId, message));
    }

    //向指定用户发送消息
    public static void sendMessage(String userId,String message){
        Session session = USERS_ONLINE.get(userId);
        if (session == null) {
            return;
        }

        final RemoteEndpoint.Basic basic = session.getBasicRemote();
        if (basic == null) {
            return;
        }

        try {
            basic.sendText(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
 
}