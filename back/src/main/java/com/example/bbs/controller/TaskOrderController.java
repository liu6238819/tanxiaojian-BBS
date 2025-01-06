package com.example.bbs.controller;


import com.alibaba.fastjson.JSONObject;
import com.example.bbs.pojo.TaskOrder;
import com.example.bbs.pojo.TaskOrderApplicant;
import com.example.bbs.pojo.vo.RankingVO;
import com.example.bbs.pojo.vo.TaskOrderVO;
import com.example.bbs.result.CommonResult;
import com.example.bbs.service.TaskOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/order")
@Api(tags = "订单相关操作")
public class TaskOrderController {
    @Autowired
    TaskOrderService taskOrderService;

    @ApiOperation("创建订单")
    @PostMapping("/createOrder")
    public CommonResult createOrder(@RequestBody TaskOrder order) {
        try {
            TaskOrder taskOrder = taskOrderService.createOrder(order);
            return CommonResult.success("订单创建成功！", taskOrder);
        } catch (Exception e) {
            log.warn("订单创建失败！订单信息：" + order);
            return CommonResult.failed(e.getMessage());
        }
    }

    @ApiOperation("获取订单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "当前浏览用户id", required = true),
            @ApiImplicitParam(name = "publisherId", value = "发布者id"),
            @ApiImplicitParam(name = "orderKind", value = "订单类型"),
            @ApiImplicitParam(name = "orderState", value = "订单状态"),
            @ApiImplicitParam(name = "schoolId", value = "学校id", required = true),
            @ApiImplicitParam(name = "pageNum", value = "当前页数", required = true),
            @ApiImplicitParam(name = "pageSize", value = "页面显示条数", required = true),

    })
    @GetMapping("/getOrderList")
    public CommonResult getOrderList(String userId, String publisherId, @RequestParam(defaultValue = "-1") int orderKind, @RequestParam(defaultValue = "-1") int orderState, int pageNum, int pageSize, @RequestParam(defaultValue = "-1") int schoolId) {
        if (StringUtils.isBlank(userId)) {
            return CommonResult.failed("非法用户访问");
        }
        try {
            List<TaskOrderVO> taskOrderVOList = null;
            taskOrderVOList = taskOrderService.getOrderList(orderKind, orderState, schoolId, publisherId, pageNum, pageSize);
            log.info("订单列表获取成功！");
            return CommonResult.success("订单列表获取成功！", taskOrderVOList);
        } catch (Exception e) {
            log.info("订单列表获取失败！");
            log.info(e.getMessage());
            return CommonResult.failed(e.getMessage());
        }
    }

    @ApiOperation("获取订单详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "当前用户id", required = true),
            @ApiImplicitParam(name = "orderId", value = "订单id", required = true),

    })
    @GetMapping("/getOrderDetail")
    public CommonResult getOrderDetail(String userId, String orderId) {
        //log.info("接口请求用户："+userId);
        if (StringUtils.isBlank(userId)) {
            return CommonResult.failed("非法用户访问");
        }
        try {
            JSONObject result = null;
            result = taskOrderService.getOrderDetail(orderId, userId);
            log.info("订单详情获取成功！");
            return CommonResult.success("订单详情获取成功！", result);
        } catch (Exception e) {
            log.info("订单详情获取失败！");
            log.info(e.getMessage());
            return CommonResult.failed(e.getMessage());
        }
    }

    @ApiOperation("接取订单")
    @PostMapping("/applyOrder")
    public CommonResult applyOrder(@RequestBody TaskOrderApplicant taskOrderApplicant) {
        try {
            int result = taskOrderService.applyOrder(taskOrderApplicant);
            if (result == 2) return CommonResult.failed("申请失败，该订单已被接取！");
            if (result == 3) return CommonResult.failed("申请失败，请勿重复接取！");
            if (result == 4) return CommonResult.failed("申请失败，本人不可接取！");
            return CommonResult.success("申请接单成功！");

        } catch (Exception e) {
            log.warn("申请失败！：" + e.getMessage());
            return CommonResult.failed("申请失败，接口异常！");
        }
    }

    @ApiOperation("获取用户接取的订单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "接取者id", required = true),
            @ApiImplicitParam(name = "pageNum", value = "当前页数", required = true),
            @ApiImplicitParam(name = "pageSize", value = "页面显示条数", required = true),

    })
    @GetMapping("/getAppliedOrderList")
    public CommonResult getAppliedOrderList(String userId, int pageNum, int pageSize) {
        try {
            List<TaskOrderVO> taskOrderVOList = null;
            taskOrderVOList = taskOrderService.getAppliedOrderList(userId, pageNum, pageSize);
            log.info("接取列表获取成功！");
            return CommonResult.success("接取列表获取成功！", taskOrderVOList);

        } catch (Exception e) {
            log.warn("申请失败！：" + e.getMessage());
            return CommonResult.failed("申请失败，接口异常！");
        }
    }

    @ApiOperation("申诉订单")
    @PostMapping("/appealOrder")
    public CommonResult appealOrder(@RequestBody TaskOrder order) {
        if (StringUtils.isBlank(order.getPublisherId())) {
            return CommonResult.failed("非法用户访问");
        }
        try {
            int result = taskOrderService.appealOrder(order);
            if (result == 2) return CommonResult.failed("申请失败，订单不存在！");
            if (result == 3) return CommonResult.failed("申请失败，非订单发布者！");
            return CommonResult.success("申诉提交成功！");

        } catch (Exception e) {
            log.warn("申请失败！：" + e.getMessage());
            return CommonResult.failed("申请失败，接口异常！");
        }
    }

    @ApiOperation("取消订单")
    @PostMapping("/cancelOrder")
    public CommonResult cancelOrder(@RequestBody TaskOrder order) {
        if (StringUtils.isBlank(order.getPublisherId())) {
            return CommonResult.failed("非法用户访问");
        }
        try {
            int result = taskOrderService.cancelOrder(order);
            if (result == 2) return CommonResult.failed("申请失败，订单不存在！");
            if (result == 3) return CommonResult.failed("申请失败，非订单发布者！");
            if (result == 4) return CommonResult.failed("申请失败，订单状态异常！");
            return CommonResult.success("申诉提交成功！");

        } catch (Exception e) {
            log.warn("申请失败！：" + e.getMessage());
            return CommonResult.failed("申请失败，接口异常！");
        }
    }

    @ApiOperation("确认订单")
    @PostMapping("/confirmOrder")
    public CommonResult confirmOrder(@RequestBody TaskOrder order) {
        if (StringUtils.isBlank(order.getPublisherId())) {
            return CommonResult.failed("非法用户访问");
        }
        try {
            int result = taskOrderService.confirmOrder(order);
            if (result == 2) return CommonResult.failed("申请失败，订单不存在！");
            if (result == 3) return CommonResult.failed("申请失败，非订单接取者！");
            if (result == 4) return CommonResult.failed("申请失败，订单状态异常！");
            return CommonResult.success("申诉提交成功！");

        } catch (Exception e) {
            log.warn("申请失败！：" + e.getMessage());
            return CommonResult.failed("申请失败，接口异常！");
        }
    }

    @ApiOperation("结束订单")
    @PostMapping("/finishOrder")
    public CommonResult finishOrder(@RequestBody TaskOrder order) {
        if (StringUtils.isBlank(order.getPublisherId())) {
            return CommonResult.failed("非法用户访问");
        }
        try {
            int result = taskOrderService.finishOrder(order);
            if (result == 2) return CommonResult.failed("申请失败，订单不存在！");
            if (result == 3) return CommonResult.failed("申请失败，非订单发布者！");
            if (result == 4) return CommonResult.failed("申请失败，订单状态异常！");
            return CommonResult.success("申诉提交成功！");

        } catch (Exception e) {
            log.warn("申请失败！：" + e.getMessage());
            return CommonResult.failed("申请失败，接口异常！");
        }
    }

    @ApiOperation("支付完成")
    @PostMapping("/payedOrder")
    public CommonResult payedOrder(@RequestBody TaskOrder order) {
        if (StringUtils.isBlank(order.getPublisherId())) {
            return CommonResult.failed("非法用户访问");
        }
        if (!order.getOrderRemark().equals("fromWX")) {
            log.warn("非法请求来源！");
            return CommonResult.failed("来源异常");
        }
        try {
            taskOrderService.payedOrder(order);
            return CommonResult.success("订单状态修改成功！");
        } catch (Exception e) {
            log.warn("订单状态修改失败！");
            return CommonResult.failed(e.getMessage());
        }
    }

    @ApiOperation("选择接取人")
    @PostMapping("/selectApplicant")
    public CommonResult selectApplicant(@RequestBody TaskOrderApplicant taskOrderApplicant) {
        if (StringUtils.isBlank(taskOrderApplicant.getApplicantId())) {
            return CommonResult.failed("非法用户访问");
        }
        try {
            int result = taskOrderService.selectApplicant(taskOrderApplicant);
            if (result == 2) return CommonResult.failed("申请失败，订单不存在！");
            if (result == 3) return CommonResult.failed("申请失败，接取记录不存在！");
            if (result == 4) return CommonResult.failed("申请失败，不是寻人订单！");
            if (result == 5) return CommonResult.failed("申请失败，订单状态异常！");
            return CommonResult.success("接取人选择成功！");
        } catch (Exception e) {
            log.warn("订单状态修改失败！");
            return CommonResult.failed(e.getMessage());
        }
    }

    @ApiOperation("获取实时的用户收益统计")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "接取者id", required = true),
            @ApiImplicitParam(name = "schoolId", value = "学校id", required = true),
            @ApiImplicitParam(name = "duration", value = "统计天数"),

    })
    @GetMapping("/getUserProfitRealTime")
    public CommonResult getUserProfitRealTime(String userId, int schoolId, int duration) {
        try {
            JSONObject result = null;
            result = taskOrderService.getUserProfitRealTime(userId, schoolId, duration);
            return CommonResult.success("收益统计获取成功！", result);

        } catch (Exception e) {
            log.warn("申请失败！：" + e.getMessage());
            return CommonResult.failed("收益统计申请失败，接口异常！");
        }
    }


    @ApiOperation("申请提现")
    @PostMapping("/applyCashOut")
    public CommonResult applyCashOut(@RequestBody TaskOrderApplicant taskOrderApplicant) {
        if (StringUtils.isBlank(taskOrderApplicant.getApplicantId())) {
            return CommonResult.failed("非法用户访问");
        }
        try {
            taskOrderService.applyCashOut(taskOrderApplicant);
            return CommonResult.success("用户申请提现成功！");
        } catch (Exception e) {
            log.warn("用户申请提现失败！");
            return CommonResult.failed(e.getMessage());
        }
    }

    @ApiOperation("获取收益排行榜")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "接取者id", required = true),
            @ApiImplicitParam(name = "schoolId", value = "学校id", required = true),
            @ApiImplicitParam(name = "pageNum", value = "当前页数", required = true),
            @ApiImplicitParam(name = "pageSize", value = "页面显示条数", required = true),
            @ApiImplicitParam(name = "duration", value = "统计天数"),

    })
    @GetMapping("/getProfitRanking")
    public CommonResult getProfitRanking(String userId, int schoolId, int duration,int pageNum, int pageSize) {
        if (StringUtils.isBlank(userId)) {
            return CommonResult.failed("非法用户访问");
        }
        try {
            List<RankingVO> result = null;
            result = taskOrderService.getProfitRanking(userId, schoolId, duration,pageNum,pageSize);
            return CommonResult.success("收益排行榜获取成功！", result);

        } catch (Exception e) {
            log.warn("申请失败！：" + e.getMessage());
            return CommonResult.failed("收益排行榜申请失败，接口异常！");
        }
    }


}
