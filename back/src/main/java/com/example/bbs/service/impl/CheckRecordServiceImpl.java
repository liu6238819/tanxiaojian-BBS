package com.example.bbs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.bbs.mapper.BbsConfigMapper;
import com.example.bbs.mapper.CheckRecordMapper;
import com.example.bbs.mapper.ContentMapper;
import com.example.bbs.pojo.BbsConfig;
import com.example.bbs.pojo.CheckRecord;
import com.example.bbs.pojo.Comment;
import com.example.bbs.pojo.Content;
import com.example.bbs.result.CommonResult;
import com.example.bbs.service.CheckRecordService;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service

public class CheckRecordServiceImpl implements CheckRecordService {

    @Autowired
    ContentMapper contentMapper;

    @Autowired
    CheckRecordMapper checkRecordMapper;
    @Autowired
    BbsConfigMapper bbsConfigMapper;

    static final OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder().build();

    @Override
    public int addCheckRecord(String userId, String contentId, int schoolId, int checkState, int breakRuleState, String breakRuleReason, String checkText) {
        CheckRecord checkRecord = new CheckRecord();
        String newUUID = UUID.randomUUID().toString().replace("-", "");
        Date nowDate = new Date();
        checkRecord.setId(newUUID);
        checkRecord.setUserId(userId);
        checkRecord.setContentId(contentId);
        checkRecord.setSchoolId(schoolId);
        checkRecord.setCheckState(checkState);
        checkRecord.setCheckText(checkText);
        checkRecord.setBreakRuleState(breakRuleState);
        checkRecord.setBreakRuleReason(breakRuleReason);
        checkRecord.setCheckTime(nowDate);
        checkRecord.setCreateTime(nowDate);
        checkRecord.setUpdateTime(nowDate);
        //在redis中插入帖子记录
        int result = checkRecordMapper.insert(checkRecord);
        return result;
    }

    @Override
    public CommonResult AICheckContent(Content content) {
        //返回结果
        com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
        //频繁发帖限制
        QueryWrapper<Content> lastContentQueryWrapper = new QueryWrapper<>();
        lastContentQueryWrapper.eq("userId", content.getUserId());
        lastContentQueryWrapper.eq("schoolId", content.getSchoolId());
        lastContentQueryWrapper.eq("contentState", 0);
        lastContentQueryWrapper.orderByDesc("createTime").last("limit 1");
        Content lastContent = contentMapper.selectOne(lastContentQueryWrapper);
        //时间比较
        if (lastContent != null) {
            Date preDate = lastContent.getCreateTime();
            long preApplyTime = preDate.getTime();
            long currentApplyTime = new Date().getTime();
            int minutes = (int) ((currentApplyTime - preApplyTime) / (1000 * 60));
            if (minutes < 5) {
                //返回。提示频繁发帖
                jsonObject.put("checkResult",0);
                jsonObject.put("frequentPosting",1);
                return CommonResult.success(jsonObject);
            }
        }
        //获取审核配置项
        QueryWrapper<BbsConfig> batchConfigQueryWrapper = new QueryWrapper<>();
        batchConfigQueryWrapper.eq("school_id", content.getSchoolId());
        batchConfigQueryWrapper.eq("config_type", "checkConfig");
        BbsConfig config = bbsConfigMapper.selectOne(batchConfigQueryWrapper);
        //json处理
        Object configJson = config.getConfigJson();
        com.alibaba.fastjson.JSONObject configJsonObject = com.alibaba.fastjson.JSONObject.parseObject(configJson.toString());
        Integer checkState = configJsonObject.getInteger("checkState");
        Integer AICheckState = configJsonObject.getInteger("AICheckState");
        Integer AIFailMode = configJsonObject.getInteger("AIFailMode");
        com.alibaba.fastjson.JSONArray AIIdList = configJsonObject.getJSONArray("AIIdList");
        //根据AIIdList长度任选一组账号
        Random random = new Random();
        int randomNumber = random.nextInt(AIIdList.size());
        //审核关闭
        if (checkState!=1 ||(checkState==1 && AICheckState!=1)){
            //返回。视作审核成功
            jsonObject.put("checkResult",0);
            return CommonResult.success(jsonObject);
        }
        else{
            String newUUID = UUID.randomUUID().toString().replace("-", "");
            String text = content.getTitle() + " " + content.getContentText();
            //结果
            String AIRes ="";
            com.alibaba.fastjson.JSONObject AIBot = (com.alibaba.fastjson.JSONObject) AIIdList.get(randomNumber);
            AIRes =getAuditResFromBotApi0603(text,AIBot.getString("token"),AIBot.getString("botId"));
            if (AIRes==null){
                jsonObject.put("checkResult",0);
                return CommonResult.success(jsonObject);
            }
            if (AIRes.substring(0, 2).equals("违规")){
                jsonObject.put("checkResult",1);
                jsonObject.put("contentId",newUUID);
                jsonObject.put("checkReason",AIRes);
                addCheckRecord(content.getUserId(), newUUID,content.getSchoolId(),0,1,AIRes,text);
            }
            else if (AIRes.substring(0, 3).equals("不违规")){
                jsonObject.put("checkResult",0);
                jsonObject.put("contentId",newUUID);
                addCheckRecord(content.getUserId(), newUUID,content.getSchoolId(),0,0,AIRes,text);
            }
            else{
                jsonObject.put("checkResult",0);
                jsonObject.put("contentId",newUUID);
                addCheckRecord(content.getUserId(), newUUID,content.getSchoolId(),1,0,AIRes,text);
            }

        }
        return CommonResult.success(jsonObject);
    }

