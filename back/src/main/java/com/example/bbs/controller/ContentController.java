package com.example.bbs.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.bbs.auto.PermissionCheck;
import com.example.bbs.mapper.UserMapper;
import com.example.bbs.pojo.Content;
import com.example.bbs.pojo.VoteResult;
import com.example.bbs.pojo.vo.HomeContentVO;
import com.example.bbs.pojo.vo.SchoolVO;
import com.example.bbs.result.CommonResult;
import com.example.bbs.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.bbs.util.DateUtil.getChineseName;


@Slf4j
@RestController
@RequestMapping("/content")
@Api(tags = "帖子相关操作")
@EnableCaching
public class ContentController {

    @Autowired
    SchoolService schoolService;
    @Autowired
    VoteService voteService;
    @Autowired
    ContentService contentService;
    @Autowired
    PermissionCheck permissionCheck;
    @Autowired
    UserContentService userContentService;

    @Autowired
    UserMapper userMapper;


    @Autowired
    ShortUrlService shortUrlService;


    @ApiOperation("增加帖子信息")
    @PostMapping("/publishContentApi")
    public CommonResult publishContent(@RequestBody Content content) {
        try {
            StringBuilder message = new StringBuilder();
            //没有userId无法发帖子
            if (content.getUserId()==null){
                return CommonResult.failed("发帖失败，用户ID缺失！");
            }
            int publishResult = contentService.publishContent(content);
            if (publishResult == -1) return CommonResult.failed("发帖失败，权限异常！");
            if (publishResult == -2) return CommonResult.failed("发帖过于频繁，请稍后再试！");

            log.info("发帖成功！帖子信息：" + content);
            return CommonResult.success("发帖成功！", content);
        } catch (Exception e) {
            log.warn("发帖失败！" + e.getMessage());
            return CommonResult.failed(e.getMessage());
        }
    }

    //频繁发帖检查提前到审核中
    @ApiOperation("增加帖子信息")
    @PostMapping("/publishContentApi0605")
    public CommonResult publishContent0605(@RequestBody Content content) {
        try {
            StringBuilder message = new StringBuilder();
            //没有userId无法发帖子
            if (content.getUserId()==null){
                return CommonResult.failed("发帖失败，用户ID缺失！");
            }
            //频繁发帖检查提前到审核中
            int publishResult = contentService.publishContent0605(content);
            if (publishResult == -1) return CommonResult.failed("发帖失败，权限异常！");
            if (publishResult == -2) return CommonResult.failed("发帖过于频繁，请稍后再试！");

            log.info("发帖成功！帖子信息：" + content);
            return CommonResult.success("发帖成功！", content);
        } catch (Exception e) {
            log.warn("发帖失败！" + e.getMessage());
            return CommonResult.failed(e.getMessage());
        }
    }

    @ApiOperation("用户投票")
    @PostMapping("/doVoteApi")
    public CommonResult doVote(@RequestBody VoteResult[] voteResult) {
        try {
            for (int i = 0; i < voteResult.length; i++) {
                voteService.doVote(voteResult[i]);
            }
            log.info("投票成功！");
            return CommonResult.success("投票成功！");
        } catch (Exception e) {
            log.warn("投票失败！" + e);
            return CommonResult.failed(e.getMessage());
        }


    }

