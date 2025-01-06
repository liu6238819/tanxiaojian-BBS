package com.example.bbs.service;

import com.example.bbs.pojo.vo.SchoolVO;
import org.springframework.stereotype.Service;

import java.util.List;


public interface SchoolService {

    public int addSchool(SchoolVO schoolVO);

    public List<SchoolVO> getSchoolList(String appId);

    public List<SchoolVO> searchSchoolList(String appId,String searchText);
}
