package com.example.bbs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.bbs.mapper.*;
import com.example.bbs.pojo.*;
import com.example.bbs.pojo.vo.HomeCommentVO;
import com.example.bbs.pojo.vo.HomeContentVO;
import com.example.bbs.service.*;
import okhttp3.*;
import org.apache.commons.text.similarity.JaroWinklerDistance;
import org.json.JSONArray;
import org.json.JSONObject;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service

public class FuzzySearchServiceImpl implements FuzzySearchService {

    @Autowired
    ContentMapper contentMapper;

    @Autowired
    CheckRecordService checkRecordService;

    @Autowired
    CommentMapper commentMapper;

    @Autowired
    ContentKeyWordMapper contentKeyWordMapper;

    @Autowired
    CommentKeyWordMapper commentKeyWordMapper;

    @Autowired
    AISearchRecordMapper aiSearchRecordMapper;

    @Autowired
    ContentService contentService;

    @Autowired
    BbsConfigMapper bbsConfigMapper;


    @Override
    public List<HomeContentVO> getSimilarContent(String text , int schoolId) {
        //屏蔽词库
        List<String> maskWords =new ArrayList<>();
        maskWords.add("有没有");
        maskWords.add("想问");
        maskWords.add("问一下");
        //获取分词结果
        String NLPRes = new String();
        try {
            NLPRes = BdNLP(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<HomeContentVO> similarContents = new ArrayList<>();
        //将分词结果转为JsonObject
        JSONObject resText=  new JSONObject(NLPRes);
        JSONArray NLPItems = resText.getJSONArray("items");
//        System.out.println(NLPItems);
        //构建关键词列表
        List<String> keyWordList = new ArrayList();
        for (Object NLPItem : NLPItems){
            String wordPOS = new JSONObject(NLPItem.toString()).getString("pos");
            String wordNE = new JSONObject(NLPItem.toString()).getString("ne");
            String wordItem = new JSONObject(NLPItem.toString()).getString("item");
            if (wordPOS.contains("n")||wordPOS.equals("t")){
                keyWordList.add(new JSONObject(NLPItem.toString()).getString("item"));
            }
            // 两个字以上的动词
            if (wordPOS.equals("v") && wordItem.length()>=2 && !maskWords.contains(wordItem)){
                keyWordList.add(new JSONObject(NLPItem.toString()).getString("item"));
            }
            //ne:  PER 人名	LOC 地名	ORG 机构名	TIME 时间
            if (wordNE.equals("PER") ||wordNE.equals("LOC") ||wordNE.equals("ORG")||wordNE.equals("TIME")){
                keyWordList.add(new JSONObject(NLPItem.toString()).getString("item"));
            }
            //某些没有标注，但很关键
            if (wordNE.isEmpty() && wordPOS.isEmpty()){
                keyWordList.add(new JSONObject(NLPItem.toString()).getString("item"));
            }
        }
        if(keyWordList.size()==1){
            return similarContents;
        }

        //搜索时间段(按月）
        int duration = 1;
        //构建mysql查询语句
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT *, COUNT(*) as rowNumber ")
                .append("FROM (");
        for (String keyword : keyWordList) {
            queryBuilder.append("SELECT c.contentId contentId,")
                    .append("c.title  title,")
                    .append("u.userId userId,")
                    .append("u.nickName     nickName,")
                    .append("u.headimgUrl   headimgUrl,")
                    .append("u.introduction introduction,")
                    .append("c.createTime   createTime,")
                    .append("c.contentText  contentText,")
                    .append("c.contentUrl   contentUrl,")
                    .append("c.contentType  contentType,")
                    .append("c.contentState contentState,")
                    .append("c.upNum        upNum,")
                    .append("c.downNum downNum,")
                    .append("c.commentNum   commentNum,")
                    .append("c.readNum readNum,")
                    .append("c.realReadNum realReadNum,")
                    .append("c.plateId      plateId,")
                    .append("c.schoolId     schoolId,")
                    .append("c.isSpecial    isSpecial,")
                    .append("c.place        place,")
                    .append("u.userState userIdentify ")
                    .append("From bbs_content  c join bbs_user u on c.userId = u.userId ")
                    .append("WHERE (c.contentText LIKE '%").append(keyword).append("%' or c.title LIKE '%").append(keyword).append("%') ")
                    .append("and TIMESTAMPDIFF(SECOND, c.createTime, now()) > 0 ")
                    .append("and TIMESTAMPDIFF(SECOND ,date_sub(now(),interval ").append(duration).append(" month) , c.createTime) > 0 ")
                    .append("and c.schoolId = ").append(schoolId)
                    .append(" and (c.contentState = 0 or c.contentState = 1 or c.contentState = 9 or c.contentState = 10) ")
                    .append(" UNION ALL ");
        }
        // 删除最后一个UNION ALL
        queryBuilder.delete(queryBuilder.length() - "UNION ALL ".length(), queryBuilder.length());
        queryBuilder.append(") AS mergedTable ")
                .append("group by contentId ")
                .append("order by rowNumber desc,createTime desc ")
                .append("limit 0,5");
        String query = queryBuilder.toString();
        similarContents = contentMapper.getSimilarContents(query);
        return similarContents;



    }


    public Long parseTimeStrToDate(JSONObject content) {
        // 获取 AIRes 字段内容，假设它是日期时间的字符串格式
        String aiResDateStr = content.getString("createTime");

        // 设置日期解析的格式，匹配 "Fri Sep 06 19:27:57 GMT+0800 2024"
        SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);

        // 设置时区为中国标准时间 (GMT+0800)
        formatter.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));

