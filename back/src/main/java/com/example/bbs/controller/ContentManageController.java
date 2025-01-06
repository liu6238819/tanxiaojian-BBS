package com.example.bbs.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.bbs.mapper.ContentManageMapper;
import com.example.bbs.pojo.Content;
import com.example.bbs.result.CommonResult;
import com.example.bbs.service.CommentManageService;
import com.example.bbs.service.ContentManageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 功能描述：
 *
 * @Author: lyc
 * @Date: 2022/2/14 3:32 下午
 */
@Slf4j
@RestController
@RequestMapping("/contentManage")
@Api(tags = "帖子管理相关操作")
public class ContentManageController {

    @Autowired
    private ContentManageService contentManageService;

    @Autowired
    private ContentManageMapper contentManageMapper;

    @ApiOperation("编辑帖子信息")
    @PostMapping("/editContentInfo")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId",value = "用户ID"),
            @ApiImplicitParam(name = "contentId",value = "帖子ID"),
            @ApiImplicitParam(name = "contentState",value = "帖子状态")
    })
    public CommonResult editContentInfo(String userId,String contentId, Integer contentState){
        QueryWrapper<Content> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("contentId",contentId);
        Content content = contentManageMapper.selectOne(queryWrapper);

        if(userId.equals(content.getUserId())){
            Integer result = contentManageService.updateContentInfo(contentId,contentState);
            if(result!=null){
                return CommonResult.success("修改帖子成功");
            }else{
                return CommonResult.success("修改帖子失败");
            }
        }else{
            return CommonResult.failed("抱歉，您无权修改帖子信息");
        }
    }

    @ApiOperation("管理员修改帖子信息")
    @PostMapping("/manageContentInfo")
    public CommonResult manageContentInfo(@RequestBody Content content){
        Integer result = contentManageService.manageContent(content);
        if(result ==1){
            return CommonResult.success("修改帖子成功");
        }else{
            return CommonResult.success("修改帖子失败");
        }

    }
}
