package com.example.bbs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.bbs.mapper.SchoolMapper;
import com.example.bbs.pojo.School;
import com.example.bbs.pojo.vo.SchoolVO;
import com.example.bbs.service.SchoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SchoolServiceImpl implements SchoolService {

    @Autowired
    SchoolMapper schoolMapper;

    @Override
    public int addSchool(SchoolVO schoolVO) {
        School school = new School(schoolVO);
        return schoolMapper.insert(school);
    }

    @Override
    public List<SchoolVO> getSchoolList(String appId) {
        if (null == appId) appId = "";
        QueryWrapper<School> wrapper = new QueryWrapper<>();
//        wrapper.eq("state",0);
        wrapper.in("state",0,2,3,6);
        List<School> schoolList = schoolMapper.selectList(wrapper);
        List<SchoolVO> schoolVOList = new ArrayList<>();
        for (int i = 0; i < schoolList.size(); i++) {
            schoolVOList.add(new SchoolVO(schoolList.get(i)));
        }
        return schoolVOList;
    }

    @Override
    public List<SchoolVO> searchSchoolList(String appId, String searchText) {
        if (null == appId) appId = "";
        QueryWrapper<School> wrapper = new QueryWrapper<>();
        wrapper
                .in("state",0,2,3,6)
                .like("schoolName",searchText);
        List<School> schoolList = schoolMapper.selectList(wrapper);
        List<SchoolVO> schoolVOList = new ArrayList<>();
        for (int i = 0; i < schoolList.size(); i++) {
            schoolVOList.add(new SchoolVO(schoolList.get(i)));
        }
        return schoolVOList;
    }

}