    @ApiOperation("获取首页帖子列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "当前浏览用户id"),
            @ApiImplicitParam(name = "searchUserId", value = "帖子拥有者id"),
            @ApiImplicitParam(name = "plateId", value = "当前板块id"),
            @ApiImplicitParam(name = "pageNum", value = "当前页数", required = true),
            @ApiImplicitParam(name = "pageSize", value = "页面显示条数", required = true),
            @ApiImplicitParam(name = "schoolId", value = "学校id", required = true),
            @ApiImplicitParam(name = "requestType", value = "请求方式", required = true),
            @ApiImplicitParam(name = "contentType", value = "帖子类型"),
            @ApiImplicitParam(name = "duration", value = "时间间隔"),
            @ApiImplicitParam(name = "queryTime", value = "查询时间"),
            @ApiImplicitParam(name = "hideSecondHand", value = "隐藏闲置交易")

    })
    @GetMapping("/getHomeContentsApi")
    public CommonResult getHomeContents(String userId, @RequestParam(defaultValue = "") String searchUserId, @RequestParam(defaultValue = "") String plateId, int pageNum, int pageSize, @RequestParam(defaultValue = "-1") int schoolId, @RequestParam(defaultValue = "") String requestType, @RequestParam(defaultValue = "-1") int contentType, @RequestParam(defaultValue = "0") int duration, String queryTime, @RequestParam(defaultValue = "0") int hideSecondHand) {
        try {
            List<HomeContentVO> list;
            //学校id异常
            if(schoolId==-1){
                log.info("学校id异常，首页内容列表获取失败！");
                return CommonResult.failed("学校id异常，首页内容列表获取失败！");
            }
            if (queryTime !=null && !queryTime.equals("")){
                list = contentService.getHomeContentsWithQueryTimeUseRedis0511(userId, searchUserId, plateId, pageNum, pageSize, schoolId, requestType, contentType, duration,queryTime,hideSecondHand);
            }
            else{
                list = contentService.getHomeContents(userId, searchUserId, plateId, pageNum, pageSize, schoolId, requestType, contentType, duration);
            }
            //log.info("首页内容列表获取成功！");
            return CommonResult.success("首页内容列表获取成功！", list);
        } catch (Exception e) {
            log.info("首页内容列表获取失败！");
            log.info(e.getMessage());
            return CommonResult.failed(e.getMessage());
        }
    }

    @ApiOperation("根据帖子id获取帖子内容")
    @GetMapping("/getContentById")
    public CommonResult getContentById(String contentId, String userId) {
        HomeContentVO contentById = contentService.getContentById(contentId, userId);
        if (contentById.getContentType() == 3 || contentById.getIsSpecial() == 1 || contentById.getIsSpecial() == 4) { //匿名贴处理，兼容新旧类型
            contentById = contentService.dealAnonymous(contentById);
        }
        if (contentById == null) {
            return CommonResult.failed("帖子不存在");
        } else {
            return CommonResult.success("帖子获取成功", contentById);
        }
    }


    @ApiOperation("获取学校列表")
    @GetMapping("/getSchoolList")
    public CommonResult getSchoolList(String appId) {
        List<SchoolVO> schoolList = schoolService.getSchoolList(appId);
        if (schoolList != null) {
            return CommonResult.success("获取成功", schoolList);
        } else {
            return CommonResult.failed("获取失败");
        }
    }

    @ApiOperation("搜索学校列表")
    @GetMapping("/searchSchoolList")
    public CommonResult searchSchoolList(String appId, String searchText) {
        List<SchoolVO> schoolList = schoolService.searchSchoolList(appId,searchText);
        if (schoolList != null) {
            return CommonResult.success("获取成功", schoolList);
        } else {
            return CommonResult.failed("获取失败");
        }
    }

    @ApiOperation("增加学校")
    @PostMapping("/addSchool")
    public CommonResult addSchool(SchoolVO schoolVO) {
        int message = schoolService.addSchool(schoolVO);
        return CommonResult.success("增加学校成功", message);
    }

    @ApiOperation("首次阅读判断")
    @PostMapping("/isFirstRead")
    public CommonResult isFirstRead(String contentId, String userId) {
        boolean success = userContentService.isFirstRead(contentId,userId);
        if (success) {
            return CommonResult.success("首次阅读");
        } else {
            return CommonResult.failed("非首次阅读");
        }

    }

    @ApiOperation("首次阅读判断")
    @PostMapping("/isFirstReadNew")
    public CommonResult isFirstReadNew(String contentId, String userId) {
        boolean success = userContentService.isFirstReadNew(contentId,userId);
        if (success) {
            return CommonResult.success("首次阅读",success);
        } else {
            return CommonResult.success("非首次阅读",success);
        }

    }

    private HomeContentVO dealAnonymous(HomeContentVO content) {
        String newName = getChineseName(content.getContentId(),content.getUserId());
        String code = "";
        for (int j = 0; j < 7; j++) {
            if (j % 2 == 0) {
                code += content.getContentId().charAt(j);
            } else {
                code += content.getUserId().charAt(j);
            }

        }
        newName = newName + content.getUserId().substring(0,3);
        content.setNickName(newName);
        //根据用户ID和帖子ID，生成该帖子下的唯一用户头像
        String regEx = "[^0-9]"; //提取所有数字
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(code);
        String num = m.replaceAll("").trim();
        //纯字符串处理
        if (num.equals("")) num = "0";
        int imageId = Integer.valueOf(num);
        imageId = imageId % 50;
        String headimgUrl = "https://image.tanxiaojian.zone/user/anonymous/" + String.valueOf(imageId) +
                ".png";
        content.setHeadimgUrl(headimgUrl);
        return content;
    }

    @ApiOperation("时光机与精选热帖")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "当前浏览用户id"),
            @ApiImplicitParam(name = "pageNum", value = "当前页数", required = true),
            @ApiImplicitParam(name = "pageSize", value = "页面显示条数", required = true),
            @ApiImplicitParam(name = "schoolId", value = "学校id", required = true),
            @ApiImplicitParam(name = "requestType", value = "请求方式", required = true),
            @ApiImplicitParam(name = "contentType", value = "帖子类型"),
            @ApiImplicitParam(name = "duration", value = "跨越时间段" )
    })
    @GetMapping("/getHistoryContentsApi")
    public CommonResult getHistoryContents(String userId, int pageNum, int pageSize, @RequestParam(defaultValue = "0") int schoolId, @RequestParam(defaultValue = "") String requestType, @RequestParam(defaultValue = "-1") int contentType,@RequestParam(defaultValue = "0") int duration) {
        try {

            List<HomeContentVO> list = contentService.getHistoryContents(userId, pageNum, pageSize,schoolId, requestType,contentType, duration);
            log.info("时光机/历史热帖列表获取成功！");
            return CommonResult.success("时光机/历史热帖列表获取成功！", list);
        } catch (Exception e) {
            log.info("时光机/历史热帖列表获取失败！");
            log.info(e.getMessage());
            return CommonResult.failed(e.getMessage());
        }
    }

    @ApiOperation("通过帖子id列表获取图片base64")
    @GetMapping("/getImageBase64ByContent")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "contentIds", value = "帖子id列表", required = true),
    })
    public CommonResult getImageBase64ByContent(String contentIds ) {
        try {
            JSONArray result = contentService.getImageBase64ByContentIds(contentIds);
            return CommonResult.success("获取成功！",result);

        } catch (Exception e) {
            log.warn("更新失败！：" + e.getMessage());
            return CommonResult.failed("更新失败，接口异常！");
        }


    }

    @ApiOperation("获取首页帖子列表的图片base64")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "plateId", value = "当前板块id"),
            @ApiImplicitParam(name = "pageNum", value = "当前页数", required = true),
            @ApiImplicitParam(name = "pageSize", value = "页面显示条数", required = true),
            @ApiImplicitParam(name = "schoolId", value = "学校id", required = true),
            @ApiImplicitParam(name = "contentType", value = "帖子类型"),
            @ApiImplicitParam(name = "queryTime", value = "查询时间")

    })
    @GetMapping("/getHomeContentsImage")
    public CommonResult getHomeContentsImage( @RequestParam(defaultValue = "") String plateId, int pageNum, int pageSize, @RequestParam(defaultValue = "0") int schoolId, @RequestParam(defaultValue = "-1") int contentType, String queryTime) {
        try {
            JSONArray result = contentService.getImageBase64WithQueryTime(pageNum,pageSize,schoolId,contentType,queryTime,plateId);
            return CommonResult.success("获取成功！",result);
        } catch (Exception e) {
            log.info("首页内容列表获取失败！");
            log.info(e.getMessage());
            return CommonResult.failed(e.getMessage());
        }
    }

    @ApiOperation("按帖子类型搜索帖子")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "当前浏览用户id"),
            @ApiImplicitParam(name = "pageNum", value = "当前页数", required = true),
            @ApiImplicitParam(name = "pageSize", value = "页面显示条数", required = true),
            @ApiImplicitParam(name = "schoolId", value = "学校id", required = true),
            @ApiImplicitParam(name = "keywords", value = "检索词", required = true),
            @ApiImplicitParam(name = "contentType", value = "帖子类型"),
            @ApiImplicitParam(name = "queryTime", value = "查询时间")

    })
    @GetMapping("/searchContentsByContentType")
    public CommonResult searchContentsByContentType(String userId, int pageNum, int pageSize, int schoolId, String keywords, int contentType, String queryTime) {
        try {
            List<HomeContentVO> list;
            //学校id异常
            if(schoolId==-1){
                log.info("学校id异常，检索帖子失败！");
                return CommonResult.failed("学校id异常，检索帖子失败！");
            }
            List<HomeContentVO> result = contentService.searchContentsByContentType(userId, pageNum,pageSize,schoolId, contentType, keywords, queryTime);
            return CommonResult.success("帖子检索成功！", result);
        } catch (Exception e) {
            log.info("检索帖子失败！");
            log.info(e.getMessage());
            return CommonResult.failed(e.getMessage());
        }
    }

    @ApiOperation("获取某用户所有帖子列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "搜索用户id"),
            @ApiImplicitParam(name = "pageNum", value = "当前页数", required = true),
            @ApiImplicitParam(name = "pageSize", value = "页面显示条数", required = true),
            @ApiImplicitParam(name = "schoolId", value = "学校id", required = true),

    })
    @GetMapping("/getAllHomeContentsApi")
    public CommonResult getAllHomeContentsApi(String userId, int pageNum, int pageSize, @RequestParam(defaultValue = "-1") int schoolId) {
        try {
            List<HomeContentVO> list;
            //学校id异常
            if(schoolId==-1){
                log.info("学校id异常，用户所有帖子列表获取失败！");
                return CommonResult.failed("学校id异常，用户所有帖子列表获取失败！");
            }
            list = contentService.getAllHomeContents(userId, pageNum, pageSize, schoolId);
            //log.info("首页内容列表获取成功！");
            return CommonResult.success("用户所有帖子列表获取成功！", list);
        } catch (Exception e) {
            log.info("用户所有帖子列表获取失败！");
            log.info(e.getMessage());
            return CommonResult.failed(e.getMessage());
        }
    }



}
