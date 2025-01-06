package com.example.bbs.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.bbs.mapper.CommentManageMapper;
import com.example.bbs.mapper.ContentMapper;
import com.example.bbs.pojo.Comment;
import com.example.bbs.pojo.Content;
import com.example.bbs.result.CommonResult;
import com.example.bbs.service.CommentManageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 功能描述：
 *
 * @Author: lyc
 * @Date: 2022/2/14 3:04 下午
 */
@Slf4j
@RestController
@RequestMapping("/commentManage")
@Api(tags = "评论管理相关操作")
public class CommentManageController {

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private HttpServletResponse httpServletResponse;

    @Autowired
    private CommentManageService commentManageService;

    @Autowired
    private CommentManageMapper commentManageMapper;

    @Autowired
    private ContentMapper contentMapper;


    @ApiOperation("编辑评论信息")
    @PostMapping("/editCommentInfo")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "commentId",value = "评论ID"),
            @ApiImplicitParam(name = "commentState",value = "评论状态"),
    })
    public CommonResult editCommentInfo(String commentId,Integer commentState){
        //根据评论ID去对评论进行编辑
        String token = httpServletRequest.getHeader("token");
        Boolean res = commentManageService.judgeTokenValid(token);
        if(res.equals(true)){
            Integer result = commentManageService.updateCommentInfo(commentId,commentState);
            if(result!=null){
                return CommonResult.success("修改评论成功");
            }else{
                return CommonResult.success("修改评论失败");
            }
        }else{
            return CommonResult.failed("抱歉，您无权修改评论信息");
        }
    }

    @ApiOperation("删除评论信息")
    @PostMapping("/deleteComment")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId",value = "用户ID"),
            @ApiImplicitParam(name = "commentId",value = "评论ID"),
            @ApiImplicitParam(name = "commentState",value = "评论状态"),
    })
    public CommonResult deleteComment(String userId,String commentId,Integer commentState){
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("commentId",commentId);
        Comment comment = commentManageMapper.selectOne(queryWrapper);
        if(userId.equals(comment.getUserId())){
            Integer result = commentManageService.updateCommentInfo(commentId,commentState);
            if(result!=null){
                QueryWrapper<Content> contentQueryWrapper = new QueryWrapper<>();
                contentQueryWrapper.eq("contentId", comment.getContentId());
                Content content = contentMapper.selectOne(contentQueryWrapper);
                content.setCommentNum(content.getCommentNum() - 1);
                contentMapper.updateById(content);
                return CommonResult.success("修改评论成功");
            }else{
                return CommonResult.success("修改评论失败");
            }
        }
            else{
            return CommonResult.failed("抱歉，您无权修改评论信息");
        }
    }

    @ApiOperation("管理员评论信息")
    @PostMapping("/manageComment")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId",value = "用户ID"),
            @ApiImplicitParam(name = "commentId",value = "评论ID"),
            @ApiImplicitParam(name = "commentState",value = "评论状态"),
    })
    public CommonResult manageComment(String userId,String commentId,Integer commentState){
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("commentId",commentId);
        Comment comment = commentManageMapper.selectOne(queryWrapper);
        if(userId.equals(comment.getUserId())){
            Integer result = commentManageService.updateCommentInfo(commentId,commentState);
            if(result!=null){
                if (commentState==4){
                    QueryWrapper<Content> contentQueryWrapper = new QueryWrapper<>();
                    contentQueryWrapper.eq("contentId", comment.getContentId());
                    Content content = contentMapper.selectOne(contentQueryWrapper);
                    content.setCommentNum(content.getCommentNum() - 1);
                    contentMapper.updateById(content);
                }
                if (commentState==0){
                    QueryWrapper<Content> contentQueryWrapper = new QueryWrapper<>();
                    contentQueryWrapper.eq("contentId", comment.getContentId());
                    Content content = contentMapper.selectOne(contentQueryWrapper);
                    content.setCommentNum(content.getCommentNum() + 1);
                    contentMapper.updateById(content);
                }
                return CommonResult.success("修改评论成功");
            }else{
                return CommonResult.success("修改评论失败");
            }
        }
        else{
            return CommonResult.failed("抱歉，您无权修改评论信息");
        }
    }

    @ApiOperation("管理员获取评论信息")
    @GetMapping("/manageGetCommentById")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "commentId",value = "评论ID"),
    })
    public CommonResult manageGetCommentById(String commentId){
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("commentId",commentId);
        Comment comment = commentManageMapper.selectOne(queryWrapper);
        if (comment==null){
            return CommonResult.failed("获取评论失败");
        }
        return CommonResult.success("获取评论成功",comment);
    }


}
