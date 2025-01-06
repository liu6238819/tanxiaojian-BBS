package com.example.bbs.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.bbs.auto.PermissionCheck;
import com.example.bbs.mapper.CommentMapper;
import com.example.bbs.mapper.ContentMapper;
import com.example.bbs.mapper.RemindMapper;
import com.example.bbs.mapper.UserMapper;
import com.example.bbs.pojo.Comment;
import com.example.bbs.pojo.Content;
import com.example.bbs.pojo.Remind;
import com.example.bbs.pojo.User;
import com.example.bbs.pojo.vo.ChildrenCommentVO;
import com.example.bbs.pojo.vo.HomeCommentVO;
import com.example.bbs.result.CommonResult;
import com.example.bbs.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.jdbc.Null;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/comment")
@Api(tags = "评论相关操作")
public class CommentController {

    @Autowired
    CommentService commentService;
    @Autowired
    PermissionCheck permissionCheck;

    @Autowired
    RemindService remindService;

    @Autowired
    RemindMapper remindMapper;

    @Autowired
    ContentMapper contentMapper;

    @Autowired
    CommentMapper commentMapper;


    @Autowired
    UserMapper userMapper;

    @Autowired
    MarkService markService;


    @ApiOperation("添加评论")
    @PostMapping("/publishCommentApi")
    public CommonResult publishComment( Comment comment) {
        try {
            StringBuilder message = new StringBuilder();

            //添加一条新评论
            int publishResult = commentService.publishComment(comment);
            if (publishResult == -1) return CommonResult.failed("评论失败，权限异常！");
            Remind remind = new Remind();
            remind.setState(0);
            Boolean bool = StringUtils.isEmpty(comment.getReplyCommentId());
            String remindUserId = "";
            //true，该条为主评论
            if (bool.equals(false)) {
                remind.setType(5);
                QueryWrapper<Comment> commentQueryWrapper = new QueryWrapper<>();
                commentQueryWrapper.eq("commentId", comment.getReplyCommentId());
                Comment toComment = commentMapper.selectOne(commentQueryWrapper);
                remind.setUserId(toComment.getUserId());
                remind.setRelateId(comment.getReplyCommentId());
                //同步redis中userId
                remindUserId =toComment.getUserId();
                //给实际回复对象也发一条通知
                if (!StringUtils.isBlank(comment.getReplyUserId()) && !comment.getReplyUserId().equals(toComment.getUserId())) {
                    Remind remind2 = new Remind();
                    remind2.setState(0);
                    remind2.setType(5);
                    remind2.setUserId(comment.getReplyUserId());
                    remind2.setRelateId(comment.getReplyCommentId());
                    remind2.setFromUserId(comment.getUserId());
                    if (!remind2.getUserId().equals(remind.getFromUserId())) {
                        remindMapper.insert(remind2);
                    }
                }

            } else {
                remind.setType(4);
                QueryWrapper<Content> contentQueryWrapper = new QueryWrapper<>();
                contentQueryWrapper.eq("contentId", comment.getContentId());
                Content content = contentMapper.selectOne(contentQueryWrapper);
                remind.setUserId(content.getUserId());
                remind.setRelateId(content.getContentId());
                remindUserId =content.getUserId();
            }
            remind.setFromUserId(comment.getUserId());
            if (!remind.getUserId().equals(remind.getFromUserId())) {
                remindMapper.insert(remind);
            }
            markService.addMarkMessage(comment.getUserId(),comment);
            log.info("评论成功！评论信息：" + comment);
//            return CommonResult.success("评论成功！");
            return CommonResult.success("评论成功！",comment.getCommentId());
        } catch (Exception e) {
            log.info("评论失败！评论信息：" + comment);
            log.info(String.valueOf(e));
            return CommonResult.failed(e.getMessage());
        }

    }

