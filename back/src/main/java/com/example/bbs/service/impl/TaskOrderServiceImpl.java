package com.example.bbs.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.bbs.controller.ChatController;
import com.example.bbs.mapper.*;
import com.example.bbs.pojo.*;
import com.example.bbs.pojo.vo.RankingVO;
import com.example.bbs.pojo.vo.TaskOrderVO;
import com.example.bbs.service.TaskOrderService;
import com.example.bbs.util.WebSocketUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class TaskOrderServiceImpl implements TaskOrderService {
    @Autowired
    TaskOrderMapper taskOrderMapper;
    @Autowired
    TaskOrderApplicantMapper taskOrderApplicantMapper;
    @Autowired
    TaskChatMapper taskChatMapper;
    @Autowired
    UserMapper userMapper;


    @Override
    public TaskOrder createOrder(TaskOrder order) {
        TaskOrder result = new TaskOrder();
        result.setApplicantMajor(order.getApplicantMajor());
        result.setApplicantQualification(order.getApplicantQualification());
        result.setOrderContent(order.getOrderContent());
        result.setOrderImg(order.getOrderImg());
        result.setOrderCost(order.getOrderCost());
        result.setOrderDeadline(order.getOrderDeadline());
        result.setOrderEndPlace(order.getOrderEndPlace());
        result.setOrderKind(order.getOrderKind());
        result.setOrderRemark(order.getOrderRemark());
        result.setOrderStartPlace(order.getOrderStartPlace());
        result.setPublisherId(order.getPublisherId());
        result.setSchoolId(order.getSchoolId());
        result.setOrderState(-1);
        taskOrderMapper.insert(result);
        return result;

    }

    @Override
    public List<TaskOrderVO> getOrderList(int orderKind, int orderState, int schoolId, String publisherId, int pageNum, int pageSize) {
        List<TaskOrder> taskOrderList = null;
        taskOrderList = taskOrderMapper.selectTaskOrderList(orderKind, orderState, schoolId, publisherId, (pageNum - 1) * pageSize, pageSize);
        List<TaskOrderVO> taskOrderVOListList = JSON.parseArray(JSON.toJSONString(taskOrderList), TaskOrderVO.class);
        for (int i = 0; i < taskOrderVOListList.size(); i++) {
            //获取用户信息
            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.eq("userId", taskOrderVOListList.get(i).getPublisherId());
            User user = userMapper.selectOne(userQueryWrapper);
            if (null != user) {
                taskOrderVOListList.get(i).setPublisherHeadImgUrl(user.getHeadimgUrl());
                taskOrderVOListList.get(i).setPublisherNickName(user.getNickName());
                taskOrderVOListList.get(i).setOpenId(user.getOpenId());
            }
            //寻人类订单，返回接取者列表
            if (taskOrderVOListList.get(i).getOrderKind() >= 0 && taskOrderVOListList.get(i).getOrderKind() <= 9) {//找人类订单
                //获取订单接取关系
                QueryWrapper<TaskOrderApplicant> releation = new QueryWrapper<>();
                releation.eq("order_id", taskOrderVOListList.get(i).getOrderId());
                List<TaskOrderApplicant> records = taskOrderApplicantMapper.selectList(releation);
                taskOrderVOListList.get(i).setApplicants(records);
            }


        }

        return taskOrderVOListList;
    }

    public int applyOrder(TaskOrderApplicant taskOrderApplicant) {
        int result = 1;
        //获取订单
        QueryWrapper<TaskOrder> taskOrderQueryWrapper = new QueryWrapper<>();
        taskOrderQueryWrapper.eq("order_id", taskOrderApplicant.getOrderId());
        TaskOrder order = taskOrderMapper.selectOne(taskOrderQueryWrapper);
        //获取订单接取关系
        QueryWrapper<TaskOrderApplicant> releation = new QueryWrapper<>();
        releation.eq("order_id", taskOrderApplicant.getOrderId());
        List<TaskOrderApplicant> records = taskOrderApplicantMapper.selectList(releation);
        if (order.getPublisherId() == taskOrderApplicant.getApplicantId()) return 4; //本人不可接取
        if (order.getOrderState() != 0) return 2; //订单已被接取（状态不为0）
        //初始化发布方私信
        TaskChat taskChat = new TaskChat();
        taskChat.setOrderId(taskOrderApplicant.getOrderId());
        taskChat.setMessageType(1);
        taskChat.setMessageState(2);//已读
        taskChat.setSenderId(taskOrderApplicant.getApplicantId());
        taskChat.setReceiverId(order.getPublisherId());
        taskChat.setUserId(taskOrderApplicant.getApplicantId());
        taskChat.setChaterId(order.getPublisherId());
        //初始化接收方私信
        TaskChat taskChat1 = new TaskChat();
        taskChat1.setOrderId(taskOrderApplicant.getOrderId());
        taskChat1.setMessageType(1);
        taskChat1.setMessageState(1);
        taskChat1.setSenderId(taskOrderApplicant.getApplicantId());
        taskChat1.setReceiverId(order.getPublisherId());
        taskChat1.setUserId(order.getPublisherId());
        taskChat1.setChaterId(taskOrderApplicant.getApplicantId());
        //业务逻辑
        if (order.getOrderKind() >= 0 && order.getOrderKind() <= 9) {//找人类订单
            for (int i = 0; i < records.size(); i++) {
                if (records.get(i).getApplicantId().equals(taskOrderApplicant.getApplicantId()))
                    return 3;//重复申请
            }
            taskOrderApplicant.setApplicantState(0);//状态初始化为未被选择
            taskOrderApplicantMapper.insert(taskOrderApplicant);
            taskChat.setMessageContent("我可以联系到您所需要的人，欢迎进一步沟通。");
            taskChat1.setMessageContent("我可以联系到您所需要的人，欢迎进一步沟通。");
        } else {
            taskOrderApplicant.setApplicantState(1);
            if (null == records || records.size() == 0) {
                taskOrderApplicantMapper.insert(taskOrderApplicant);
                order.setOrderState(1);
                order.setStartTime(new Date());
                taskOrderMapper.updateById(order);//非找人类订单，更改订单状态为进行中
                taskChat.setMessageContent("我接取了您的订单，有问题请随时私信或电话联系。");
                taskChat1.setMessageContent("我接取了您的订单，有问题请随时私信或电话联系。");
            } else {
                return 2;//订单已被接取（非跑腿类订单只允许一个接取人，关系表中已存在记录）
            }
        }
        //私信入库
        taskChatMapper.insert(taskChat);
        taskChatMapper.insert(taskChat1);
        //通过websocket发送通知
        JSONObject rsObject = JSONObject.parseObject(JSONObject.toJSON(taskChat).toString());
        rsObject.put("returnType", "newMessage");
        String newMessage = rsObject.toJSONString();
        WebSocketUtil.sendMessage(order.getPublisherId(), newMessage);
        //更新双方的消息列表
        ChatController chatController = new ChatController();
        chatController.getMessageList(order.getPublisherId());
        chatController.getMessageList(taskOrderApplicant.getApplicantId());
        return result;
    }

    @Override
    public JSONObject getOrderDetail(String orderId, String userId) {
        //获取订单
        QueryWrapper<TaskOrder> taskOrderQueryWrapper = new QueryWrapper<>();
        taskOrderQueryWrapper.eq("order_id", orderId);
        TaskOrder order = taskOrderMapper.selectOne(taskOrderQueryWrapper);
        if (null == order) return null;
        TaskOrderVO orderVO = JSON.parseObject(JSON.toJSONString(order), TaskOrderVO.class);
        //获取发布者信息
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("userId", orderVO.getPublisherId());
        User user = userMapper.selectOne(userQueryWrapper);
        if (null != user) {
            orderVO.setPublisherHeadImgUrl(user.getHeadimgUrl());
            orderVO.setPublisherNickName(user.getNickName());
            orderVO.setPhone(user.getPhone());
            orderVO.setOpenId(user.getOpenId());
        }

        //获取订单接取关系
        QueryWrapper<TaskOrderApplicant> releation = new QueryWrapper<>();
        releation.eq("order_id", orderId);
        List<TaskOrderApplicant> records = taskOrderApplicantMapper.selectList(releation);
        //关系详情及对应接取人信息存入json数组
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < records.size(); i++) {
            JSONObject record = new JSONObject();
            record.put("order_applicant", records.get(i));
            //获取用户信息
            QueryWrapper<User> applyerQuery = new QueryWrapper<>();
            applyerQuery.eq("userId", records.get(i).getApplicantId());
            User applyer = userMapper.selectOne(applyerQuery);
            JSONObject applicant = new JSONObject();
            applicant.put("userId", applyer.getUserId());
            applicant.put("nickName", applyer.getNickName());
            applicant.put("headimgUrl", applyer.getHeadimgUrl());
            applicant.put("phone", applyer.getPhone());
            applicant.put("openId", applyer.getOpenId());
            record.put("applicantInfo", applicant);
            jsonArray.add(record);
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("orderInfo", orderVO);
        jsonObject.put("applicantRecord", jsonArray);

        return jsonObject;
    }

    @Override
    public List<TaskOrderVO> getAppliedOrderList(String userId, int pageNum, int pageSize) {
        List<TaskOrderVO> taskOrderVOList = new ArrayList<TaskOrderVO>();
        QueryWrapper<TaskOrderApplicant> taskOrderApplicantQueryWrapper = new QueryWrapper<>();
        taskOrderApplicantQueryWrapper.eq("applicant_id", userId);
        taskOrderApplicantQueryWrapper.orderByDesc("create_time");
        Page<TaskOrderApplicant> taskOrderApplicantPage = new Page<>(pageNum, pageSize);
        IPage<TaskOrderApplicant> taskOrderApplicantIPage = taskOrderApplicantMapper.selectPage(taskOrderApplicantPage, taskOrderApplicantQueryWrapper);
        List<TaskOrderApplicant> taskOrderApplicantList = taskOrderApplicantIPage.getRecords();


        for (int i = 0; i < taskOrderApplicantList.size(); i++) {
            TaskOrderApplicant record = taskOrderApplicantList.get(i);
            //获取订单
            QueryWrapper<TaskOrder> taskOrderQueryWrapper = new QueryWrapper<>();
            taskOrderQueryWrapper.eq("order_id", record.getOrderId());
            TaskOrder order = taskOrderMapper.selectOne(taskOrderQueryWrapper);
            if (null == order) continue;
            TaskOrderVO orderVO = JSON.parseObject(JSON.toJSONString(order), TaskOrderVO.class);
            //获取用户信息
            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.eq("userId", orderVO.getPublisherId());
            User user = userMapper.selectOne(userQueryWrapper);
            if (null != user) {
                orderVO.setPublisherHeadImgUrl(user.getHeadimgUrl());
                orderVO.setPublisherNickName(user.getNickName());
                orderVO.setPhone(user.getPhone());
                orderVO.setOpenId(user.getOpenId());
            }
            //设置接取状态
            orderVO.setApplicantState(record.getApplicantState());
            taskOrderVOList.add(orderVO);
        }

        return taskOrderVOList;
    }

    @Override
    public int appealOrder(TaskOrder order) {
        int result = 1;
        //获取订单
        QueryWrapper<TaskOrder> taskOrderQueryWrapper = new QueryWrapper<>();
        taskOrderQueryWrapper.eq("order_id", order.getOrderId());
        TaskOrder record = taskOrderMapper.selectOne(taskOrderQueryWrapper);
        if (null == record) return 2; //订单不存在
        if (!record.getPublisherId().equals(order.getPublisherId())) return 3;//非订单发布者
        //申诉业务逻辑
        record.setAppealReason(order.getAppealReason());
        record.setAppealTime(new Date());
        record.setOrderState(6);
        taskOrderMapper.updateById(record);

        return result;
    }

    @Override
    public int cancelOrder(TaskOrder order) {
        int result = 1;
        //获取订单
        QueryWrapper<TaskOrder> taskOrderQueryWrapper = new QueryWrapper<>();
        taskOrderQueryWrapper.eq("order_id", order.getOrderId());
        TaskOrder record = taskOrderMapper.selectOne(taskOrderQueryWrapper);
        if (null == record) return 2; //订单不存在
        if (!record.getPublisherId().equals(order.getPublisherId())) return 3;//非订单发布者
        //取消业务逻辑
        if (record.getOrderState() != 0) return 4;//只有进行中订单才可取消
        record.setOrderState(5);
        taskOrderMapper.updateById(record);
        return result;
    }

    @Override
    public int confirmOrder(TaskOrder order) {
        int result = 1;
        //获取订单
        QueryWrapper<TaskOrder> taskOrderQueryWrapper = new QueryWrapper<>();
        taskOrderQueryWrapper.eq("order_id", order.getOrderId());
        TaskOrder record = taskOrderMapper.selectOne(taskOrderQueryWrapper);
        if (null == record) return 2; //订单不存在
        //确认业务逻辑
        //订单接取者与订单为1对1关系，且接取状态已接取
        QueryWrapper<TaskOrderApplicant> releation = new QueryWrapper<>();
        releation.eq("order_id", record.getOrderId());
        releation.eq("applicant_state", 1);
        TaskOrderApplicant taskOrderApplicant = taskOrderApplicantMapper.selectOne(releation);
        if (null == taskOrderApplicant || !taskOrderApplicant.getApplicantId().equals(order.getPublisherId()))
            return 3;//非订单接取者
        //确认业务逻辑
        if (record.getOrderState() != 1) return 4;//只有进行中订单才可确认
        record.setOrderState(2);
        record.setConfirmTime(new Date());
        taskOrderMapper.updateById(record);
        return result;
    }

    @Override
    public int finishOrder(TaskOrder order) {
        int result = 1;
        //获取订单
        QueryWrapper<TaskOrder> taskOrderQueryWrapper = new QueryWrapper<>();
        taskOrderQueryWrapper.eq("order_id", order.getOrderId());
        TaskOrder record = taskOrderMapper.selectOne(taskOrderQueryWrapper);
        if (null == record) return 2; //订单不存在
        if (!record.getPublisherId().equals(order.getPublisherId())) return 3;//非订单发布者
        //结束业务逻辑
        //订单状态为2或3方可结束
        if (record.getOrderState() == 2 || record.getOrderState() == 3) {
            record.setOrderState(4);
            record.setFinishTime(new Date());
            taskOrderMapper.updateById(record);
        } else return 4;//订单状态异常

        return result;
    }

    @Override
    public void payedOrder(TaskOrder order) {
        //获取订单
        QueryWrapper<TaskOrder> taskOrderQueryWrapper = new QueryWrapper<>();
        taskOrderQueryWrapper.eq("order_id", order.getOrderId());
        TaskOrder record = taskOrderMapper.selectOne(taskOrderQueryWrapper);
        //修改订单状态
        record.setOrderState(0);
        taskOrderMapper.updateById(record);
    }

    @Override
    public int selectApplicant(TaskOrderApplicant taskOrderApplicant) {
        int result = 1;
        //获取订单
        QueryWrapper<TaskOrder> taskOrderQueryWrapper = new QueryWrapper<>();
        taskOrderQueryWrapper.eq("order_id", taskOrderApplicant.getOrderId());
        TaskOrder record = taskOrderMapper.selectOne(taskOrderQueryWrapper);
        //获取接取信息
        QueryWrapper<TaskOrderApplicant> taskOrderApplicantQueryWrapper = new QueryWrapper<>();
        taskOrderApplicantQueryWrapper.eq("order_id", taskOrderApplicant.getOrderId());
        taskOrderApplicantQueryWrapper.eq("applicant_id", taskOrderApplicant.getApplicantId());
        TaskOrderApplicant info = taskOrderApplicantMapper.selectOne(taskOrderApplicantQueryWrapper);
        if (null == record) return 2; //订单不存在
        if (null == info) return 3; //接取信息不存在
        if (record.getOrderKind() > 9 || record.getOrderKind() < 0) return 4;//非寻人订单
        //选择接取人业务逻辑
        //订单状态为进行中方可选择
        if (record.getOrderState() == 0) {
            record.setOrderState(1);//订单状态进行中
            record.setStartTime(new Date());
            taskOrderMapper.updateById(record);
            info.setApplicantState(1);//接取人状态修改
            taskOrderApplicantMapper.updateById(info);
        } else return 5;//订单状态异常

        return result;
    }

    @Override
    public JSONObject getUserProfitRealTime(String userId, int schoolId, int duration) {
        int queryType = 0;
        //查询类型1：总收益，订单state为4、10、11
        queryType = 1;
        List<TaskOrderVO> totalProfitList = getProfitByParamsVO(userId, schoolId, queryType, duration);
        BigDecimal totalProfit = sumOrderCost(totalProfitList);
        //查询类型2：可提现收益，订单state为4
        queryType = 2;
        List<TaskOrderVO> canCashOutList = getProfitByParamsVO(userId, schoolId, queryType, duration);
        BigDecimal canCashOut = sumOrderCost(canCashOutList);
        //查询类型3：提现中收益，订单state为10
        queryType = 3;
        List<TaskOrderVO> inCashOutList = getProfitByParamsVO(userId, schoolId, queryType, duration);
        BigDecimal inCashOut = sumOrderCost(inCashOutList);
        //查询类型4：已提现收益，订单state为11
        queryType = 4;
        List<TaskOrderVO> alreadyCashOutList = getProfitByParamsVO(userId, schoolId, queryType, duration);
        BigDecimal alreadyCashOut = sumOrderCost(alreadyCashOutList);
        //查询类型5：累计x日收益，订单state为4、10、11，订单完成时间在x-now之间
        queryType = 5;
        List<TaskOrderVO> totalProfitInDaysList = getProfitByParamsVO(userId, schoolId, queryType, duration);
        BigDecimal totalProfitInDays = sumOrderCost(totalProfitInDaysList);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("totalProfit", totalProfit);
        jsonObject.put("canCashOut", canCashOut);
        jsonObject.put("inCashOut", inCashOut);
        jsonObject.put("alreadyCashOut", alreadyCashOut);
        jsonObject.put("totalProfitInDays", totalProfitInDays);
        return jsonObject;
    }

    @Override
    public void applyCashOut(TaskOrderApplicant applicant) {
        //获取该用户可提现收益列表
        List<TaskOrder> canCashOutList = getProfitByParams(applicant.getApplicantId(), applicant.getApplicantState(), 2, 0);
        //修改订单状态
        for (int i = 0; i < canCashOutList.size(); i++) {
            TaskOrder order = canCashOutList.get(i);
            order.setOrderState(10);
            taskOrderMapper.updateById(order);
        }

    }

    @Override
    public List<RankingVO> getProfitRanking(String userId, int schoolId, int duration, int pageNum, int pageSize) {
        List<RankingVO> rankingVOList = taskOrderMapper.selectProfitRanking(userId, schoolId, duration, (pageNum - 1) * pageSize, pageSize);
        return rankingVOList;
    }

    //根据参数获取全部有收益的订单(VO)
    private List<TaskOrderVO> getProfitByParamsVO(String userId, int schoolId, int queryType, int duration) {
        List<TaskOrder> taskOrderList = null;
        taskOrderList = taskOrderMapper.selectProfitOrderList(userId, schoolId, queryType, duration);
        List<TaskOrderVO> taskOrderVOListList = JSON.parseArray(JSON.toJSONString(taskOrderList), TaskOrderVO.class);
        return taskOrderVOListList;
    }

    //根据参数获取全部有收益的订单
    private List<TaskOrder> getProfitByParams(String userId, int schoolId, int queryType, int duration) {
        List<TaskOrder> taskOrderList = null;
        taskOrderList = taskOrderMapper.selectProfitOrderList(userId, schoolId, queryType, duration);
        return taskOrderList;
    }

    //统计订单金额
    private BigDecimal sumOrderCost(List<TaskOrderVO> taskOrderVOList) {
        BigDecimal totalAmount = new BigDecimal(0);
        for (int i = 0; i < taskOrderVOList.size(); i++) {
            totalAmount = totalAmount.add(taskOrderVOList.get(i).getOrderCost());
        }
        return totalAmount;
    }


}