        // 将字符串解析为 Date 对象
        Date date ;
        try {
            date = formatter.parse(aiResDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
                return null;
        }
        // 返回时间戳（毫秒级别）
        return date.getTime();

    }

    public List<JSONObject> getUsefulCommentsTest(JSONArray commentList){
        List<JSONObject> resComments = new ArrayList<>();
        for (int j = 0; j < commentList.length(); j++) {
            double score = 0;
            JSONObject commentJSONObject = new JSONObject();
            JSONObject comment = commentList.getJSONObject(j);
            String commentAIRes;
            if (comment.has("commentAIRes")) {
                commentAIRes = comment.getString("commentAIRes");
                // 继续处理 AIRes
            }
            else{
                commentAIRes=null;
            }
            if (commentAIRes!=null){
                if (commentAIRes.contains("联系方式：")){
                    score = 1;
                }
                else if (commentAIRes.equals("有价值内容")){
                    score = 0.9;
                }
            }
            commentJSONObject.put("score",score);
            commentJSONObject.put("commentText",comment.getString("commentText"));
            resComments.add(commentJSONObject);
        }
        return resComments;
    }

    private double getSecondHandsThingScore(String AIRes,String thingListStr){
        String[] thingList = thingListStr.split("/");

        JaroWinklerDistance jwd = new JaroWinklerDistance();
        // 用于保存最高相似度的变量
        double highestSimilarity = 0.0;

        // 遍历 thingList 中的每个词，并计算与 contentText 的相似度
        for (String thing : thingList) {
            double similarity = jwd.apply(thing, AIRes);
            // 如果当前的相似度大于已知最高的相似度，更新最高相似度
            if (similarity > highestSimilarity) {
                highestSimilarity = similarity;
            }
        }

        // 返回最高的相似度分数
        return highestSimilarity;
    }

