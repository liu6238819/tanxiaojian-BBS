package com.example.bbs.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.bbs.mapper.*;
import com.example.bbs.pojo.*;
import com.example.bbs.pojo.vo.*;
import com.example.bbs.result.CommonResult;
import com.example.bbs.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 功能描述：
 *
 * @Author: lyc
 * @Date: 2022/2/10 10:36 上午
 */
@Slf4j
@RestController
@RequestMapping("/commonAPIs")
@Api(tags = "通用搜索接口")
public class CommonAPIsController {

    @Autowired
    CommonSearchService commonSearchService;
    @Autowired
    UserSchoolService userSchoolService;
    @Autowired
    RemindService remindService;

    @Autowired
    ContentService contentService;

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserSchoolMapper userSchoolMapper;
    @Autowired
    private ContentMapper contentMapper;
    @Autowired
    private SchoolInfoMapper schoolInfoMapper;
    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private RemindMapper remindMapper;

    @Autowired
    private BbsConfigMapper bbsConfigMapper;


    @Autowired
    private  FuzzySearchService fuzzySearchService;

    @Autowired
    GroupMemberService groupMemberService;

    @ApiOperation("通用搜索方法")
    @GetMapping("/searchSimplified")
    //
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码数", required = true),
            @ApiImplicitParam(name = "pageSize", value = "每页数量", required = true),
            @ApiImplicitParam(name = "sortType", value = "排序类型", required = false, defaultValue = "createTime"),
    })
    public CommonResult searchSimplified(CommonSearchVO commonSearchVO, Integer pageNum, Integer pageSize, String sortType) {
        String tableName = commonSearchService.parseObject(commonSearchVO);
        return commonSearchService.getSearchList(tableName, commonSearchVO.getMode(), commonSearchVO.getCondition(), pageNum, pageSize, sortType);
    }

    @ApiOperation("权限审核")
    @PostMapping("/permissionCheck")
    //
    public CommonResult permisionCheck(PermissionCheckVO permissionCheckVO) {
        return userSchoolService.permisionCheck(
                permissionCheckVO.getUserId(),
                permissionCheckVO.getSchoolId(),
                permissionCheckVO.getPlateId());
    }

    @ApiOperation("权限审核（非BBS业务）")
    @PostMapping("/permissionCheckOutBBS")
    //
    public CommonResult permissionCheckOutBBS(PermissionCheckVO permissionCheckVO) {
        return userSchoolService.permissionCheckOutBBS(
                permissionCheckVO.getUserId(),
                permissionCheckVO.getSchoolId(),
                permissionCheckVO.getSchoolFlag());
    }

    @ApiOperation("通知公告")
    @GetMapping("/getReminds")
    //
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "userId", required = true),
            @ApiImplicitParam(name = "schoolId", value = "schoolId", required = true),
            @ApiImplicitParam(name = "pageNum", value = "页码数", required = true),
            @ApiImplicitParam(name = "pageSize", value = "每页数量", required = true),

    })
    public CommonResult getReminds(String userId, @RequestParam(defaultValue = "all") String queryType,Integer schoolId, Integer pageNum, Integer pageSize) {
        if (StringUtils.isBlank(userId)) {
            return CommonResult.failed("非法用户访问");
        }
        CommonResult<RemindWithCountVO> commonResult = CommonResult.success();
        RemindWithCountVO remindWithCountVO = new RemindWithCountVO();
        if (queryType.equals("all")) {//all时获取所有类型的通知数
            //查询数据库
            remindWithCountVO.setRemindCount(remindService.selectNums(userId, queryType));
            remindWithCountVO.setAdmireCount(remindService.selectNums(userId, "admire"));
            remindWithCountVO.setFollowCount(remindService.selectNums(userId, "follow"));
            remindWithCountVO.setReplyCount(remindService.selectNums(userId, "reply"));
        } else {
            remindWithCountVO.setRemindCount(remindService.selectNums(userId, queryType));
            remindWithCountVO.setData(remindService.getRemindsPage(userId, queryType, schoolId,pageNum, pageSize));
        }
        commonResult.setData(remindWithCountVO);
        return commonResult;

    }

    @ApiOperation("阅读通知公告")
    @GetMapping("/readRemind")
    //
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "userId", required = true),
            @ApiImplicitParam(name = "remindId", value = "提醒id", required = true),


    })
    public CommonResult readRemind(String userId, String remindId) {
        if (remindService.readRemind(userId, remindId)) {
            CommonResult commonResult = CommonResult.success();
            return commonResult;
        } else {
            CommonResult commonResult = CommonResult.failed();
            commonResult.setMessage("更新通知公告失败");
            return commonResult;
        }


    }

    @ApiOperation("清空公告")
    @GetMapping("/clearRemind")
    //
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "userId", required = true),
            @ApiImplicitParam(name = "type", value = "公告类型", required = true),


    })
    public CommonResult clearRemind(String userId, Integer type) {
        if (remindService.clearRemind(userId, type)) {
            CommonResult commonResult = CommonResult.success();
            return commonResult;
        } else {
            CommonResult commonResult = CommonResult.failed();
            commonResult.setMessage("清空公告失败");
            return commonResult;
        }


    }

    @RequestMapping(value = "/createRobots", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation("robot生成")
    @ApiImplicitParam(name = "data", required = true, dataType = "UserRobot")
    public CommonResult createRobots(@RequestBody List<UserRobot> data) {
        for (int i = 0; i < data.size(); i++) {
            UserRobot robot = data.get(i);
            if (null == robot.getId()) return CommonResult.failed("缺少ID");
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("openId", robot.getId());
            User record = userMapper.selectOne(queryWrapper);
            if (null == record) {//新的机器人，插入用户表以及关系表
                User user = new User();
                user.setOpenId(robot.getId());
                user.setNickName(robot.getNickName());
                user.setHeadimgUrl(robot.getHeadimgUrl());
                user.setUserState(0);
                user.setUserType(0);
                user.setPhone(robot.getId());
                userMapper.insert(user);
                UserSchool userSchool = new UserSchool();
                userSchool.setUserId(user.getUserId());
                userSchool.setSchoolId(robot.getSchoolId());
                userSchool.setUserState(2);
                userSchoolMapper.insert(userSchool);
            } else {//已存在该机器人，只判断是否需要赋予新校区权限
                QueryWrapper<UserSchool> userSchoolQueryWrapper = new QueryWrapper<>();
                userSchoolQueryWrapper.eq("userId", record.getUserId());
                userSchoolQueryWrapper.eq("schoolId", robot.getSchoolId());
                UserSchool record1 = userSchoolMapper.selectOne(userSchoolQueryWrapper);
                if (null != record1) {
                    UserSchool userSchool = new UserSchool();
                    userSchool.setUserId(record.getUserId());
                    userSchool.setSchoolId(robot.getSchoolId());
                    userSchool.setUserState(2);
                    userSchoolMapper.insert(userSchool);
                }


            }

        }
        return CommonResult.success("注册成功");
    }

    @RequestMapping(value = "/createContents", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation("帖子生成")
    @ApiImplicitParam(name = "data", required = true, dataType = "Content")
    public CommonResult createContents(@RequestBody List<Content> data) {
        try {
            for (int i = 0; i < data.size(); i++) {
                Content copy = data.get(i);
                Content result = new Content();
                result.setContentId(copy.getContentId());
                result.setTitle(copy.getTitle());
                result.setContentText(copy.getContentText());
                result.setPlateId(copy.getPlateId());
                result.setSchoolId(copy.getSchoolId());
                result.setContentUrl(copy.getContentUrl());
                result.setVisibleRange(copy.getVisibleRange());
                //处理时间
                String time = copy.getVoteId();
                if (StringUtils.isNotBlank(time)) {//判断不为空和null
                    time = time.replace("-", "/");
                    time = time.replace("T", " ");
                    Date date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(time);
                    result.setCreateTime(date);
                }
                //最后处理userID
                QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("openId", copy.getUserId());
                User record = userMapper.selectOne(queryWrapper);
                if (null == record) {
                    return CommonResult.failed("没有该用户！");
                } else {
                    result.setUserId(record.getUserId());
                }
                contentMapper.insert(result);

            }

            log.info("自动发帖成功！");
            return CommonResult.success("发帖成功！");
        } catch (Exception e) {
            log.warn("自动发帖失败！");
            return CommonResult.failed(e.getMessage());
        }
    }

    @RequestMapping(value = "/createComments", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation("评论生成")
    @ApiImplicitParam(name = "data", required = true, dataType = "Comment")
    public CommonResult createComments(@RequestBody List<Comment> data) {
        try {
            for (int i = 0; i < data.size(); i++) {
                Comment copy = data.get(i);
                Comment result = new Comment();
                result.setCommentId(copy.getCommentId());
                result.setCommentText(copy.getCommentText());
                result.setIsMain(copy.getIsMain());
                result.setReplyCommentId(copy.getReplyCommentId());
                //处理content
                QueryWrapper<Content> contentQueryWrapper = new QueryWrapper<>();
                contentQueryWrapper.eq("contentId", copy.getContentId());
                Content content = contentMapper.selectOne(contentQueryWrapper);
                if (null != content) {
                    result.setContentId(copy.getContentId());
                    content.setCommentNum(content.getCommentNum() + 1);
                    contentMapper.updateById(content);
                } else {
                    return CommonResult.failed("没有该主贴！");
                }
                //处理时间
                String time = copy.getCommentUrl();
                time = time.replace("-", "/");
                time = time.replace("T", " ");
                Date date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(time);
                if (StringUtils.isNotBlank(time)) {//判断不为空和null
                    result.setCreateTime(date);
                }
                //最后处理userID
                QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("openId", copy.getUserId());
                User record = userMapper.selectOne(queryWrapper);
                if (null == record) {
                    return CommonResult.failed("没有该用户！");
                } else {
                    result.setUserId(record.getUserId());
                }
                commentMapper.insert(result);
                Remind remind = new Remind();
                remind.setState(0);
                Boolean bool = StringUtils.isEmpty(result.getReplyCommentId());
                //true，comment不为空
                if (bool.equals(false)) {
                    remind.setType(5);
                    QueryWrapper<Comment> commentQueryWrapper = new QueryWrapper<>();
                    commentQueryWrapper.eq("commentId", result.getReplyCommentId());
                    Comment toComment = commentMapper.selectOne(commentQueryWrapper);
                    remind.setUserId(toComment.getUserId());
                    remind.setRelateId(result.getReplyCommentId());
                } else {
                    remind.setType(4);
                    remind.setUserId(content.getUserId());
                    remind.setRelateId(content.getContentId());
                }
                remind.setFromUserId(result.getUserId());
                if (!remind.getUserId().equals(remind.getFromUserId())) {
                    remind.setCreateTime(date);
                    remindMapper.insert(remind);
                }

            }


            log.info("自动发帖成功！");
            return CommonResult.success("发帖成功！");
        } catch (Exception e) {
            log.warn("自动发帖失败！");
            return CommonResult.failed(e.getMessage());
        }
    }

    @RequestMapping(value = "/createSchoolInfos", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation("校园信息生成")
    @ApiImplicitParam(name = "content", required = true, dataType = "Content")
    public CommonResult createSchoolInfos(@RequestBody Content content) {
        try {
            Content result = new Content();
            result.setTitle(content.getTitle());
            result.setContentText(content.getContentText());
            result.setPlateId(content.getPlateId());
            result.setSchoolId(content.getSchoolId());
            result.setVisibleRange(content.getVisibleRange());
            result.setUserId(content.getUserId());
            result.setContentType(content.getContentType());
            result.setIsSpecial(2);
            contentMapper.insert(result);
            //处理校园信息
            for (int i = 0; i < content.getSchoolInfos().size(); i++) {
                SchoolInfo info = new SchoolInfo();
                info.setContentId(result.getContentId());
                info.setIntro(content.getSchoolInfos().get(i).getIntro());
                info.setImgUrls(content.getSchoolInfos().get(i).getImgUrls());
                info.setLinkman(content.getSchoolInfos().get(i).getLinkman());
                schoolInfoMapper.insert(info);
            }


            log.info("发布校园信息成功！");
            return CommonResult.success("发布校园信息成功！");

        } catch (Exception e) {
            log.warn("发布校园信息失败！");
            return CommonResult.failed(e.getMessage());
        }
    }

    @ApiOperation("进行线下面对面认证")
    @GetMapping("/offlineIdentification")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", required = true),
            @ApiImplicitParam(name = "schoolId", value = "学校id", required = true),
            @ApiImplicitParam(name = "code", value = "验证码", required = true),


    })
    public CommonResult offlineIdentification(String userId, int schoolId, String code) {
        if (StringUtils.isBlank(userId)) {
            return CommonResult.failed("非法用户访问");
        }
        try {
            int result = userSchoolService.offlineIdentification(userId, schoolId, code);
            if (result == 2) return CommonResult.failed("认证失败，用户不存在或未完成授权！");
            if (result == 3) return CommonResult.failed("认证失败，验证码错误或已失效！");
            return CommonResult.success("线下认证成功！");

        } catch (Exception e) {
            log.warn("申请失败！：" + e.getMessage());
            return CommonResult.failed("认证失败，接口异常！");
        }


    }

    @ApiOperation("获取某项论坛配置")
    @GetMapping("/getOneBbsConfig")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "schoolId", value = "学校id", required = true),
            @ApiImplicitParam(name = "configType", value = "配置类型", required = true),


    })
    public CommonResult getOneBbsConfig(int schoolId,String configType) {
        QueryWrapper<BbsConfig> batchConfigQueryWrapper = new QueryWrapper<>();
        batchConfigQueryWrapper
                .eq("school_id", schoolId)
                .eq("config_type", configType);
        try {
           BbsConfig result = bbsConfigMapper.selectOne(batchConfigQueryWrapper);
            return CommonResult.success("获取成功！",result);

        } catch (Exception e) {
            log.warn("获取失败！：" + e.getMessage());
            return CommonResult.failed("获取失败，接口异常！");
        }


    }

    @ApiOperation("获取【先审后发】配置状态")
    @GetMapping("/getConfigOfCheckBeforePublish")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "schoolId", value = "学校id", required = true),
    })
    public CommonResult getConfigOfCheckBeforePublish(int schoolId) {
        //获取【先审后发】配置状态
        QueryWrapper<BbsConfig> bbsConfigQueryWrapper = new QueryWrapper<>();
        bbsConfigQueryWrapper
                .eq("school_id", schoolId)
                .eq("config_type", "functionConfig");
        BbsConfig bbsConfig = bbsConfigMapper.selectOne(bbsConfigQueryWrapper);
        //json处理
        Object configJson = bbsConfig.getConfigJson();
        JSONObject configJsonObject = JSONObject.parseObject(configJson.toString());
        JSONArray configDataList = configJsonObject.getJSONArray("data");
        int checkBeforePublish =-1;
        //找到配置项中的匿名发帖状态
        for (Object configData: configDataList) {
            JSONObject configDataJO = JSONObject.parseObject(configData.toString());
            if (configDataJO.get("function_name").equals("chatGPT")){
                checkBeforePublish =configDataJO.getInteger("check_before_publish");
            }
        }
        try {
            return CommonResult.success("先审后发配置状态获取成功！",checkBeforePublish);

        } catch (Exception e) {
            log.warn("获取失败！：" + e.getMessage());
            return CommonResult.failed("获取失败，接口异常！");
        }


    }


    @ApiOperation("弹窗弹出判断")
    @GetMapping("/needShowPopup")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "schoolId", value = "学校ID", required = true),
            @ApiImplicitParam(name = "unionId", value = "用户unionId", required = true),
    })
    public CommonResult needShowPopup(int schoolId,String unionId) {
        try {
            Boolean result = groupMemberService.needShowPopup("notice",schoolId,unionId);
            return CommonResult.success("弹窗展示结果！",result);

        } catch (Exception e) {
            log.warn("获取失败！：" + e.getMessage());
            return CommonResult.failed("获取失败，接口异常！");
        }

    }

    @ApiOperation("新增不再提醒记录")
    @GetMapping("/addNoRemindRecord")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "schoolId", value = "学校ID", required = true),
            @ApiImplicitParam(name = "userId", value = "用户ID", required = true),
            @ApiImplicitParam(name = "nickName", value = "用户昵称", required = true),
            @ApiImplicitParam(name = "unionId", value = "用户unionId", required = true),
    })
    public CommonResult addNoRemindRecord(String userId, String nickName, int schoolId, String unionId) {
        try {
            groupMemberService.addNoRemindRecord(userId,nickName,schoolId,unionId);
            return CommonResult.success("操作成功！");

        } catch (Exception e) {
            log.warn("操作失败！：" + e.getMessage());
            return CommonResult.failed("操作失败，接口异常！");
        }

    }


    @ApiOperation("模糊搜索帖子")
    @GetMapping("/NLPContentText")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "text", value = "搜索内容", required = true),
            @ApiImplicitParam(name = "schoolId", value = "学校id", required = true),

    })
    public CommonResult getSimilarContent(String text ,int schoolId) {
        try {
            List<HomeContentVO> result= fuzzySearchService.getSimilarContent(text,schoolId);
            return CommonResult.success("获取成功！",result);

        } catch (Exception e) {
            log.warn("更新失败！：" + e.getMessage());
            return CommonResult.failed("更新失败，接口异常！");
        }


    }

    @ApiOperation("仅按照,获取某项论坛配置")
    @GetMapping("/getOneBbsConfigWithoutSchoolId")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "configType", value = "配置类型", required = true),


    })
    public CommonResult getOneBbsConfigWithoutSchoolId(String configType) {
        QueryWrapper<BbsConfig> batchConfigQueryWrapper = new QueryWrapper<>();
        batchConfigQueryWrapper
                .eq("config_type", configType);
        try {
           BbsConfig result = bbsConfigMapper.selectOne(batchConfigQueryWrapper);
            return CommonResult.success("获取成功！",result);

        } catch (Exception e) {
            log.warn("获取失败！：" + e.getMessage());
            return CommonResult.failed("获取失败，接口异常！");
        }


    }

    @ApiOperation("区分学校的通知公告")
    @GetMapping("/getRemindsDistinguishSchoolId")
    //
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "userId", required = true),
            @ApiImplicitParam(name = "pageNum", value = "页码数", required = true),
            @ApiImplicitParam(name = "pageSize", value = "每页数量", required = true),

    })
    public CommonResult getRemindsDistinguishSchoolId(String userId, @RequestParam(defaultValue = "all") String queryType, Integer pageNum, Integer pageSize) {
        if (StringUtils.isBlank(userId)) {
            return CommonResult.failed("非法用户访问");
        }
        CommonResult<RemindWithCountVO> commonResult = CommonResult.success();
        RemindWithCountVO remindWithCountVO = new RemindWithCountVO();
        remindWithCountVO.setRemindCount(remindService.selectNums(userId, queryType));
        if (queryType.equals("all")) {//all时获取所有类型的通知数
            remindWithCountVO.setAdmireCount(remindService.selectNums(userId, "admire"));
            remindWithCountVO.setFollowCount(remindService.selectNums(userId, "follow"));
            remindWithCountVO.setReplyCount(remindService.selectNums(userId, "reply"));
        } else {
            remindWithCountVO.setData(remindService.getRemindsPageWithoutschoolId0102(userId, queryType, pageNum, pageSize));
        }
        commonResult.setData(remindWithCountVO);
        return commonResult;

    }







}
