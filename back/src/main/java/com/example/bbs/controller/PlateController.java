package com.example.bbs.controller;

import com.example.bbs.pojo.Cate;
import com.example.bbs.pojo.Plate;
import com.example.bbs.pojo.vo.PlateVO;
import com.example.bbs.pojo.User_Plate;
import com.example.bbs.result.CommonResult;
import com.example.bbs.service.PlateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/plate")
@Api(tags = "板块相关操作")
public class PlateController {

    @Autowired
    PlateService plateService;

    @ApiOperation("创建板块")
    @PostMapping("/createPlate")
    public CommonResult createPlate(Plate plate) {
        Plate plateResult = plateService.createPlate(plate);
        if (plateResult.getPlateId()!=null) {
            return CommonResult.success(plateResult.getPlateId());
        } else  {
            return CommonResult.failed(plateResult.getName());
        }
    }

    @ApiOperation("获取类目列表")
    @GetMapping("/getClassLists")
    public List<Cate> getClassLists() {
        List<Cate> classLists = plateService.getClassLists();
        return classLists;
    }

    @ApiOperation("根据学校Id和类目id获取板块列表")
    @GetMapping("/getPlateLists")
    public List<PlateVO> getPlateLists(Integer schoolId, @RequestParam(defaultValue = "0") Integer cateId) {
        List<Plate> plateLists = plateService.getPlateLists(schoolId, cateId);
        ArrayList<PlateVO> plateVOArrayList = new ArrayList<>();
        for (int i = 0; i < plateLists.size(); i++) {
            PlateVO e = new PlateVO(plateLists.get(i));
            plateVOArrayList.add(e);
        }
        return plateVOArrayList;
    }

    @ApiOperation("根据板块id与用户id获取板块信息")
    @GetMapping("/getPlateInfoById")
    public PlateVO getPlateInfo(String plateId, String userId) {

        //根据plateId获取plate表中的信息
        Plate plate = plateService.getPlateInfo(plateId);

        //根据plate构造plateInfo
        PlateVO plateVO = new PlateVO(plate);

        //判断当前用户是否关注
        boolean join = plateService.isJoin(plateId, userId);
        plateVO.setJoin(join);

        return plateVO;
    }

    @ApiOperation("关注板块")
    @PostMapping("/joinPlate")
    public CommonResult joinPlate(User_Plate user_pLate) {
        boolean success = plateService.joinPlate(user_pLate);
        if (success) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }

    }

    @ApiOperation("取关板块")
    @PostMapping("/leavePlate")
    public CommonResult leavePlate(User_Plate user_pLate) {
        System.out.println(user_pLate);
        boolean success = plateService.leavePlate(user_pLate);
        if (success) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }

    }

    @ApiOperation("解散板块")
    @PostMapping("/deletePlate")
    public CommonResult deletePlate(User_Plate user_pLate) {
        System.out.println(user_pLate);
        boolean success = plateService.deletePlate(user_pLate);
        if (success) {
            return CommonResult.success();
        } else {
            return CommonResult.failed("操作失败！");
        }

    }

    @ApiOperation("获取用户关注板块")
    @PostMapping("/getFocusedPlates")
    public List<Plate> getFocusedPlates(String userId,Integer schoolId) {
        List<Plate> plateLists = plateService.getFocusedPlates(userId,schoolId);
        return plateLists;

    }
}