    @Override
    public Map<String, Object> getSecondHandsContent(int schoolId,String text,String userId,int searchWay) {
        List<JSONObject> ResContentList =new ArrayList<>();
        List<JSONObject> ResCommentList =new ArrayList<>();
        String newUUID = UUID.randomUUID().toString().replace("-", "");
        //结果
        String AIRes ="";
        String AIBotToken ="";
        String botId = "";
        AIRes = checkRecordService.getAuditResFromBotApi0603(text.replace("\n", "").replace("\r", ""),AIBotToken,botId);
        if (AIRes!=null){
            AIRes = AIRes.replace("\"", "").replace("“", "").replace("”", "");
            String[] AIResList = AIRes.split("[,，]");
            //初步筛选帖子
            //获取关键词查询天数配置状态
            QueryWrapper<BbsConfig> bbsConfigQueryWrapper = new QueryWrapper<>();
            bbsConfigQueryWrapper
                    .eq("school_id", schoolId)
                    .eq("config_type", "functionConfig");
            BbsConfig bbsConfig = bbsConfigMapper.selectOne(bbsConfigQueryWrapper);
            //json处理
            Object configJson = bbsConfig.getConfigJson();
            com.alibaba.fastjson.JSONObject configJsonObject = com.alibaba.fastjson.JSONObject.parseObject(configJson.toString());
            com.alibaba.fastjson.JSONArray configDataList = configJsonObject.getJSONArray("data");
            int AISearchDuration =7;
            //找到配置项中的匿名发帖状态
            for (Object configData: configDataList) {
                com.alibaba.fastjson.JSONObject configDataJO = com.alibaba.fastjson.JSONObject.parseObject(configData.toString());
                if (configDataJO.get("function_name").equals("firstPage")){
                    AISearchDuration =configDataJO.getInteger("AI_search_duration");
                }
            }
            List<ContentKeyWord>  contentKeyWordList = contentKeyWordMapper.getSecondContentKeyWordList(schoolId,AISearchDuration);
            //帖子评分
            for (ContentKeyWord contentKeyWord: contentKeyWordList) {
                double score = 0;
                JSONObject contentJSONObject = new JSONObject();
                //相似度得分
                String contentText = contentKeyWord.getKeyWord();
                if (contentText!=null){
                    contentText = contentText.replace("\"", "").replace("“", "").replace("”", "");
                    String[] contentAIResList = contentText.split("[,，]");
                    if (contentAIResList.length>1) {
                        //匹配 交易物品 的相似度
                        double thingSimilarity = getSecondHandsThingScore(AIResList[1], contentAIResList[1]);
                        //如果低于阈值，直接跳过该帖子
                        if (thingSimilarity<0.8){
                            continue;
                        }
                        score =  score + thingSimilarity * 0.4;
                        //买卖得分
                        if (Objects.equals(AIResList[0], "卖") && Objects.equals(contentAIResList[0], "买")) {
                            score = score + 0.4;
                        } else if (Objects.equals(AIResList[0], "买") && Objects.equals(contentAIResList[0], "卖")) {
                            score = score + 0.4;
                        }
                        //买卖相同的话就检查评论，获取有效评论
                        else if(Objects.equals(AIResList[0], "买") && Objects.equals(contentAIResList[0], "买") && thingSimilarity >0.7) {
                            ResCommentList.addAll(getUsefulCommentJsonList(contentKeyWord.getContentId(),contentKeyWord.getUserId()));
                            continue;
                        }else if(Objects.equals(AIResList[0], "卖") && Objects.equals(contentAIResList[0], "卖") && thingSimilarity >0.7){
                            ResCommentList.addAll(getUsefulCommentJsonList(contentKeyWord.getContentId(),contentKeyWord.getUserId()));
                            continue;
                        }else{

                        }
                        //日期得分
                        Long date =contentKeyWord.getContentCreateTime().getTime();
                        Long nowTime = new Date().getTime();           // 当前时间的毫秒数
                        long daysBetween = (nowTime - date) / 86400000; // 计算时间差，单位为天
                        // 最大允许7天，超过7天的时间差将导致评分接近0
                        double timeScore = 0.2 * (1 - Math.min((double) daysBetween / 7, 1));
                        //总得分
                        score= score + timeScore;
                        contentJSONObject.put("score",score);
                        contentJSONObject.put("contentText",contentText);
                        contentJSONObject.put("contentId",contentKeyWord.getContentId());
                        updateTop5Content(ResContentList,contentJSONObject);
                    }

                }

            }
        }
        // 假设 ResCommentList 是一个 List<JSONObject>
        ResCommentList = ResCommentList.stream()
                // 首先根据 contentId 进行分组
                .collect(Collectors.groupingBy(comment -> comment.getString("contentId")))
                // 对每个 contentId 的评论列表，保留分数最高的评论
                .values().stream()
                .map(commentsForContentId -> commentsForContentId.stream()
                        .max(Comparator.comparing(comment -> comment.getDouble("score"))) // 分数最高的评论
                        .get() // 获取最高分评论
                )
                // 将结果转换为 List<JSONObject>
                .collect(Collectors.toList())
                // 然后对评论按 score 进行降序排序
                .stream()
                .sorted(Comparator.comparing(comment -> comment.getDouble("score"), Comparator.reverseOrder()))
                // 只保留前5个
                .limit(5)
                .collect(Collectors.toList());
        //获取帖子的全量信息
        List<HomeContentVO> contentList = new ArrayList<>();
        List<JSONObject> ContentJsonList = new ArrayList<>();
        for (JSONObject contentJson :ResContentList) {
            String contentId = contentJson.getString("contentId");
            //返回前端的
            HomeContentVO content = contentMapper.getContentById(contentId);
            //埋点数据库里的
            JSONObject contentJsonObject= new JSONObject();
            contentJsonObject.put("contentId",content.getContentId());
            contentJsonObject.put("title",content.getTitle());
            contentJsonObject.put("contentText",content.getContentText());
            contentJsonObject.put("createTime",content.getCreateTime());
            ContentJsonList.add(contentJsonObject);
            contentList.add(content);
        }
        //埋点数据库结果格式调整
        List<String> ContentListStrings = ContentJsonList.stream()
                .map(JSONObject::toString)
                .collect(Collectors.toList());

        //获取评论的全量信息
        List<JSONObject> commentContentList = new ArrayList<>();  //前端
        List<JSONObject> sqlCommentContentList = new ArrayList<>();  //埋点数据库
        for (JSONObject commentJson :ResCommentList) {
            String commentId = commentJson.getString("commentId");
            HomeCommentVO comment = commentMapper.getCommentVoByCommentId(commentId);
            String contentId = commentJson.getString("contentId");
            Content content = contentMapper.selectById(contentId);
            //前端结果
            JSONObject commentContent= new JSONObject();
            commentContent.put("userId",comment.getUserId());
            commentContent.put("nickName",comment.getNickName());
            commentContent.put("headImgUrl",comment.getHeadimgUrl());
            commentContent.put("contentId",content.getContentId());
            commentContent.put("title",content.getTitle());
            commentContent.put("contentText",content.getContentText());
            commentContent.put("commentId",comment.getCommentId());
            commentContent.put("commentText",comment.getCommentText());
            commentContent.put("createTime",comment.getCreateTime());
            commentContentList.add(commentContent);

            //埋点数据库结果
            JSONObject sqlCommentContent= new JSONObject();
            sqlCommentContent.put("contentId",content.getContentId());
            sqlCommentContent.put("title",content.getTitle());
            sqlCommentContent.put("contentText",content.getContentText());
            sqlCommentContent.put("commentId",comment.getCommentId());
            sqlCommentContent.put("commentText",comment.getCommentText());
            sqlCommentContent.put("createTime",comment.getCreateTime());
            sqlCommentContentList.add(sqlCommentContent);
        }
        //返回前端结果格式调整
        List<String> commentContentListStrings = commentContentList.stream()
                .map(JSONObject::toString)
                .collect(Collectors.toList());
        //埋点数据库结果格式调整
        List<String> sqlCommentContentListStrings = sqlCommentContentList.stream()
                .map(JSONObject::toString)
                .collect(Collectors.toList());

        //前端结果
        Map<String, Object> result = new HashMap<>();
        result.put("contentList", contentList);
        result.put("commentContentList", commentContentListStrings);

        //埋点数据结果
        Map<String, Object> sqlResJson = new HashMap<>();
        sqlResJson.put("contentList", ContentListStrings);
        sqlResJson.put("commentContentList", sqlCommentContentListStrings);

        //创建用户AI查询记录(埋点记录)
        AISearchRecord aiSearchRecord =new AISearchRecord();
        String AINewUUID = UUID.randomUUID().toString().replace("-", "");
        aiSearchRecord.setId(AINewUUID);
        aiSearchRecord.setUserId(userId);
        aiSearchRecord.setSchoolId(schoolId);
        aiSearchRecord.setSearchText(text);
        aiSearchRecord.setKeyWord(AIRes);
        aiSearchRecord.setSearchWay(searchWay);
        String jsonResult = new JSONObject(sqlResJson).toString();
        aiSearchRecord.setResJson(jsonResult);
        aiSearchRecordMapper.insert(aiSearchRecord);

        result.put("searchRecordId",AINewUUID);
        return result;
    }


