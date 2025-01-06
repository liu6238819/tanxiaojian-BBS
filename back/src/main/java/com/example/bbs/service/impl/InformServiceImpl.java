package com.example.bbs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.bbs.mapper.InformMapper;
import com.example.bbs.pojo.Inform;
import com.example.bbs.service.InformService;
import com.example.bbs.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class InformServiceImpl implements InformService {

    @Autowired
    InformMapper informMapper;

    @Override
    public void addInformInfo(String userId, String targetId, int isContent, String informInfo) {
        Inform inform = new Inform();
        inform.setUserId(userId);
        inform.setTargetId(targetId);
        inform.setIsContent(isContent);
        inform.setInformInfo(informInfo);
        informMapper.insert(inform);
    }

    @Override
    public void addInformInfoMoreInfo(String userId,String informedUserId,String contentId,String targetText, String targetId, int isContent,String informInfo, String informUrl,int schoolId) {
        Inform inform = new Inform();
        inform.setUserId(userId);
        inform.setInformedUserId(informedUserId);
        inform.setContentId(contentId);
        inform.setTargetText(targetText);
        inform.setTargetId(targetId);
        inform.setIsContent(isContent);
        inform.setInformInfo(informInfo);
        inform.setInformUrl(informUrl);
        inform.setSchoolId(schoolId);
        informMapper.insert(inform);
    }

    @Override
    public int ifAlreadyInformThisTarget(String userId,int schoolId, String targetId) {
        int AlreadyInform =0;
        QueryWrapper <Inform> informQueryWrapper =new QueryWrapper<>();
        informQueryWrapper.eq("userId",userId)
                .eq("targetId",targetId)
                .eq("schoolId",schoolId);
        List<Inform> informList = informMapper.selectList(informQueryWrapper);
        //如果已经举报过
        if (informList != null && informList.size() != 0) {
            AlreadyInform = 1;
        }
        return AlreadyInform;

    }

}
