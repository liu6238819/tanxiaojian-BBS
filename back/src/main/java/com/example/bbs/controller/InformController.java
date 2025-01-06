package com.example.bbs.controller;

import com.example.bbs.pojo.Inform;
import com.example.bbs.pojo.vo.HomeContentVO;
import com.example.bbs.result.CommonResult;
import com.example.bbs.service.CommentService;
import com.example.bbs.service.ContentService;
import com.example.bbs.service.InformService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/inform")
@Api(tags = "举报相关操作")
public class InformController {
    @Autowired
    InformService informService;
    @Autowired
    ContentService contentService;
    @Autowired
    CommentService commentService;

    @ApiOperation("举报帖子")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "举报人id"),
            @ApiImplicitParam(name = "targetId", value = "目标id"),
            @ApiImplicitParam(name = "isContent", value = "0：评论；1：帖子"),
            @ApiImplicitParam(name = "informInfo", value = "举报信息")
    })
    @PostMapping("/informContent")
    public CommonResult informContent(String userId, String targetId, int isContent,String informInfo) {
        try {
            if (isContent == 0) {
                //评论举报数量+1
                commentService.informComment(targetId);
            } else if (isContent == 1) {
                //帖子举报数量+1
                contentService.informContent(targetId);
            }
            //添加举报信息
            informService.addInformInfo(userId, targetId, isContent, informInfo);
            log.info("举报成功，举报信息：userId:" + userId + ",targetId:" + targetId + ",isContent:" + isContent + "");
            return CommonResult.success("举报成功！");
        } catch (Exception e) {
            log.info(e.getMessage());
            return CommonResult.failed(e.getMessage());
        }
    }

    @ApiOperation("举报帖子（新）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "举报人id"),
            @ApiImplicitParam(name = "informedUserId", value = "被举报人id"),
            @ApiImplicitParam(name = "contentId", value = "被举报人帖子id"),
            @ApiImplicitParam(name = "targetId", value = "目标id"),
            @ApiImplicitParam(name = "targetText", value = "目标内容节选"),
            @ApiImplicitParam(name = "isContent", value = "0：评论；1：帖子"),
            @ApiImplicitParam(name = "informInfo", value = "举报信息"),
            @ApiImplicitParam(name = "informUrl", value = "举报图片"),
            @ApiImplicitParam(name = "schoolId", value = "学校ID")

    })
    @PostMapping("/informContentMoreInfo")
    public CommonResult informContentMoreInfo(String userId,String informedUserId,String contentId,String targetText, String targetId, int isContent,String informInfo, String informUrl, int schoolId) {
        try {
            if (isContent == 0) {
                //评论举报数量+1
                commentService.informCommentWithSchoolId(targetId,schoolId);
            } else if (isContent == 1) {
                //帖子举报数量+1
                contentService.informContentWithSchoolId(targetId,schoolId);
            }
            //添加举报信息
            informService.addInformInfoMoreInfo(userId,informedUserId,contentId,targetText, targetId, isContent,informInfo, informUrl,schoolId);
            log.info("举报成功，举报信息：userId:" + userId + ",targetId:" + targetId + ",isContent:" + isContent + "");
            return CommonResult.success("举报成功！");
        } catch (Exception e) {
            log.info(e.getMessage());
            return CommonResult.failed(e.getMessage());
        }
    }

    @ApiOperation("举报帖子（新）")
    @PostMapping("/informContentMoreInfo0522")
    public CommonResult informContentMoreInfo(@RequestBody Inform informContentDTO) {
        try {
            String userId = informContentDTO.getUserId();
            String informedUserId = informContentDTO.getInformedUserId();
            String contentId = informContentDTO.getContentId();
            String targetId = informContentDTO.getTargetId();
            String targetText = informContentDTO.getTargetText();
            Integer isContent = informContentDTO.getIsContent();
            String informInfo = informContentDTO.getInformInfo();
            String informUrl = informContentDTO.getInformUrl();
            Integer schoolId = informContentDTO.getSchoolId();
            if (isContent == 0) {
                //评论举报数量+1
                commentService.informCommentWithSchoolId(targetId,schoolId);
            } else if (isContent == 1) {
                //帖子举报数量+1
                contentService.informContentWithSchoolId(targetId,schoolId);
            }
            //添加举报信息
            informService.addInformInfoMoreInfo(userId,informedUserId,contentId,targetText, targetId, isContent,informInfo, informUrl,schoolId);
            log.info("举报成功，举报信息：userId:" + userId + ",targetId:" + targetId + ",isContent:" + isContent + "");
            return CommonResult.success("举报成功！");
        } catch (Exception e) {
            log.info(e.getMessage());
            return CommonResult.failed(e.getMessage());
        }
    }



    @ApiOperation("判断是否已经举报过该目标")
    @GetMapping("/ifAlreadyInformThisTarget")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", required = true),
            @ApiImplicitParam(name = "schoolId", value = "学校id", required = true),
            @ApiImplicitParam(name = "targetId", value = "目标id", required = true),

    })
    public CommonResult ifAlreadyInformThisTarget(String userId ,int schoolId, String targetId) {
        try {
            int result =informService.ifAlreadyInformThisTarget(userId,schoolId,targetId);
            if (result==1){
                return CommonResult.success("用户已举报过该目标！",result);
            }
            else{
                return CommonResult.success("用户没有举报过该目标！",result);
            }

        } catch (Exception e) {
            log.warn("获取举报信息失败！：" + e.getMessage());
            return CommonResult.failed("获取举报信息失败，接口异常！");
        }


    }
}
