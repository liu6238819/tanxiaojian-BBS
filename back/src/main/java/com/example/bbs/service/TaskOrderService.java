package com.example.bbs.service;

import com.alibaba.fastjson.JSONObject;
import com.example.bbs.pojo.TaskOrder;
import com.example.bbs.pojo.TaskOrderApplicant;
import com.example.bbs.pojo.vo.RankingVO;
import com.example.bbs.pojo.vo.TaskOrderVO;


import java.util.List;

public interface TaskOrderService {
    TaskOrder createOrder(TaskOrder order);
    List<TaskOrderVO> getOrderList(int orderKind, int orderState, int schoolId, String publisherId, int pageNum, int pageSize);
    int applyOrder(TaskOrderApplicant taskOrderApplicant);
    JSONObject getOrderDetail(String orderId,String userId);
    List<TaskOrderVO> getAppliedOrderList(String userId, int pageNum, int pageSize);
    int appealOrder(TaskOrder order);
    int cancelOrder(TaskOrder order);
    int confirmOrder(TaskOrder order);
    int finishOrder(TaskOrder order);
    void payedOrder(TaskOrder order);
    int selectApplicant(TaskOrderApplicant taskOrderApplicant);
    JSONObject getUserProfitRealTime(String userId, int schoolId, int duration);
    void applyCashOut(TaskOrderApplicant taskOrderApplicant) ;
    List<RankingVO> getProfitRanking(String orderId, int schoolId, int duration, int pageNum, int pageSize);


}
