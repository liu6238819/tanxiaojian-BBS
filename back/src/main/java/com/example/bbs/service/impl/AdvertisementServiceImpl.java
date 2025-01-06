package com.example.bbs.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.bbs.mapper.AdvSignUpMapper;
import com.example.bbs.mapper.AdvertisementMapper;
import com.example.bbs.mapper.BbsConfigMapper;
import com.example.bbs.mapper.UserAdvMapper;
import com.example.bbs.pojo.*;
import com.example.bbs.service.AdvertisementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

@Service
public class AdvertisementServiceImpl implements AdvertisementService {

    @Autowired
    AdvertisementMapper advertisementMapper;

    @Autowired
    BbsConfigMapper batchConfigMapper;

    @Autowired
    AdvSignUpMapper advSignUpMapper;

    @Autowired
    UserAdvMapper userAdvMapper;


    @Override
    public int createOneAdv(Advertisement advertisement){
        int result = 0;
        //创建活动
        result = advertisementMapper.insert(advertisement);
        return result;

    }

    @Override
    public List<Advertisement> getAdvList(int schoolId, String advPosition){
        List<Advertisement> advertisementList = new ArrayList<>();
        //是否按序号排列
        QueryWrapper<BbsConfig> batchConfigQueryWrapper = new QueryWrapper<>();
        batchConfigQueryWrapper
                .eq("school_id", schoolId)
                .eq("config_type", "adv");
        BbsConfig advConfig = batchConfigMapper.selectOne(batchConfigQueryWrapper);
        if (advConfig.getState()==0){
            return null;
        }
        //获取显示的广告
        QueryWrapper<Advertisement> adQueryWrapper = new QueryWrapper<>();
        adQueryWrapper
                .eq("school_id", schoolId);
        //帖子详情页开关
        if (Objects.equals(advPosition, "postDetail")){
            adQueryWrapper.eq("is_show_post_detail", 1);
        }
        //首页开关
        if (Objects.equals(advPosition, "index")){
            adQueryWrapper.eq("is_show_index", 1);
        }
        //是否需要按序号排列
        Object configJson = advConfig.getConfigJson();
        JSONObject configJsonObject = JSONObject.parseObject(configJson.toString());
        if(configJsonObject.get("indexByOrder").toString().equals("1")){
            adQueryWrapper.orderByAsc("adv_order");
        }
        advertisementList = advertisementMapper.selectList(adQueryWrapper);
        if (configJsonObject.get("indexByOrder").toString().equals("0")) {
            Collections.shuffle(advertisementList);
        }
        return advertisementList;
    }

    @Override
    public Advertisement getOneAdvById(String advId,String userId){
        QueryWrapper<Advertisement> adQueryWrapper = new QueryWrapper<>();
        adQueryWrapper
                .eq("id", advId);
        Advertisement  advertisement = advertisementMapper.selectOne(adQueryWrapper);

        //改变广告阅读量
        if (advertisement!=null){
            //增加阅读次数
            advertisement.setReadNum(advertisement.getReadNum()+1);
            //判断用户是否第一次阅读该广告
            QueryWrapper<UserAdv> userAdvQueryWrapper =new QueryWrapper<>();
            userAdvQueryWrapper
                    .eq("adv_id", advId)
                    .eq("user_id", userId);
            UserAdv userAdv = userAdvMapper.selectOne(userAdvQueryWrapper);
            //不是第一次阅读
            if (userAdv!=null){
                userAdv.setReadNum(userAdv.getReadNum()+1);
                userAdv.setUpdateTime(new Date());
                userAdvMapper.updateById(userAdv);
            }
            //是第一次阅读
            else{
                UserAdv newRecord =new UserAdv();
                newRecord.setAdvId(advId);
                newRecord.setUserId(userId);
                newRecord.setReadNum(1);
                userAdvMapper.insert(newRecord);
                advertisement.setRealReadNum(advertisement.getRealReadNum()+1);
            }
            //更新广告
            advertisement.setUpdateTime(new Date());
            advertisementMapper.updateById(advertisement);
        }
        return advertisement;
    }

    @Override
    public JSONObject createAdvSignUp(AdvSignUp advSignUp){
        JSONObject jsonObject = new JSONObject();
        QueryWrapper<AdvSignUp> advSignUpQueryWrapper = new QueryWrapper<>();
        advSignUpQueryWrapper.eq("adv_id", advSignUp.getAdvId());
        advSignUpQueryWrapper.eq("user_id", advSignUp.getUserId());
        AdvSignUp newRecord = advSignUpMapper.selectOne(advSignUpQueryWrapper);
        if (null == newRecord) {//无记录，添加新的报名信息
            newRecord = new AdvSignUp();
            newRecord.setAdvId(advSignUp.getAdvId());
            newRecord.setUserId(advSignUp.getUserId());
            String jsonString = JSON.toJSONString(advSignUp.getInfoJson());
            newRecord.setInfoJson(jsonString);
            advSignUpMapper.insert(newRecord);
        } else {//有记录，更新报名信息即可
            String jsonString = JSON.toJSONString(advSignUp.getInfoJson());
            newRecord.setInfoJson(jsonString);
            advSignUpMapper.updateById(newRecord);
        }
        newRecord.setInfoJson(null);//去除报名详请
        jsonObject.put("SignUpInfo", newRecord);
        return jsonObject;
    }