    @Override
    public String getAuditResFromBotApi0603(String contentText ,String token, String botId) {
        String url = "https://api.coze.cn/open_api/v2/chat";
        String personalAccessToken = token;
        //给帖子内容增加双引号
        String userQuery = "“" + contentText + "“";
        String result="";

        OkHttpClient client = new OkHttpClient();

        // Create JSON payload
        //"\"conversation_id\": \"0528\"," +
        String jsonPayload = String.format(
                "{" +
                        "\"bot_id\": \"%s\"," +
                        "\"user\": \"host\"," +
                        "\"query\": \"%s\"," +
                        "\"stream\": false" +
                        "}", botId, userQuery);

        RequestBody body = RequestBody.create(jsonPayload, MediaType.get("application/json; charset=utf-8"));

        // Build the request
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Authorization", "Bearer " + personalAccessToken)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "*/*")
                .addHeader("Host", "api.coze.cn")
                .addHeader("Connection", "keep-alive")
                .build();

        // Execute the request
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            // Get response body
            if (response.body() != null) {
//                System.out.println(response.body().string());
                JSONObject jsonResponse = new JSONObject(response.body().string());
                if (jsonResponse.getInt("code")==0){
                    // 获取 messages 数组
                    JSONArray messages = jsonResponse.getJSONArray("messages");
                    // 遍历 messages 数组，找到 type 为 answer 的 content
                    for (int i = 0; i < messages.length(); i++) {
                        JSONObject message = messages.getJSONObject(i);
                        String messageType = message.getString("type");
                        if ("answer".equals(messageType)) {
                            String answerContent = message.getString("content");
                            result = answerContent;
                            System.out.println("Answer Content: " + answerContent);
                            break;  // 找到后跳出循环
                        }
                    }
                }
                //接口报错
                else{
                    return null;
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    //敏感词审核
    @Override
    public int CheckContentSensitiveWord(Content content) {
        //返回结果
        com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
        int checkResult = 0;
        //获取审核配置项
        QueryWrapper<BbsConfig> batchConfigQueryWrapper = new QueryWrapper<>();
        batchConfigQueryWrapper.eq("school_id", content.getSchoolId());
        batchConfigQueryWrapper.eq("config_type", "checkConfig");
        BbsConfig config = bbsConfigMapper.selectOne(batchConfigQueryWrapper);
        //json处理
        Object configJson = config.getConfigJson();
        com.alibaba.fastjson.JSONObject configJsonObject = com.alibaba.fastjson.JSONObject.parseObject(configJson.toString());
        Integer checkState = configJsonObject.getInteger("checkState");
        Integer sensitiveWordCheckState = configJsonObject.getInteger("sensitiveWordCheckState");
        String sensitiveWords = configJsonObject.getString("sensitiveWordList");
        com.alibaba.fastjson.JSONArray sensitiveWordList = new com.alibaba.fastjson.JSONArray();
        if (sensitiveWords != null && !sensitiveWords.trim().isEmpty()) {
            String[] wordsArray = sensitiveWords.split(",");
            for (String word : wordsArray) {
                // 过滤掉空字符串
                if (word != null && !word.trim().isEmpty()) {
                    sensitiveWordList.add(word.trim());
                }
            }
        }

        //审核关闭
        if (checkState!=1 ||(checkState==1 && sensitiveWordCheckState!=1)){
            //返回。视作审核成功
            jsonObject.put("checkResult",0);
            return checkResult;
        }
        else{
            String text = content.getTitle() + " " + content.getContentText();
            //结果
            if (sensitiveWordList == null) {
                return checkResult;
            }
            for (int i = 0; i < sensitiveWordList.size(); i++) {
                String sensitiveWord = sensitiveWordList.getString(i);
                // 过滤掉空字符串
                if (sensitiveWord == null || sensitiveWord.trim().isEmpty()) {
                    continue;
                }
                // 检查 text 是否包含敏感词
                if (text.contains(sensitiveWord)) {
                    checkResult=1;
                    break;
                }
            }
            if (checkResult==1){
                //生成审核记录（违规）
                addCheckRecord(content.getUserId(), content.getContentId(),content.getSchoolId(),0,3,"包含敏感词",text);
            }

        }
        return checkResult;
    }

    @Override
    public int CheckCommentSensitiveWord(Comment comment) {
        //返回结果
        com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
        int checkResult = 0;
        Content commentContent = contentMapper.selectById(comment.getContentId());
        //获取审核配置项
        QueryWrapper<BbsConfig> batchConfigQueryWrapper = new QueryWrapper<>();
        batchConfigQueryWrapper.eq("school_id", commentContent.getSchoolId());
        batchConfigQueryWrapper.eq("config_type", "checkConfig");
        BbsConfig config = bbsConfigMapper.selectOne(batchConfigQueryWrapper);
        //json处理
        Object configJson = config.getConfigJson();
        com.alibaba.fastjson.JSONObject configJsonObject = com.alibaba.fastjson.JSONObject.parseObject(configJson.toString());
        Integer checkState = configJsonObject.getInteger("checkState");
        Integer commentSensitiveWordCheckState = configJsonObject.getInteger("commentSensitiveWordCheckState");
        String sensitiveWords = configJsonObject.getString("sensitiveWordList");
        com.alibaba.fastjson.JSONArray sensitiveWordList = new com.alibaba.fastjson.JSONArray();
        if (sensitiveWords != null && !sensitiveWords.trim().isEmpty()) {
            String[] wordsArray = sensitiveWords.split(",");
            for (String word : wordsArray) {
                // 过滤掉空字符串
                if (word != null && !word.trim().isEmpty()) {
                    sensitiveWordList.add(word.trim());
                }
            }
        }

        //审核关闭
        if (checkState!=1 ||(checkState==1 && commentSensitiveWordCheckState!=1)){
            //返回。视作审核成功
            jsonObject.put("checkResult",0);
            return checkResult;
        }
        else{
            String text = comment.getCommentText();
            //结果
            if (sensitiveWordList == null) {
                return checkResult;
            }
            for (int i = 0; i < sensitiveWordList.size(); i++) {
                String sensitiveWord = sensitiveWordList.getString(i);
                // 过滤掉空字符串
                if (sensitiveWord == null || sensitiveWord.trim().isEmpty()) {
                    continue;
                }
                // 检查 text 是否包含敏感词
                if (text.contains(sensitiveWord)) {
                    checkResult=1;
                    break;
                }
            }
            if (checkResult==1){
                //生成审核记录（违规）
                addCheckRecord(comment.getUserId(), commentContent.getContentId(),commentContent.getSchoolId(),0,3,"评论包含敏感词",text);
            }

        }
        return checkResult;
    }

}
