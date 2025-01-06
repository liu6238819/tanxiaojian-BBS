package com.example.bbs.controller;

import com.auth0.jwt.JWT;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.bbs.pojo.Plate;
import com.example.bbs.pojo.User;
import com.example.bbs.pojo.vo.EditPlateInfoVO;
import com.example.bbs.pojo.vo.SearchPlateVO;
import com.example.bbs.result.CommonResult;
import com.example.bbs.service.PlateManageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 功能描述：
 *
 * @Author: lyc
 * @Date: 2022/2/7 4:09 下午
 */
//@Slf4j
//@CrossOrigin
//@RestController
//@RequestMapping("/plateManage")
//@Api(tags = "板块管理")
public class PlateManageController {


    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private HttpServletResponse httpServletResponse;

    @Autowired
    private PlateManageService plateManageService;

    @ApiOperation("多条件获取板块信息")
    @GetMapping("/getPlateList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "SearchPlateVO",value = "搜索板块条件结构体"),
            @ApiImplicitParam(name = "pageNum",value="页数限额"),
            @ApiImplicitParam(name = "pageSize",value = "页数码"),
    })
    public CommonResult getPlateList(SearchPlateVO searchPlateVO,Integer pageNum,Integer pageSize){
        String token = httpServletRequest.getHeader("token");
        Boolean res = plateManageService.judgeTokenValid(token);
        if(res.equals(true)){
            IPage<Plate> plateList = plateManageService.getSearchPlateList(searchPlateVO,pageNum,pageSize);
            return CommonResult.success(plateList);
        }else{
            return CommonResult.failed("抱歉，您无权获取板块信息");
        }
    }

    @ApiOperation("编辑板块信息")
    @PostMapping("/editPlateInfo")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "EditPlateInfoVO",value = "编辑板块信息结构体"),
    })
    public CommonResult editPlateInfo(EditPlateInfoVO editPlateInfoVO){
        String token = httpServletRequest.getHeader("token");
        Boolean res = plateManageService.judgeTokenValid(token);
        if(res.equals(true)){
            Integer result = plateManageService.updatePlateInfo(editPlateInfoVO);
            if(result!=null){
                return CommonResult.success("修改板块信息成功");
            }else{
                return CommonResult.success("用户修改板块失败");
            }
        }else{
            return CommonResult.failed("抱歉，您无权获取板块信息");
        }
    }

}
