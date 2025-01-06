package com.example.bbs.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.bbs.auto.PermissionCheck;
import com.example.bbs.pojo.Admire;
import com.example.bbs.result.CommonResult;
import com.example.bbs.service.AdmireService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/admire")
@Api(tags = "点赞相关操作")
public class AdmireController {
    @Autowired
    AdmireService admireService;
    @Autowired
    PermissionCheck permissionCheck;


    @ApiOperation("对点赞点踩操作进行编辑")
    @PostMapping("/editLikeNumApi")
    public CommonResult editLikeNum(Admire admire) {
        StringBuilder message = new StringBuilder();
        try {
            //点赞类型，1：新增点赞；2：重复点赞
            int admireType = admireService.editLikeNum(admire.getUserId(), admire.getTargetId(), admire.getIsContent(), admire.getIsLike(), message);
            log.info("点赞/点踩操作成功！用户id：" + admire.getUserId());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("targetId", admire.getTargetId());
            if (admire.getIsLike() == 1) {
                jsonObject.put("isUp", 1);
                jsonObject.put("result", "点赞操作成功！");
                jsonObject.put("admireType", admireType);
            } else {
                jsonObject.put("isUp", 0);
                jsonObject.put("result", "点踩操作成功！");
            }
            return CommonResult.success(jsonObject);

        } catch (Exception e) {
            log.info("点赞/点踩操作失败！" + e.getMessage());
            return CommonResult.failed("点赞操作失败！");
        }
    }
}
