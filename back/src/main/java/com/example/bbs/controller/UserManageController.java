package com.example.bbs.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.bbs.auto.UserTypeAutoEdit;
import com.example.bbs.pojo.Plate;
import com.example.bbs.pojo.User;
import com.example.bbs.pojo.vo.EditUserInfoVO;
import com.example.bbs.pojo.vo.SearchUserVO;
import com.example.bbs.result.CommonResult;
import com.example.bbs.service.PlateManageService;
import com.example.bbs.service.TokenService;
import com.example.bbs.service.UserManageService;
import com.example.bbs.service.UserService;
import com.example.bbs.util.IpUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 功能描述：
 *
 * @Author: lyc
 * @Date: 2022/2/3 10:38 上午
 */
@Slf4j
//@CrossOrigin
//@RestController
//@RequestMapping("/userManage")
//@Api(tags = "用户管理")
public class UserManageController {
    @Autowired
    UserManageService UserManageService;

    @Autowired
    UserTypeAutoEdit userTypeAutoEdit;

    @Autowired
    TokenService tokenService;

    @Autowired
    UserService userService;

    @Autowired
    PlateManageService plateManageService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private HttpServletResponse httpServletResponse;

    @ApiOperation("获取用户列表")
    @GetMapping("/getUserList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "searchUserVO", value = "搜索用户条件结构体"),
            @ApiImplicitParam(name = "pageNum", value = "页数限额"),
            @ApiImplicitParam(name = "pageSize", value = "页数码")
    })
//    HttpServletRequest request,
    public CommonResult getUserList(SearchUserVO searchUserVO, Integer pageNum, Integer pageSize) {
        String token = httpServletRequest.getHeader("token");
        Boolean res = plateManageService.judgeTokenValid(token);
        if (res.equals(true)) {
            IPage<User> userList = UserManageService.getSearchUserList(searchUserVO, pageNum, pageSize);
            if (userList.getRecords().size() > 1) {
                return CommonResult.success("成功获取用户列表信息！", userList);
            } else {
                return CommonResult.failed("当前没有符合条件的用户！");
            }
        } else {
            return CommonResult.failed("抱歉，您无权搜索相关用户");
        }
    }

    @ApiOperation("修改用户信息")
    @PostMapping("/editUserInfo")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "editUserVO", value = "编辑用户信息")
    })
    public CommonResult editUser(EditUserInfoVO editUserVO) {
        String token = httpServletRequest.getHeader("token");
        Boolean res = plateManageService.judgeTokenValid(token);
        if (res.equals(true)) {
            Integer result = UserManageService.editUser(editUserVO);
            if (result != null) {
                return CommonResult.success("修改信息成功");
            } else {
                return CommonResult.success("用户修改信息失败");
            }
        } else {
            return CommonResult.failed("抱歉，您无权编辑相关用户的信息");
        }
    }

    //    /adminLogin  管理员登录的接口
    @ApiOperation("管理员登录")
    @PostMapping("/adminLogin")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "phone", value = "用户电话"),
            @ApiImplicitParam(name = "password", value = "用户密码"),
    })
    public CommonResult adminLogin(String phone, String password, HttpServletRequest request) {
        //获取本次登录ip
        String ipAddr = IpUtil.getIpAddr(request);
        User user = userService.userLogin(phone, password);
        //生成token
        String token = tokenService.generateToken(phone, password);
        if (user == null) {
            log.warn("用户登录失败！手机号：" + phone);
            return CommonResult.failed("账号或者密码错误！");
        } else {
            user.setLastIp(ipAddr);
            userService.editUserInfos(user);
            log.info("用户登录成功！用户信息：" + user);
            //预警用户变为正常
            userTypeAutoEdit.WarnToNormalUser(user);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("token", token);
            return CommonResult.success(jsonObject);
        }
    }


}
