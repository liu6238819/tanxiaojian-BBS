package com.example.bbs.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.bbs.auto.UserTypeAutoEdit;
import com.example.bbs.mapper.UserMapper;
import com.example.bbs.mapper.UserSchoolMapper;
import com.example.bbs.pojo.User;
import com.example.bbs.pojo.UserSchool;
import com.example.bbs.result.CommonResult;
import com.example.bbs.service.*;
import com.example.bbs.util.IpUtil;
import com.example.bbs.util.ScoreUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.bbs.util.DateUtil.initToday;
import static com.example.bbs.util.DateUtil.initYesterday;

@Slf4j
@RestController
@RequestMapping("/user")
@Api(tags = "用户相关操作")
public class UserController {
    @Autowired
    UserService userService;

    @Autowired
    UserSchoolService userSchoolService;
    @Autowired
    UserTypeAutoEdit userTypeAutoEdit;
    @Autowired
    UserMapper userMapper;
    @Autowired
    UserSchoolMapper userSchoolMapper;

    @Autowired
    FocusService focusService;

    @Autowired
    ScoreUtil scoreUtil;


    @Autowired
    LoginRecordService loginRecordService;

    @Autowired
    UserPhoneService userPhoneService;

    @CrossOrigin
    @ApiOperation("注册用户信息")
    @PostMapping("/userRegister")
    public CommonResult userRegister(User user, HttpServletRequest request) {
        System.out.println(user);
        String ipAddr = IpUtil.getIpAddr(request);
        System.out.println(ipAddr);
        boolean b = userService.userRegister(user, ipAddr);
        if (b) {
            log.info("用户注册成功！用户信息：" + user);
            return CommonResult.success("注册成功");
        } else {
            log.warn("用户注册失败！用户信息：" + user);
            return CommonResult.failed("当前账号已经注册过！");
        }
    }

