package com.example.bbs.service.impl;

import com.auth0.jwt.JWT;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.bbs.mapper.PlateManageMapper;
import com.example.bbs.mapper.UserManageMapper;
import com.example.bbs.pojo.Plate;
import com.example.bbs.pojo.User;
import com.example.bbs.pojo.vo.EditPlateInfoVO;
import com.example.bbs.pojo.vo.SearchPlateVO;
import com.example.bbs.service.PlateManageService;
import com.example.bbs.service.PlateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 功能描述：
 *
 * @Author: lyc
 * @Date: 2022/2/7 1:31 下午
 */

@Service
public class PlateManageServiceImpl implements PlateManageService {

    @Autowired
    private PlateManageMapper plateManageMapper;

    @Autowired
    private UserManageMapper userManageMapper;


    @Override
    public IPage<Plate> getSearchPlateList(SearchPlateVO searchPlateVO, int pageNum, int pageSize) {
//        return null;

        QueryWrapper<Plate> queryWrapper = new QueryWrapper<>();
        //板块名称
        if(!Objects.equals(searchPlateVO.getName(), null)){
            queryWrapper.eq("name",searchPlateVO.getName());
        }
        //板块所属学校
        if(!Objects.equals(searchPlateVO.getSchool(), null)){
            queryWrapper.eq("school",searchPlateVO.getSchool());
        }
        //cateId板块类目
        if(!Objects.equals(searchPlateVO.getCateId(),null)){
            queryWrapper.eq("cateId",searchPlateVO.getCateId());
        }
        if(!Objects.equals(searchPlateVO.getPlateState(), null)){
            queryWrapper.eq("plateState",searchPlateVO.getPlateState());
        }
        //板块创建者Id
        if(!Objects.equals(searchPlateVO.getUserId(), null)){
            queryWrapper.eq("userId",searchPlateVO.getUserId());
        }
        Page<Plate> platePage = new Page<>(pageNum , pageSize);
        IPage<Plate> plateIPage = plateManageMapper.selectPage(platePage , queryWrapper);
        return plateIPage;
    }

    @Override
    public Boolean judgeTokenValid(String token) {
        String userId= JWT.decode(token).getAudience().get(0);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId",userId);
        User user = userManageMapper.selectOne(queryWrapper);
        if(user!=null){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public Integer updatePlateInfo(EditPlateInfoVO editPlateInfoVO){
        QueryWrapper<Plate> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("plateId",editPlateInfoVO.getPlateId());
        Plate plate = plateManageMapper.selectOne(queryWrapper);
        plate.setPlateState(editPlateInfoVO.getPlateState());
        return plateManageMapper.updateById(plate);
    }
}
