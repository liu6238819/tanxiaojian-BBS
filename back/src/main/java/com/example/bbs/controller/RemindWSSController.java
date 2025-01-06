package com.example.bbs.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.bbs.component.SpringContext;
import com.example.bbs.pojo.Admire;
import com.example.bbs.pojo.Mark;
import com.example.bbs.pojo.vo.HomeCommentVO;
import com.example.bbs.pojo.vo.HomeContentVO;
import com.example.bbs.pojo.vo.MarkMessageVO;
import com.example.bbs.result.CommonResult;
import com.example.bbs.service.*;
import com.example.bbs.util.WebSocketUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@ServerEndpoint("/remindWSS/{userId}") //声明创建websocket的endpoint
@RestController
@RequestMapping("/remindWSS")
@Api(tags = "通知消息接口")
public class RemindWSSController {

    @Autowired
    MarkService markService;

    @Autowired
    RemindService reamindService;

    @Autowired
    FocusService focusService;

    @Autowired
    ContentService contentService;

    MarkService markServiceWSS = (MarkService) SpringContext.getBean("markServiceImpl");

    @ApiOperation("对马住/弃坑操作进行编辑")
    @PostMapping("/editMark")
    public CommonResult editMark(Mark mark ) {
        String userId = mark.getUserId();
        String contentId = mark.getContentId();
        int isMark = mark.getIsMark();
        int rs = markService.editMark(userId , contentId, isMark);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("contentId", mark.getContentId());
        if (rs == 1 || rs == 2) {
            jsonObject.put("isMark", 1);
            jsonObject.put("result", "马住操作成功！");
        } else if (rs == 0 ) {
            jsonObject.put("isMark", 0);
            jsonObject.put("result", "弃坑操作成功！");
        }
        else{
            return CommonResult.failed("马住操作失败！");
        }
        return CommonResult.success(jsonObject);

    }

    @ApiOperation("对马住记录的已读状态进行编辑")
    @PostMapping("/editMarkReadState")
    public CommonResult editMarkRead(Mark mark ) {
        String userId = mark.getUserId();
        String contentId = mark.getContentId();
        int rs = markService.editReadState(userId , contentId);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("contentId", mark.getContentId());
        if (rs == 1 ) {
            jsonObject.put("isRead", 1);
            jsonObject.put("result", "已读操作成功！");
        }
        else{
            return CommonResult.failed("已读操作失败！");
        }
        return CommonResult.success(jsonObject);

    }

    @ApiOperation("对该贴子相关的通知的已读状态进行编辑")
    @PostMapping("/editRemindsByContentId")
    public CommonResult editMarkRead(String userId ,String contentId ) {
        JSONObject rs =reamindService.editRemindByContentId(userId, contentId);
        JSONObject jsonObject = new JSONObject();
        if (rs != null ) {
            jsonObject.put("reduceCommentCount", rs.getString("reduceCommentCount"));
            jsonObject.put("reduceAdmireCount", rs.getString("reduceAdmireCount"));
            jsonObject.put("result", "已读操作成功！");
        }
        else{
            return CommonResult.failed("已读操作失败！");
        }
        return CommonResult.success(jsonObject);
    }


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
        if ("getMarkMessageList".equals(requestType)) {
            int pageNum = Integer.parseInt(messageObject.getString("pageNum"));
            int pageSize = Integer.parseInt(messageObject.getString("pageSize"));
            markServiceWSS.getMarkMessageListVo(userId,pageNum,pageSize);
        }
        if ("heartBeat".equals(requestType)) {
        }

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

    @ApiOperation("对马住/弃坑操作进行编辑")
    @PostMapping("/editMark_1")
    public CommonResult editMark_1(Mark mark ) {
        String userId = mark.getUserId();
        String contentId = mark.getContentId();
        int isMark = mark.getIsMark();
        int rs = markService.editMark_1(userId , contentId, isMark);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("contentId", mark.getContentId());
        if (rs == 1 || rs == 2) {
            jsonObject.put("isMark", 1);
            jsonObject.put("result", "马住操作成功！");
        } else if (rs == 0 ) {
            jsonObject.put("isMark", 0);
            jsonObject.put("result", "弃坑操作成功！");
        }
        else{
            return CommonResult.failed("马住操作失败！");
        }
        return CommonResult.success(jsonObject);

    }

    @ApiOperation("对马住记录的已读状态进行编辑")
    @PostMapping("/editMarkReadState_1")
    public CommonResult editMarkRead_1(Mark mark ) {
        String userId = mark.getUserId();
        String contentId = mark.getContentId();
        int rs = markService.editReadState_1(userId , contentId);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("contentId", mark.getContentId());
        if (rs == 1 ) {
            jsonObject.put("isRead", 1);
            jsonObject.put("result", "已读操作成功！");
        }
        else{
            return CommonResult.failed("已读操作失败！");
        }
        return CommonResult.success(jsonObject);

    }

    @ApiOperation("按页获取某个用户的mark记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "当前浏览用户id"),
            @ApiImplicitParam(name = "pageNum", value = "当前页数"),
            @ApiImplicitParam(name = "pageSize", value = "页面显示条数"),

    })
    @GetMapping("/getMarkMessageList_1")
    public CommonResult getMarkMessageListVo_1(String userId, int pageNum, int pageSize) {

        JSONObject markMessageListRS = markService.getMarkMessageListVo1231(userId,pageNum, pageSize);

        if (markMessageListRS != null) {
            return CommonResult.success("mark记录列表获取成功！", markMessageListRS);
        } else {
            return CommonResult.failed("mark记录列表获取失败");
        }
    }

    @ApiOperation("获取某个用户的关注发帖记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "当前浏览用户id"),
            @ApiImplicitParam(name = "schoolId", value = "学校id"),
    })
    @GetMapping("/searchFocusContentRecords")
    public CommonResult searchFocusContentRecords(String userId, int schoolId) {
        JSONObject focusContentRecords = focusService.searchFocusContentRecord0103(userId,schoolId);
        if (focusContentRecords != null) {
            return CommonResult.success("关注发帖记录获取成功！", focusContentRecords);
        } else {
            return CommonResult.failed("关注发帖记录获取失败");
        }
    }

    @ApiOperation("获取某个用户的关注发帖记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "当前浏览用户id"),
            @ApiImplicitParam(name = "schoolId", value = "学校id"),
    })
    @GetMapping("/updateFocusSearchTime")
    public CommonResult updateFocusSearchTime(String userId, int schoolId) {
        int focusContentRecords = focusService.updateFocusSearchTime(userId,schoolId);
        if (focusContentRecords != 0) {
            return CommonResult.success("更新关注查询时间记录成功！", focusContentRecords);
        } else {
            return CommonResult.failed("更新关注查询时间失败");
        }
    }

    @ApiOperation("获取某个用户的关注发帖列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "当前浏览用户id"),
            @ApiImplicitParam(name = "schoolId", value = "学校id"),
            @ApiImplicitParam(name = "queryTime", value = "请求时间"),
            @ApiImplicitParam(name = "pageSize", value = "页面显示条数"),
    })
    @GetMapping("/getFocusContentList")
    public CommonResult getFocusContentList(String userId, int schoolId, String queryTime, int pageSize) {
        List<HomeContentVO> homeContentVOList = contentService.getFocusContentPage(userId,schoolId,queryTime,pageSize);
        if (homeContentVOList != null) {
            return CommonResult.success("mark记录列表获取成功！", homeContentVOList);
        } else {
            return CommonResult.failed("mark记录列表获取失败");
        }
    }
}