    @ApiOperation("用户登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "phone", value = "手机号"),
            @ApiImplicitParam(name = "password", value = "密码")
    })
    @PostMapping("/userLoginApi")
    public CommonResult userLogin(String phone, String password, HttpServletRequest request) {
        //获取本次登录ip
        String ipAddr = IpUtil.getIpAddr(request);
        User user = userService.userLogin(phone, password);
        if (user == null) {
            log.warn("用户登录失败！手机号：" + phone);
            return CommonResult.failed("账号或者密码错误！");
        } else {
            user.setLastIp(ipAddr);
            userService.editUserInfos(user);
            log.info("用户登录成功！用户信息：" + user);
            //预警用户变为正常
            userTypeAutoEdit.WarnToNormalUser(user);
            //去除用户敏感信息
            user.setPhone(null);
            user.setPassword(null);
            user.setIdCardUrl(null);
            user.setLastIp(null);
            return CommonResult.success(user);
        }
    }


    //    微信用户登录接口
    @PostMapping("/userLoginWX")
    public CommonResult userLoginWX(String openId, HttpServletRequest request) {
        //获取本次登录ip
        String ipAddr = IpUtil.getIpAddr(request);
        User user = userService.userLoginWX(openId);
        //数据库和redis查询都为null
        if (user == null) {
            return CommonResult.failed("用户登录失败！");
        } else {
            //日期数值初始化
            Date lastLoginTime = user.getLastLoginTime();
            Date todayZero = initToday();
            Date yesterdayZero = initYesterday();
            Date todayNow = new Date();
            if (null == lastLoginTime) lastLoginTime = user.getUpdateTime(); //无上次登录时间的情况
            if (!StringUtils.isBlank(user.getHeadimgUrl())) {//授权用户，获取每日登录积分
                //逻辑判断
                if (lastLoginTime.after(todayZero)) { //今日多次登录
                    System.out.println("今日多次登录，不获取积分");
                } else if (todayZero.after(lastLoginTime)) {//今日首次登录
                    System.out.println("今日首次登录，获取积分+10");
                    //此处只在score表中增加一行记录，需更新积分
                    scoreUtil.changeScore(user.getUserId(), 4, 20, null);
                    user.setScores(user.getScores() + 20);
                }
            }
            //更新记录
            user.setLastIp(ipAddr);
            user.setUpdateTime(new Date());
            user.setLastLoginTime(todayNow);
            userService.editUserInfos(user);
            log.info("用户登录成功！用户信息：" + user);

            if (!StringUtils.isBlank(user.getPhone())){
                user.setPhone("12345678");
            }
            user.setPassword(null);
            user.setIdCardUrl(null);
            user.setLastIp(null);
            user.setOpenId(null);
            return CommonResult.success(user);
        }
    }

    @ApiOperation("修改用户信息")
    @PostMapping("/editUserInfosApi")
    public CommonResult editUserInfos(User user) {
        if (userService.editUserInfoFront(user) >= 1) {
            log.info("用户信息修改成功！用户信息：" + user);
            if(!StringUtils.isBlank(user.getPhone())){
                userPhoneService.createUserPhoneRecord(user);
            }
            return CommonResult.success("信息修改成功！");
        } else {
            log.warn("用户信息修改失败！用户信息：" + user);
            return CommonResult.failed("用户信息更新失败！请检测名称是否为空，或存在特殊字符");
        }
    }



    //校友身份认证
    @ApiOperation("校友身份认证")
    @PostMapping("/identification")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "schoolId", value = "学校ID", required = true),
            @ApiImplicitParam(name = "userId", value = "用户ID", required = true),
            @ApiImplicitParam(name = "stuNum", value = "学号"),
            @ApiImplicitParam(name = "idCardUrl", value = "身份认证照片", required = true),
            @ApiImplicitParam(name = "email", value = "邮箱"),
            @ApiImplicitParam(name = "inviteCode", value = "邀请码"),
            @ApiImplicitParam(name = "sex", value = "性别", required = true)
    })
    public CommonResult identification(Integer schoolId, String userId, String idCardUrl, String stuNum, String email, String inviteCode, Integer sex) {
        //在userSchool表中判断学号是否已注册
        if (stuNum != null && !stuNum.equals("")) {
            QueryWrapper<UserSchool> userSchoolQueryWrapper = new QueryWrapper<>();
            userSchoolQueryWrapper.eq("stuNum", stuNum);
            userSchoolQueryWrapper.eq("schoolId", schoolId);
            List<UserSchool> userStuNum = userSchoolMapper.selectList(userSchoolQueryWrapper);
            int recordNum = 0;
            if (null != userStuNum) recordNum = userStuNum.size();
            if (recordNum > 0) {
                return CommonResult.exist("该学号已被注册！若非本人操作，请联系客服申诉");
            }
        }
        // 进user表更新记录
        User user = userService.getUserByUserId(userId);
        //根据userId从库里面查询到user的信息
        if (idCardUrl != null) {
            user.setIdCardUrl(idCardUrl);
        }
        if (email != null) {
            user.setEmail(email);
        }
        if (stuNum != null && !stuNum.equals("")) {
            user.setStuNum(stuNum);
        }
        if (sex != null) {
            user.setSex(sex);
        }
        if (inviteCode != null) {
            user.setInviter(inviteCode);
        }
        userMapper.updateById(user);
        //若原本无记录，新增一条记录在user-school表里面,处理传递过来的数据school数组
        QueryWrapper<UserSchool> userSchoolQueryWrapper = new QueryWrapper<>();
        userSchoolQueryWrapper.eq("userId", userId)
                .eq("schoolId", schoolId)
                .orderByAsc("createTime");
        UserSchool oldRecord=new UserSchool();
        List <UserSchool> userSchoolList = userSchoolMapper.selectList(userSchoolQueryWrapper);
        if (userSchoolList==null){
            return CommonResult.exist("查询出错");
        }
        if (userSchoolList.size()==0) {
            UserSchool userSchool = new UserSchool();
            userSchool.setUserState(1);
            userSchool.setUserType(0);
            userSchool.setUserId(userId);
            userSchool.setSchoolId(schoolId);
            //记录身份证明、学号和邀请人
            if (idCardUrl != null) {
                userSchool.setIdCardUrl(idCardUrl);
            }
            if (stuNum != null && !stuNum.equals("")) {
                userSchool.setStuNum(stuNum);
            }
            if (inviteCode != null) {
                userSchool.setInviter(inviteCode);
            }
            userSchoolMapper.insert(userSchool);
        } else {
            oldRecord= userSchoolList.get(0);
            //有记录，关系重置为审核中，身份信息重置,不新增
            oldRecord.setUserState(1);
            if (idCardUrl != null) {
                oldRecord.setIdCardUrl(idCardUrl);
            }
            if (stuNum != null && !stuNum.equals("")) {
                oldRecord.setStuNum(stuNum);
            }
            if (inviteCode != null) {
                oldRecord.setInviter(inviteCode);
            }
            userSchoolMapper.updateById(oldRecord);
        }
        return CommonResult.success("校友身份认证请求成功");
    }

    //关注接口
    @ApiOperation("用户关注接口")
    @PostMapping("/followUser")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户ID", required = true),
            @ApiImplicitParam(name = "targetId", value = "目标用户ID", required = true),
            @ApiImplicitParam(name = "isFollow", value = "是否关注", required = true)
    })
    public CommonResult followUser(String userId, String targetId, Integer isFollow) {
        Integer res1 = userService.FollowRecordUpdate(userId, targetId, isFollow);
        Integer res2 = focusService.addRemind(userId, targetId, isFollow);
        if (res1 == 0) {
            return CommonResult.exist("用户自己不能关注自己");
        } else {
            if (res1 == 1 && res2 == 1) {
                return CommonResult.success("用户关注成功");
            }
            if (res1 == 1 && res2 == 0) {
                return CommonResult.success("用户取消关注成功");
            }

            return CommonResult.failed("接口请求失败");
        }
    }

    @ApiOperation("通过id查询用户的接口")
    @PostMapping("/getUserInfoById")
    //根据用户ID获取用户信息
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户ID", required = true),
            @ApiImplicitParam(name = "searchId", value = "搜索用户的ID", required = true),
            @ApiImplicitParam(name = "schoolId", value = "学校ID", required = true),
    })
    public CommonResult getUserInfoById(String userId, String searchId, Integer schoolId) {
        //根据用户ID去表里查寻用户
        //根据用户ID去关注表里面查询searchId和userId相同的时候的数据
        JSONObject obj = userService.getUserInfoById(userId, searchId,schoolId);
        return CommonResult.success(obj);
    }

    @ApiOperation("获取关注用户接口")
    @GetMapping("/getFollowedUsers")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户ID", required = true),
            @ApiImplicitParam(name = "type", value = "类型", required = true),
            @ApiImplicitParam(name = "pageNum", value = "页码数", required = true),
            @ApiImplicitParam(name = "pageSize", value = "页面信息条数", required = true)
    })
    public CommonResult getFollowedUsers(String userId, Integer type, Integer pageNum, Integer pageSize) {
        JSONObject usersObject = userService.getFollowedUsers(userId, type, pageNum, pageSize);
        return CommonResult.success(usersObject);
    }

    @ApiOperation("获取用户感兴趣信息的接口")
    @GetMapping("/getLikedContents")
    //根据用户ID获取用户信息
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户ID", required = true),
            @ApiImplicitParam(name = "pageNum", value = "页码数", required = true),
            @ApiImplicitParam(name = "pageSize", value = "页面信息条数", required = true)
    })
    public CommonResult getLikedContents(String userId, Integer pageNum, Integer pageSize, Integer schoolId) {
        JSONObject likedObject = userService.getLikedContents(userId, pageNum, pageSize, schoolId);
        return CommonResult.success(likedObject);
    }

    @ApiOperation("获取用户在对应学校下的状态")
    @GetMapping("/getUserStateBySchool")
    //根据用户ID获取用户信息
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户ID", required = true),
            @ApiImplicitParam(name = "schoolId", value = "学校ID", required = true)
    })
    public CommonResult getUserStateBySchool(String userId, Integer schoolId) {
        JSONObject stateObject = userService.getUserStateBySchool(userId, schoolId);
        return CommonResult.success(stateObject);
    }

    @ApiOperation("用户成功认证")
    @PostMapping("/answerIdentification")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", required = true),
            @ApiImplicitParam(name = "schoolId", value = "学校id", required = true),
    })
    public CommonResult answerIdentification(String userId, int schoolId) {
        if (StringUtils.isBlank(userId)) {
            return CommonResult.failed("非法用户访问");
        }
        try {
            int result = userSchoolService.answerIdentification(userId, schoolId);
            if (result == 2) return CommonResult.failed("认证失败，用户不存在或未完成授权！");
            int newScore = scoreUtil.changeScore(userId, 5,200,"anwser");
            if (newScore == -1) return CommonResult.failed("积分改变失败！");
            return CommonResult.success("答题认证成功！");

        } catch (Exception e) {
            log.warn("申请失败！：" + e.getMessage());
            return CommonResult.failed("认证失败，接口异常！");
        }
    }

    @ApiOperation("用户是否存在其他学校已认证记录")
    @GetMapping("/haveIdentifiedInOtherSchool")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", required = true),
            @ApiImplicitParam(name = "schoolId", value = "学校id", required = true),
    })
    public CommonResult haveIdentifiedInOtherSchool(String userId, int schoolId) {
        if (StringUtils.isBlank(userId)) {
            return CommonResult.failed("非法用户访问");
        }
        try {
            int result = userSchoolService.haveIdentifiedInOtherSchool(userId, schoolId);
            if (result == 0) return CommonResult.success("认证成功！",result);
            return CommonResult.success("您已在其他学校认证，请联系客服核实校友身份",result);

        } catch (Exception e) {
            log.warn("查询失败！：" + e.getMessage());
            return CommonResult.failed("查询失败，接口异常！");
        }

    }

    @ApiOperation("插入用户登录记录")
    @PostMapping("/insertUserLoginRecord")
    public CommonResult insertUserLoginRecord(String userId ,int schoolId, String loginPath) {
        int result = loginRecordService.createLoginRecord(userId,schoolId,loginPath);
        if (result==1) {
            return CommonResult.success("插入记录成功");
        } else {
            return CommonResult.failed("插入记录失败");
        }

    }

    @ApiOperation("管理员修改用户信息")
    @PostMapping("/manageUserInfosApi")
    public CommonResult manageUserInfos(UserSchool userSchool) {
        if (userService.manageUserInfo(userSchool) >= 1) {
            log.info("用户信息修改成功！用户信息：" + userSchool);
            return CommonResult.success("信息修改成功！");
        } else {
            log.warn("用户信息修改失败！用户信息：" + userSchool);
            return CommonResult.failed("用户信息更新失败！请检测名称是否为空，或存在特殊字符");
        }
    }

    @ApiOperation("用户注销")
    @PostMapping("/userLogOut")
    //用户注销
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户ID", required = true),
            @ApiImplicitParam(name = "headImgUrl", value = "默认头像", required = true),
            @ApiImplicitParam(name = "nickName", value = "昵称前缀", required = true),
            @ApiImplicitParam(name = "schoolId", value = "学校id", required = true),

    })
    public CommonResult userLogOut(String userId ,String headImgUrl ,String nickName,int schoolId) {
        int result = userService.userLogOut(userId, headImgUrl, nickName, schoolId);
        if (result==0){
            return CommonResult.success("无用户ID",result);
        }
        else if(result==2){
            return CommonResult.success("180天内注销过账号",result);
        }
        return CommonResult.success("账号注销成功",result);
    }

    @ApiOperation("在变更学校时获取用户在对应学校下的状态并更新用户的最新登录时间")
    @GetMapping("/getUserStateWhenChangeSchool")
    //根据用户ID获取用户信息
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户ID", required = true),
            @ApiImplicitParam(name = "schoolId", value = "学校ID", required = true),
    })
    public CommonResult getUserStateWhenChangeSchool(String userId, Integer schoolId) {
        JSONObject stateObject = userService.getUserStateBySchool(userId, schoolId);
        //更新用户的最后登录时间
        User user =userService.getUserByUserId(userId);
        if (user !=null){
            user.setLastLoginTime(new Date());
            userService.editUserInfos(user);
        }
        return CommonResult.success(stateObject);
    }

}
