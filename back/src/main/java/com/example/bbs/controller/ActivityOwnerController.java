package com.example.bbs.controller;

import com.example.bbs.pojo.ActivityOwner;
import com.example.bbs.pojo.vo.SignUpActivityVO;
import com.example.bbs.result.CommonResult;
import com.example.bbs.service.ActivityOwnerService;
import com.example.bbs.service.SignUpService;
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
@RequestMapping("/activityOwner")
@Api(tags = "活动拥有者相关操作")
public class ActivityOwnerController {
    @Autowired
    ActivityOwnerService activityOwnerService;

    @ApiOperation("创建活动拥有者")
    @PostMapping("/createActivityOwner")
    public CommonResult createActivityOwner(@RequestBody ActivityOwner activityOwner) {
        try {
            int result = activityOwnerService.createActivityOwner(activityOwner);
            if (result < 0) return CommonResult.failed("创建失败");
            return CommonResult.success("创建成功！");
        } catch (Exception e) {
            log.warn("创建失败！活动信息：" + e);
            return CommonResult.failed("创建失败，接口异常！");
        }
    }

    @ApiOperation("获取活动拥有者列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "当前浏览用户id", required = true),
            @ApiImplicitParam(name = "ownerId", value = "拥有者id"),
            @ApiImplicitParam(name = "kind", value = "类型"),
            @ApiImplicitParam(name = "state", value = "状态"),
            @ApiImplicitParam(name = "schoolId", value = "所属学校"),
            @ApiImplicitParam(name = "pageNum", value = "当前页数", required = true),
            @ApiImplicitParam(name = "pageSize", value = "页面显示条数", required = true),

    })
    @GetMapping("/getActivityOwnerList")
    public CommonResult getActivityOwnerList(String userId,String ownerId, @RequestParam(defaultValue = "-1") int kind, @RequestParam(defaultValue = "-1") int state, @RequestParam(defaultValue = "-1") int schoolId, int pageNum, int pageSize) {
        if (StringUtils.isBlank(userId)) {
            return CommonResult.failed("非法用户访问");
        }
        try {
            List<ActivityOwner> result = null;
            result = activityOwnerService.getActivityOwnerList(ownerId,kind, state, schoolId, pageNum, pageSize);
            return CommonResult.success("列表获取成功！", result);
        } catch (Exception e) {
            log.warn("申请失败！：" + e.getMessage());
            return CommonResult.failed("列表获取失败，接口异常！");
        }
    }

}
