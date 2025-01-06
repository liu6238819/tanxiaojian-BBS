package com.example.bbs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.bbs.mapper.CateMapper;
import com.example.bbs.mapper.PlateMapper;
import com.example.bbs.mapper.UserSchoolMapper;
import com.example.bbs.mapper.User_PlateMapper;
import com.example.bbs.pojo.Cate;
import com.example.bbs.pojo.Plate;
import com.example.bbs.pojo.UserSchool;
import com.example.bbs.pojo.User_Plate;
import com.example.bbs.service.PlateService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@EnableCaching
public class PlateServiceImpl implements PlateService {

    @Autowired
    PlateMapper plateMapper;
    @Autowired
    CateMapper cateMapper;
    @Autowired
    User_PlateMapper user_plateMapper;
    @Autowired
    UserSchoolMapper userSchoolMapper;


    @Override
    public Plate createPlate(Plate plate) {
        //数量检验，非管理员只能创建3个板块
        QueryWrapper<UserSchool> userSchoolQueryWrapper =new QueryWrapper<>();
        userSchoolQueryWrapper.eq("schoolId",plate.getSchoolId());
        userSchoolQueryWrapper.eq("userId",plate.getUserId());
        userSchoolQueryWrapper.orderByAsc("createTime");
//        UserSchool userSchool = userSchoolMapper.selectOne(userSchoolQueryWrapper);
        //修改selectOne,防止脏数据报错
        UserSchool userSchool=new UserSchool();
        List <UserSchool> userSchoolList = userSchoolMapper.selectList(userSchoolQueryWrapper);
        if (userSchoolList.size()>0){
            userSchool= userSchoolList.get(0);
        }
        if (userSchool.getUserType()!=3){//非管理员需要限制条件,在一个学校下最多创建30个板块
            QueryWrapper<Plate> plateQueryWrapper = new QueryWrapper<>();
            plateQueryWrapper.eq("userId", plate.getUserId());
            plateQueryWrapper.eq("schoolId",plate.getSchoolId());
            plateQueryWrapper.eq("plateState", 0);
            List<Plate> plateList=plateMapper.selectList(plateQueryWrapper);
            if (plateList.size()>=30)
            {
                //创建数量超出限制
                Plate fail = new Plate();
                fail.setPlateId(null);
                fail.setName("个人最多只能创建30个板块！");
                return fail;
            }
        }
        //开始创建板块
        QueryWrapper<Plate>  plateQueryWrapper = new QueryWrapper<>();
        plateQueryWrapper.eq("name", plate.getName());
        plateQueryWrapper.eq("schoolId",plate.getSchoolId());
        List<Plate> list = plateMapper.selectList(plateQueryWrapper);
        if (list.size() == 0) {
            plate.setName(plate.getName());
            plate.setIntroduction(plate.getIntroduction());
            plateMapper.insert(plate);
            Plate one = plateMapper.selectOne(plateQueryWrapper);
            User_Plate user_plate = new User_Plate(one.getUserId(), one.getPlateId());
            user_plateMapper.insert(user_plate);
            return one; //创建成功
        } else {
            Plate fail = new Plate();
            fail.setPlateId(list.get(0).getPlateId());
            fail.setName("已存在同名话题");
            return fail;//已存在同名板块
        }

    }

    @Override
    public List<Cate> getClassLists() {
        QueryWrapper<Cate> cateQueryWrapper = new QueryWrapper<>();
        return cateMapper.selectList(cateQueryWrapper);
    }

    @Override
    public List<Plate> getPlateLists(Integer schoolId, Integer cateId) {
        QueryWrapper<Plate> plateQueryWrapper = new QueryWrapper<>();
        if (0 == cateId) {
            plateQueryWrapper
                    .eq("schoolId", schoolId)
                    .eq("plateState", 0)
                    .ne("cateId", 3)
            ;
        } else if (3 == cateId) {
            plateQueryWrapper
                    .eq("schoolId", schoolId)
                    .eq("cateId", cateId)
                    .eq("plateState", 0)
                    .gt("userNum", 10)
            ;
        } else {
            plateQueryWrapper
                    .eq("schoolId", schoolId)
                    .eq("plateState", 0)
                    .eq("cateId", cateId)
            ;
        }
        plateQueryWrapper.eq("plateState",0);

        return plateMapper.selectList(plateQueryWrapper);
    }