    @ApiOperation("添加评论")
    @PostMapping("/publishCommentApiUseBody")
    public CommonResult publishCommentApiUseBody(@RequestBody Comment comment) {
        try {
            StringBuilder message = new StringBuilder();
            //添加一条新评论
            int publishResult = commentService.publishComment(comment);
            if (publishResult == -1) return CommonResult.failed("评论失败，权限异常！");
            //需要往remind表中新增记录{type:1,state:0,userid:replyid,fromUserId:userid，relatedid:commentId}
            Remind remind = new Remind();
            remind.setState(0);
            Boolean bool = StringUtils.isEmpty(comment.getReplyCommentId());
            String remindUserId = "";
            //true，该条为主评论
            if (bool.equals(false)) {
                remind.setType(5);
                QueryWrapper<Comment> commentQueryWrapper = new QueryWrapper<>();
                commentQueryWrapper.eq("commentId", comment.getReplyCommentId());
                Comment toComment = commentMapper.selectOne(commentQueryWrapper);
                remind.setUserId(toComment.getUserId());
                remind.setRelateId(comment.getReplyCommentId());
                //同步redis中userId
                remindUserId =toComment.getUserId();
                //给实际回复对象也发一条通知
                if (!StringUtils.isBlank(comment.getReplyUserId()) && !comment.getReplyUserId().equals(toComment.getUserId())) {
                    Remind remind2 = new Remind();
                    remind2.setState(0);
                    remind2.setType(5);
                    remind2.setUserId(comment.getReplyUserId());
                    remind2.setRelateId(comment.getReplyCommentId());
                    remind2.setFromUserId(comment.getUserId());
                    if (!remind2.getUserId().equals(remind.getFromUserId())) {
                        remindMapper.insert(remind2);
                    }
                }

            } else {
                remind.setType(4);
//              去content，根据contentId去查对应的记录的userId，赋值给remind的userID
                QueryWrapper<Content> contentQueryWrapper = new QueryWrapper<>();
                contentQueryWrapper.eq("contentId", comment.getContentId());
                Content content = contentMapper.selectOne(contentQueryWrapper);
                remind.setUserId(content.getUserId());
                remind.setRelateId(content.getContentId());
                remindUserId =content.getUserId();
            }
            remind.setFromUserId(comment.getUserId());
            if (!remind.getUserId().equals(remind.getFromUserId())) {
                remindMapper.insert(remind);
            }
            markService.addMarkMessage(comment.getUserId(),comment);
            log.info("评论成功！评论信息：" + comment);
//            return CommonResult.success("评论成功！");
            return CommonResult.success("评论成功！",comment.getCommentId());
        } catch (Exception e) {
            log.info("评论失败！评论信息：" + comment);
            log.info(String.valueOf(e));
            return CommonResult.failed(e.getMessage());
        }

    }

    @ApiOperation("获取帖子详情页的主评论")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "当前浏览用户id"),
            @ApiImplicitParam(name = "contentId", value = "当前帖子id"),
            @ApiImplicitParam(name = "pageNum", value = "当前页数"),
            @ApiImplicitParam(name = "pageSize", value = "页面显示条数"),
            @ApiImplicitParam(name = "requestType", value = "请求方式")

    })
    @GetMapping("/getHomeCommentsApi")
    public CommonResult getHomeComments(String userId, String contentId, int pageNum, int pageSize,String requestType) {
        List<HomeCommentVO> commentVO = commentService.getHomeCommentsWithType(userId, contentId, pageNum, pageSize,requestType);
        if (commentVO != null) {
            //log.info("评论获取成功！帖子id：" + contentId);
            return CommonResult.success("评论获取成功！", commentVO);
        } else {
            //log.info("当前帖子没有评论！帖子id：" + contentId);
            return CommonResult.failed("当前帖子没有评论！");
        }
    }

    @ApiOperation("获取帖子详情页的带高亮主评论")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "当前浏览用户id"),
            @ApiImplicitParam(name = "contentId", value = "当前帖子id"),
            @ApiImplicitParam(name = "commentId", value = "高亮评论id"),
            @ApiImplicitParam(name = "pageNum", value = "当前页数"),
            @ApiImplicitParam(name = "pageSize", value = "页面显示条数"),
            @ApiImplicitParam(name = "requestType", value = "请求方式")

    })
    @GetMapping("/getHomeCommentsWithHighLight")
    public CommonResult getHomeCommentsWithHighLight(String userId, String contentId,String commentId,int pageNum, int pageSize,String requestType) {
        List<HomeCommentVO> commentVO = commentService.getHomeCommentsPageWithHighLight(userId, contentId,pageNum, pageSize,requestType,commentId);
        if (commentVO != null) {
            //log.info("评论获取成功！帖子id：" + contentId);
            return CommonResult.success("评论获取成功！", commentVO);
        } else {
            //log.info("当前帖子没有评论！帖子id：" + contentId);
            return CommonResult.failed("当前帖子没有评论！");
        }
    }

    @ApiOperation("分页获取某条主评论的子评论")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "当前浏览用户id"),
            @ApiImplicitParam(name = "contentId", value = "当前帖子id"),
            @ApiImplicitParam(name = "commentId", value = "当前主评论id"),
            @ApiImplicitParam(name = "pageNum", value = "当前页数"),
            @ApiImplicitParam(name = "pageSize", value = "页面显示条数"),
            @ApiImplicitParam(name = "queryTime", value = "基准时间")

    })
    @GetMapping("/getChildrenCommentPage")
    public CommonResult getChildrenCommentPage(String userId, String contentId,String commentId, int pageNum, int pageSize,String queryTime) {
        List<ChildrenCommentVO> commentVO = commentService.getChildrenCommentsVOPage(userId, contentId,commentId, pageNum, pageSize,queryTime);
        if (commentVO != null) {
            //log.info("评论获取成功！帖子id：" + contentId);
            return CommonResult.success("评论获取成功！", commentVO);
        } else {
            //log.info("当前帖子没有评论！帖子id：" + contentId);
            return CommonResult.failed("当前帖子没有评论！");
        }
    }
}
