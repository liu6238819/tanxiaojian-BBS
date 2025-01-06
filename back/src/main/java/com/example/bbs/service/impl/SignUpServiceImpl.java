package com.example.bbs.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.bbs.mapper.SignUpActivityMapper;
import com.example.bbs.mapper.SignUpInfoMapper;
import com.example.bbs.mapper.TaskOrderMapper;
import com.example.bbs.pojo.SignUpActivity;
import com.example.bbs.pojo.SignUpInfo;
import com.example.bbs.pojo.TaskOrder;
import com.example.bbs.pojo.User;
import com.example.bbs.pojo.vo.SignUpActivityVO;
import com.example.bbs.pojo.vo.TaskOrderVO;
import com.example.bbs.service.SignUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import springfox.documentation.spring.web.json.Json;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class SignUpServiceImpl implements SignUpService {
    @Autowired
    SignUpActivityMapper signUpActivityMapper;
    @Autowired
    SignUpInfoMapper signUpInfoMapper;
    @Autowired
    TaskOrderMapper taskOrderMapper;

    @Override
    public int createSignUpActivity(SignUpActivityVO activityVO) {
        int result = 0;
        //创建活动
        SignUpActivity activity = new SignUpActivity();
        activity.setImage(activityVO.getImage());
        activity.setContent(activityVO.getContent());
        activity.setKind(activityVO.getKind());
        activity.setState(activityVO.getState());
        activity.setSchoolId(activityVO.getSchoolId());
        activity.setOwnerId(activityVO.getOwnerId());
        activity.setAlumniOnly(activityVO.getAlumniOnly());
        activity.setLink(activityVO.getLink());
        activity.setDeposit(activityVO.getDeposit());
        activity.setQuota(activityVO.getQuota());
        activity.setDeadline(activityVO.getDeadline());
        result = signUpActivityMapper.insert(activity);
        if (result <= 0) return result;
        //创建模板信息
        SignUpInfo signUpInfo = new SignUpInfo();
        signUpInfo.setActivityId(activity.getId());
        signUpInfo.setUserId("offical");
        String jsonString = JSON.toJSONString(activityVO.getInfoJson());
        signUpInfo.setInfoJson(jsonString);
        result = signUpInfoMapper.insert(signUpInfo);

        return result;
    }

    @Override
    public JSONObject getSignUpInfo(String activityId, String userId, String batchNumber) {
        //获取报名记录
        QueryWrapper<SignUpInfo> signUpInfoQueryWrapper = new QueryWrapper<>();
        signUpInfoQueryWrapper.eq("activity_id", activityId);
        signUpInfoQueryWrapper.eq("user_id", userId);
        //如果不是获取官方报名模板，需要核对批次信息
        if (!Objects.equals(userId, "offical")){
            signUpInfoQueryWrapper.eq("batch_number", batchNumber);
        }
        SignUpInfo signUpInfo = signUpInfoMapper.selectOne(signUpInfoQueryWrapper);
        if (null == signUpInfo) return null;
        //获取活动信息
        QueryWrapper<SignUpActivity> signUpActivityQueryWrapper = new QueryWrapper<>();
        signUpActivityQueryWrapper.eq("id", signUpInfo.getActivityId());
        SignUpActivity activity = signUpActivityMapper.selectOne(signUpActivityQueryWrapper);
        if (null == activity) {
            return null;
        }
        //返回json对象
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", signUpInfo.getId());
        jsonObject.put("activityId", signUpInfo.getActivityId());
        jsonObject.put("userId", signUpInfo.getUserId());
        jsonObject.put("batchNumber", signUpInfo.getBatchNumber());
        jsonObject.put("state", signUpInfo.getState());
        jsonObject.put("info", JSON.parseObject(signUpInfo.getInfoJson().toString()));
        jsonObject.put("createTime", signUpInfo.getCreateTime());
        jsonObject.put("updateTime", signUpInfo.getUpdateTime());
        jsonObject.put("content", activity.getContent());
        jsonObject.put("image", activity.getImage());
        return jsonObject;
    }

    @Override
    public List<SignUpInfo> getSignUpList(String activityId, String batchNumber) {
        List<SignUpInfo> signUpInfoList = null;
        QueryWrapper<SignUpInfo> signUpInfoQueryWrapper = new QueryWrapper<>();
        signUpInfoQueryWrapper.eq("activity_id", activityId);
        signUpInfoQueryWrapper.eq("batch_number", batchNumber);
        signUpInfoQueryWrapper.eq("state", 0);
        signUpInfoList = signUpInfoMapper.selectList(signUpInfoQueryWrapper);
        return signUpInfoList;
    }

    @Override
    public List<SignUpActivityVO> getActivityList(String ownerId,int kind, int state, int schoolId, int alumniOnly, int pageNum, int pageSize) {
        List<SignUpActivityVO> signUpActivityList = null;
        //报名sql目前写在了订单中
        signUpActivityList = taskOrderMapper.selectActivityList(ownerId,kind, state, schoolId, alumniOnly, (pageNum - 1) * pageSize, pageSize);
        for (int i = 0; i < signUpActivityList.size(); i++) {
            //获取每个活动的已报名人数
            QueryWrapper<SignUpInfo> signUpInfoQueryWrapper = new QueryWrapper<>();
            signUpInfoQueryWrapper.eq("activity_id", signUpActivityList.get(i).getId());
            signUpInfoQueryWrapper.eq("state", 0);
            int signNum = signUpInfoMapper.selectList(signUpInfoQueryWrapper).size() - 1;
            signUpActivityList.get(i).setSignUpNum(signNum);
            //获取每个活动的模板json
            QueryWrapper<SignUpInfo> signUpInfoQueryWrapper1 = new QueryWrapper<>();
            signUpInfoQueryWrapper1.eq("activity_id", signUpActivityList.get(i).getId());
            signUpInfoQueryWrapper1.eq("user_id", "offical");
            SignUpInfo offical = signUpInfoMapper.selectOne(signUpInfoQueryWrapper1);
            String jsonString = JSON.toJSONString(offical.getInfoJson());
            signUpActivityList.get(i).setInfoJson(jsonString);
        }
        return signUpActivityList;
    }

    @Override
    public JSONObject addSignUpInfo(SignUpInfo signUpInfo) {
        JSONObject jsonObject = new JSONObject();
        int result = 1;
        //获取活动
        QueryWrapper<SignUpActivity> signUpActivityQueryWrapper = new QueryWrapper<>();
        signUpActivityQueryWrapper.eq("id", signUpInfo.getActivityId());
        SignUpActivity activity = signUpActivityMapper.selectOne(signUpActivityQueryWrapper);
        if (null == activity) {
            result = 2;
            jsonObject.put("result", result);
            return jsonObject; //活动不存在
        }
        //判断报名人数是否超过限制
        QueryWrapper<SignUpInfo> signUpInfoQueryWrapper = new QueryWrapper<>();
        signUpInfoQueryWrapper.eq("activity_id", signUpInfo.getActivityId());
        List<SignUpInfo> signUpInfoList = signUpInfoMapper.selectList(signUpInfoQueryWrapper);
        if (signUpInfoList.size() >= (activity.getQuota() + 1)) {//过滤官方模板
            result = 3;
            jsonObject.put("result", result);
            return jsonObject; //报名人数已满
        }
        //判断本批次是否已报过名
        signUpInfoQueryWrapper.eq("user_id", signUpInfo.getUserId());
        signUpInfoQueryWrapper.eq("batch_number", signUpInfo.getBatchNumber());
        SignUpInfo newRecord = signUpInfoMapper.selectOne(signUpInfoQueryWrapper);
        if (null == newRecord) {//无记录，添加新的报名信息
            newRecord = new SignUpInfo();
            newRecord.setActivityId(signUpInfo.getActivityId());
            newRecord.setUserId(signUpInfo.getUserId());
            newRecord.setBatchNumber(signUpInfo.getBatchNumber());
            String jsonString = JSON.toJSONString(signUpInfo.getInfoJson());
            newRecord.setInfoJson(jsonString);
            //根据金额确定报名状态
            int isZero = activity.getDeposit().compareTo(BigDecimal.ZERO);
            if (isZero == 1) { //需付报名费，状态为未支付
                newRecord.setState(-1);
            } else { ////无需付报名费，状态为正常
                newRecord.setState(0);
            }
            signUpInfoMapper.insert(newRecord);
        } else {//有记录，更新报名信息即可
            String jsonString = JSON.toJSONString(signUpInfo.getInfoJson());
            newRecord.setInfoJson(jsonString);
            signUpInfoMapper.updateById(newRecord);
            //10.24改：先删除再新建一条报名记录，确保id刷新
//            signUpInfoMapper.deleteById(newRecord.getId());
//            newRecord = new SignUpInfo();
//            newRecord.setActivityId(signUpInfo.getActivityId());
//            newRecord.setUserId(signUpInfo.getUserId());
//            String jsonString = JSON.toJSONString(signUpInfo.getInfoJson());
//            newRecord.setInfoJson(jsonString);
            //根据金额确定报名状态
//            int isZero = activity.getDeposit().compareTo(BigDecimal.ZERO);
//            if (isZero == 1) { //需付报名费，状态为未支付
//                newRecord.setState(-1);
//            } else { ////无需付报名费，状态为正常
//                newRecord.setState(0);
//            }
//            signUpInfoMapper.insert(newRecord);
        }
        newRecord.setInfoJson(null);//去除报名详情
        jsonObject.put("result", result);
        jsonObject.put("SignUpInfo", newRecord);
        return jsonObject;

    }

    @Override
    public void payedSignUp(SignUpInfo signUpInfo) {
        //获取报名信息
        QueryWrapper<SignUpInfo> signUpInfoQueryWrapper = new QueryWrapper<>();
        signUpInfoQueryWrapper.eq("id", signUpInfo.getId());
        SignUpInfo record = signUpInfoMapper.selectOne(signUpInfoQueryWrapper);
        //修改订单状态
        record.setState(0);
        signUpInfoMapper.updateById(record);
    }
}