    @Override
    public JSONObject getAdvSignUp(String advId, String userId) {
        //获取报名记录
        QueryWrapper<AdvSignUp> advSignUpQueryWrapper = new QueryWrapper<>();
        advSignUpQueryWrapper.eq("adv_id", advId);
        advSignUpQueryWrapper.eq("user_id", userId);
        AdvSignUp advSignUp = advSignUpMapper.selectOne(advSignUpQueryWrapper);
        if (null == advSignUp) return null;
        //返回json对象
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", advSignUp.getId());
        jsonObject.put("advId", advSignUp.getAdvId());
        jsonObject.put("userId", advSignUp.getUserId());
        jsonObject.put("info", JSON.parseObject(advSignUp.getInfoJson().toString()));
        jsonObject.put("createTime", advSignUp.getCreateTime());
        jsonObject.put("updateTime", advSignUp.getUpdateTime());
        return jsonObject;
    }

    //获取广告列表(图片为base64)
    @Override
    public List<Advertisement> getAdvListWithImageBase64(int schoolId, String advPosition) {
        List<Advertisement> advertisementList = getAdvList(schoolId,advPosition);
        //将广告的图片转成base64
        for (Advertisement adv:advertisementList) {
            //请求首页广告，首页图片格式为base64
            if (Objects.equals(advPosition, "index")){
                String imageBase64 = getImageBase64FromUrl(adv.getImageIndex());
                adv.setImageIndex(imageBase64);
            }
            //请求详情页图片，详情页图片格式为base64
            else if (Objects.equals(advPosition, "postDetail")){
                String imageBase64 = getImageBase64FromUrl(adv.getImageDetail());
                adv.setImageDetail(imageBase64);
            }

        }
        return advertisementList;
    }

    private String getImageBase64FromUrl(String imageUrl){
        byte[] imageData = new byte[0];
        //根据图片url网络请求图片信息并转为字节组
        try {
            URL url = new URL(imageUrl);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            InputStream inputStream = url.openStream();
            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }

            imageData = byteArrayOutputStream.toByteArray();

            inputStream.close();
            byteArrayOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
            //如果请求base64的过程中出错，返回url
            return imageUrl;
        }
        // 对字节数组Base64编码
        Base64.Encoder encoder = Base64.getEncoder();
        String result ="data:image/png;base64," + encoder.encodeToString(imageData);
        return result;
    }

    @Override
    public List<Advertisement> getAdvList0507(int schoolId, int advPosition) {
        List<Advertisement> advertisementList = new ArrayList<>();
        //是否按序号排列
        QueryWrapper<BbsConfig> batchConfigQueryWrapper = new QueryWrapper<>();
        batchConfigQueryWrapper
                .eq("school_id", schoolId)
                .eq("config_type", "adv");
        BbsConfig advConfig = batchConfigMapper.selectOne(batchConfigQueryWrapper);
        if (advConfig.getState()==0){
            return null;
        }
        //获取显示的广告
        QueryWrapper<Advertisement> adQueryWrapper = new QueryWrapper<>();
        adQueryWrapper.eq("school_id", schoolId);
        adQueryWrapper.eq("adv_state", 1);
        adQueryWrapper.apply("FIND_IN_SET({0}, show_position) > 0", advPosition);
        //是否需要按序号排列
        Object configJson = advConfig.getConfigJson();
        JSONObject configJsonObject = JSONObject.parseObject(configJson.toString());
        if(configJsonObject.get("indexByOrder").toString().equals("1")){
            adQueryWrapper.orderByAsc("adv_order");
        }
        advertisementList = advertisementMapper.selectList(adQueryWrapper);
        if (configJsonObject.get("indexByOrder").toString().equals("0")) {
            Collections.shuffle(advertisementList);
        }
        return advertisementList;
    }

    @Override
    public JSONObject createMultiBusinessAdvSignUp(AdvSignUp advSignUp) {
        JSONObject jsonObject = new JSONObject();
        AdvSignUp newRecord = new AdvSignUp();
        newRecord.setAdvId(advSignUp.getAdvId());
        newRecord.setUserId(advSignUp.getUserId());
        String jsonString = JSON.toJSONString(advSignUp.getInfoJson());
        newRecord.setInfoJson(jsonString);
        String advJsonString = JSON.toJSONString(advSignUp.getAdvJson());
        newRecord.setAdvJson(advJsonString);
        int res =advSignUpMapper.insert(newRecord);
        jsonObject.put("SignUpInfo", newRecord);
        return jsonObject;
    }

}