    @Override
    public Plate getPlateInfo(String plateId) {
        return plateMapper.selectById(plateId);
    }

    @Override
    public boolean joinPlate(User_Plate user_plate) {
        if (StringUtils.isEmpty(user_plate.getUserId())) {
            return false;
        }
        user_plateMapper.insert(user_plate);
        QueryWrapper<Plate> plateQueryWrapper = new QueryWrapper<>();
        plateQueryWrapper.eq("plateId", user_plate.getPlateId());
        Plate plate = plateMapper.selectOne(plateQueryWrapper);
        plate.setUserNum(plate.getUserNum() + 1);
        plateMapper.update(plate, plateQueryWrapper);
        return true;
    }

    @Override
    public boolean leavePlate(User_Plate user_plate) {
        QueryWrapper<User_Plate> plateQueryWrapper = new QueryWrapper<>();
        plateQueryWrapper.eq("userId", user_plate.getUserId())
                .eq("plateId", user_plate.getPlateId());
        user_plateMapper.delete(plateQueryWrapper);

        QueryWrapper<Plate> plateQueryWrapper1 = new QueryWrapper<>();
        plateQueryWrapper1.eq("plateId", user_plate.getPlateId());
        Plate plate = plateMapper.selectOne(plateQueryWrapper1);
        plate.setUserNum(plate.getUserNum() - 1);
        plateMapper.update(plate, plateQueryWrapper1);
        return true;
    }

    @Override
    public boolean deletePlate(User_Plate user_plate) {
        QueryWrapper<Plate> plateQueryWrapper1 = new QueryWrapper<>();
        plateQueryWrapper1.eq("plateId", user_plate.getPlateId());
        plateQueryWrapper1.eq("userId", user_plate.getUserId());
        Plate plate = plateMapper.selectOne(plateQueryWrapper1);
        if (null==plate) return false;//没有权限解散
        plate.setPlateState(2);
        plateMapper.update(plate, plateQueryWrapper1);
        return true;
    }

    @Override
    public boolean isJoin(String plateId, String userId) {
        QueryWrapper<User_Plate> plateQueryWrapper = new QueryWrapper<>();
        QueryWrapper<User_Plate> plateId1 = plateQueryWrapper.eq("plateId", plateId);
        List<User_Plate> Join_Users = user_plateMapper.selectList(plateId1);
        if (null == Join_Users) return false;
        for (int i = 0; i < Join_Users.size(); i++) {
            if (null == Join_Users.get(i).getUserId()) {
                continue;
            }
            if (Join_Users.get(i).getUserId().equals(userId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Plate> getFocusedPlates(String userId,Integer schoolId) {
        QueryWrapper<User_Plate> User_PlateQueryWrapper = new QueryWrapper<>();
        QueryWrapper<User_Plate> plates = User_PlateQueryWrapper.eq("userId", userId);
        List<User_Plate> records = user_plateMapper.selectList(plates);
        if (records.size() == 0) return null;
        QueryWrapper<Plate> plateQueryWrapper = new QueryWrapper<>();
        for (int i = 0; i < records.size(); i++) {
            plateQueryWrapper.eq("plateId", records.get(i).getPlateId());
            if(i!= records.size()-1) plateQueryWrapper.or();
        }
        List<Plate> all = plateMapper.selectList(plateQueryWrapper);
        //获取当前校园下的关注板块，并筛选状态
        List<Plate> result = new ArrayList<Plate>();
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getSchoolId()==schoolId&&all.get(i).getPlateState()==0){
               result.add(all.get(i));
            }
        }
        return result;
    }
}