    public static JSONArray  readFile(String fileName) {
        JSONArray jsonArray =new JSONArray();
        try {
            // 读取 JSON 文件
            File file = new File(fileName);
            Scanner scanner = new Scanner(file);
            StringBuilder jsonString = new StringBuilder();
            while (scanner.hasNextLine()) {
                jsonString.append(scanner.nextLine());
            }
            scanner.close();

            // 转换为 JSONArray 对象
            jsonArray = new JSONArray(jsonString.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    // 方法：更新ResContentList，确保其始终保留前5个得分最大的元素
    public static void updateTop5Content(List<JSONObject> ResContentList, JSONObject newContent) {
        double newScore = newContent.getDouble("score");

        // 如果列表中的元素少于5个，直接添加
        if (ResContentList.size() < 5) {
            ResContentList.add(newContent);
        } else {
            // 找到当前列表中最小得分的元素
            JSONObject minScoreContent = ResContentList.stream()
                    .min(Comparator.comparingDouble(o -> o.getDouble("score")))
                    .orElse(null);

            // 如果新元素得分大于最小得分，则替换最小得分的元素
            if (minScoreContent != null && newScore > minScoreContent.getDouble("score")) {
                ResContentList.remove(minScoreContent);
                ResContentList.add(newContent);
            }
        }

        // 按照得分从高到低排序
        ResContentList.sort((o1, o2) -> Double.compare(o2.getDouble("score"), o1.getDouble("score")));
    }

    private double getContentScore(String AIRes,String ContentText){

        JaroWinklerDistance jwd = new JaroWinklerDistance();
        double similarity = jwd.apply(AIRes, ContentText);
        return similarity;
    }

    public List<Comment> getUsefulComments(String contentId){
        List<Comment> resComments = new ArrayList<>();
        String AIBotToken ="";
        String botId ="";
        QueryWrapper<Comment> commentQueryWrapper = new QueryWrapper<>();
        commentQueryWrapper.eq("contentId",contentId);
        List<Comment> commentList = commentMapper.selectList(commentQueryWrapper);
        for (Comment comment: commentList) {
            String commentText = (comment.getCommentText()).replace("\n", "").replace("\r", "");
            String textRes = checkRecordService.getAuditResFromBotApi0603(commentText,AIBotToken,botId);
            if (textRes!=null){
                String[] textResList = textRes.split("[：:]");
                if (textResList.length>1 && !textResList[1].contains("无")){
                    resComments.add(comment);
                }
            }
        }
        return resComments;
    }

    public List<JSONObject> getUsefulCommentJsonList(String contentId, String useId){
        List<JSONObject> resComments = new ArrayList<>();
        QueryWrapper<CommentKeyWord> commentKeyWordQueryWrapper = new QueryWrapper<>();
        commentKeyWordQueryWrapper.eq("contentId",contentId);
        List<CommentKeyWord> commentKeyWordList = commentKeyWordMapper.selectList(commentKeyWordQueryWrapper);
        for (CommentKeyWord commentKeyWord: commentKeyWordList) {
            double score = 0;
            JSONObject commentJSONObject = new JSONObject();
            String commentAIRes = commentKeyWord.getKeyWord();
            if (commentAIRes!=null){
                if (commentAIRes.contains("联系方式：")){
                    score = 1;
                }
                else if (commentAIRes.equals("有价值内容")){
                    score = 0.9;
                }
            }
            if (!Objects.equals(useId, commentKeyWord.getUserId())) {
                commentJSONObject.put("score", score);
                commentJSONObject.put("commentText", commentKeyWord.getKeyWord());
                commentJSONObject.put("commentId", commentKeyWord.getCommentId());
                commentJSONObject.put("contentId", commentKeyWord.getContentId());
                resComments.add(commentJSONObject);
            }
        }
        return resComments;
    }

    public String textList ="";
    static final OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder().build();

   static String BdNLP(String text) throws IOException {
        MediaType mediaType = MediaType.parse("application/json");
       JSONObject contentJson = new JSONObject();
       contentJson.put("text",text);
        RequestBody body = RequestBody.create(mediaType, String.valueOf(contentJson));
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/rpc/2.0/nlp/v1/lexer?charset=&access_token=" + getAccessToken())
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        String resString = response.body().string();
        return resString;
    }

    /**
     * 从用户的AK，SK生成鉴权签名（Access Token）
     *
     * @return 鉴权签名（Access Token）
     * @throws IOException IO异常
     */
    public static final String API_KEY = "";    //AK
    public static final String SECRET_KEY = "";   //SK

    static String getAccessToken() throws IOException {
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "grant_type=client_credentials&client_id=" + API_KEY
                + "&client_secret=" + SECRET_KEY);
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/oauth/2.0/token")
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();

        return new JSONObject(response.body().string()).getString("access_token");
    }

}
