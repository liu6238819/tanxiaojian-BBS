package com.example.bbs.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.bbs.mapper.*;
import com.example.bbs.pojo.*;
import com.example.bbs.pojo.vo.HomeContentVO;
import com.example.bbs.service.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.example.bbs.util.DateUtil.initToday;
import static com.example.bbs.util.DateUtil.initYesterday;

@Service
@EnableCaching
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;
    @Autowired
    ContentService contentService;
    @Autowired
    FocusService focusService;
    @Autowired
    UserService userService;
    @Autowired
    FocusMapper focusMapper;

    @Autowired
    AdmireMapper admireMapper;

    @Autowired
    ContentMapper contentMapper;

    @Autowired
    CommentMapper commentMapper;

    @Autowired
    UserSchoolMapper userSchoolMapper;

    @Autowired
    SignMapper signMapper;


    @Autowired
    OperationRecordMapper operationRecordMapper;

    @Autowired
    OperationRecordService operationRecordService;

    @Autowired
    UserSchoolService userSchoolService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.datasource.url}")
    private String JDBCDatabaseUrl;

    private String databaseUrl;

    @PostConstruct
    public void databaseUrlInit() {
        databaseUrl = processDatabaseUrl();
    }

    //提取数据库名称
    private String processDatabaseUrl() {
        int index = JDBCDatabaseUrl.indexOf("bbs_");
        String processedUrl="";
        if (index != -1) {
            // 打印位置索引和提取的子字符串
            String dbName = JDBCDatabaseUrl.substring(index);
            String[] parts = dbName.split("\\?");
            processedUrl = parts[0];
        } else {
            System.out.println("未找到字符串'bbs_'");
        }
        return processedUrl;
    }

    @Autowired
    private RedisTemplate<String, Object> redisTemplateJsonStr;

    @Override
    public boolean userRegister(User user, String ipAddr) {
        //判断当前手机号是否已经注册过
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("phone", user.getPhone());
        List<User> list = userMapper.selectList(userQueryWrapper);
        if (list.size() == 0) {
            user.setUserType(1);//初始化为微信用户
            user.setLastIp(ipAddr);
            userMapper.insert(user);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public User userLogin(String phone, String password) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("phone", phone).eq("password", password);
        User user = userMapper.selectOne(userQueryWrapper);
        return user;
    }

    @Override
    public User userLoginWX(String openId) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("openId", openId);
        User user = userMapper.selectOne(userQueryWrapper);
        return user;
    }


    //前端管理用户状态和用户类型
    @Override
    public int manageUserInfo(UserSchool userSchool) {
        //先使用redis获取用户信息
        UserSchool old = new UserSchool();
        List<UserSchool> userSchoolList =new ArrayList<>();
        //数据库获取原始用户信息
        QueryWrapper<UserSchool> userSchoolQueryWrapper = new QueryWrapper<>();
        userSchoolQueryWrapper.eq("userId", userSchool.getUserId())
                .eq("schoolId",userSchool.getSchoolId())
                .orderByAsc("createTime");
        userSchoolList = userSchoolMapper.selectList(userSchoolQueryWrapper);
        old = userSchoolList.get(0);
        //前端用户管理
        if (userSchool.getUserState()!= old.getUserState()) {
            old.setUserState(userSchool.getUserState());
        }
        if (userSchool.getUserType()!= old.getUserType()){
            old.setUserType(userSchool.getUserType());
        }
        int result = userSchoolMapper.updateById(old);
        return result;
    }

    //用户注销
    @Override
    public int userLogOut(String userId, String headImgUrl, String nickName, int schoolId) {
        if (StringUtils.isBlank(userId)){
            return 0;
        }
        //获取用户原本的用户信息
        User oldUser = userMapper.selectById(userId);

        //检测180天内是否注销过账户
        // 计算180天前的日期
        LocalDateTime dateTime180DaysAgo = LocalDateTime.now().minusDays(180);
        String formattedDateTime = dateTime180DaysAgo.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        //注销操作记录
        List<OperationRecord> operationRecordList =new ArrayList<>();
        QueryWrapper<OperationRecord> operationRecordQueryWrapper = new QueryWrapper<>();
        operationRecordQueryWrapper
                .eq("target_kind", 1)
                .like("note", "用户注销账号")
                .orderByDesc("operate_time")
                .apply("JSON_EXTRACT(change_info, '$.data.oldForm.openId') = {0}", oldUser.getOpenId())
                .ge("operate_time", formattedDateTime); // 增加180天内的时间条件
        operationRecordList = operationRecordMapper.selectList(operationRecordQueryWrapper);
        if (operationRecordList.size()>0){
            return 2;
        }

        //用户原本的user_school信息
        QueryWrapper<UserSchool> userSchoolQueryWrapper = new QueryWrapper<>();
        userSchoolQueryWrapper.eq("userId", userId)
                .orderByAsc("createTime");
        List<UserSchool> oldUserSchool = userSchoolMapper.selectList(userSchoolQueryWrapper);

        //删除用户的历史发帖和评论
        QueryWrapper<Content> contentQueryWrapper = new QueryWrapper<>();
        contentQueryWrapper.eq("userId",userId);
        List<Content> oldContentList = contentMapper.selectList(contentQueryWrapper);
        //数据库帖子下架
        contentMapper.logOutContentByUserId(userId);

        //数据库评论下架
        commentMapper.logOutCommentByUserId(userId);

        //将user的unionId和openId储存起来
        String unionId = oldUser.getUnionId();
        String openId = oldUser.getOpenId();
        //将旧的unionId和openId改为logOut（注销）
        oldUser.setUnionId("logOut");
        oldUser.setOpenId("logOut");
        //更新旧用户信息记录的unionId和openId
        userMapper.updateById(oldUser);
        //构建新的用户信息记录
        String newUUID = UUID.randomUUID().toString().replace("-", "");
        oldUser.setUserId(newUUID);
        oldUser.setHeadimgUrl(headImgUrl);
        oldUser.setNickName( nickName + newUUID.substring(0, 5));
        oldUser.setIntroduction("");
        oldUser.setOpenId(openId);
        oldUser.setUnionId(unionId);
        oldUser.setScores(0);
        //插入新的用户信息记录
        userMapper.insert(oldUser);
        //复制插入对应userSchool信息记录
        for ( UserSchool userSchool:oldUserSchool) {
            userSchool.setUserId(newUUID);
            String userSchoolNewUUID = UUID.randomUUID().toString().replace("-", "");
            userSchool.setId(userSchoolNewUUID);
            userSchoolMapper.insert(userSchool);
            //复制每个学校的禁言记录（前5条）
            List<OperationRecord> BannedList =operationRecordService.selectBannedList(userSchool.getSchoolId(),userId);
            if (BannedList.size()>0){
                for ( OperationRecord bannedRecord : BannedList) {
                    String userBannedNewUUID = UUID.randomUUID().toString().replace("-", "");
                    bannedRecord.setId(userBannedNewUUID);
                    bannedRecord.setTargetId(newUUID);
                    operationRecordMapper.insert(bannedRecord);
                }
            }
        }
        //插入操作记录
        OperationRecord operationRecord =new OperationRecord();
        operationRecord.setSchoolId(schoolId);
        operationRecord.setRecordState(1);
        operationRecord.setOperator(userId);
        operationRecord.setTargetKind(1);
        operationRecord.setTargetId(userId);
        operationRecord.setNote("用户注销账号");
        String changeInfo = "{\"data\":{\"oldForm\":{\"openId\":\"" + openId + "\",\"unionId\":\"" + unionId+ "\"},\"newForm\":{\"openId\":\"logOut\",\"unionId\":\"logOut\"}}}";
        operationRecord.setChangeInfo(changeInfo);
        operationRecordService.addOperationRecord(operationRecord);

        return 1;
    }

    @Override
    public int editUserInfos(User user) {
        //清空redis
        String redisKey = "getUserById:" + user.getUserId();
        try {
            redisTemplate.delete(redisKey);
        } catch (Exception e) {
            System.out.println("redisError: " + e.getMessage());
            e.printStackTrace();
        }
        if (null != user.getNickName()) {
            user.setNickName(user.getNickName());
        }
        if ("".equals(user.getNickName())) return 0;
        return userMapper.updateById(user);
    }

    @Override
    public int editUserInfoFront(User user) {
        if (null != user.getNickName()) {
            user.setNickName(user.getNickName());
        }
        if ("".equals(user.getNickName())) return 0;
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("userId", user.getUserId());
        User old = userMapper.selectOne(userQueryWrapper);
        Map<String, String> updateInfoMap = new HashMap<>();
        //前端只能修改昵称、手机、头像和简介
        if (null != user.getNickName()) {
            old.setNickName(user.getNickName());
            updateInfoMap.put("nickName",user.getNickName());
        }
        if (null != user.getHeadimgUrl()){
            old.setHeadimgUrl(user.getHeadimgUrl());
            updateInfoMap.put("headimgUrl",user.getHeadimgUrl());
        }
        if (null != user.getIntroduction()){
            old.setIntroduction(user.getIntroduction());
            updateInfoMap.put("introduction",user.getIntroduction());
        }
        if (null != user.getPhone()){
            old.setPhone(user.getPhone());
            updateInfoMap.put("phone",user.getPhone());
        }
        int result = userMapper.updateById(old);
        return result;
    }

    @Override
    public User getUserByConOrComId(String contentId, int isContent) {
        Content content = contentService.getContent(contentId);
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("userId", content.getUserId());
        return userMapper.selectOne(userQueryWrapper);
    }

    @Override
    public void updateUserType(String userId, int type) {
        User user = getUserByUserId(userId);
        if (user.getUserType() != type) {
            user.setUserState(type);
            userMapper.updateById(user);
        }
    }

    @Override
    public User getUserByUserId(String userId) {
        //数据库获取原始用户信息
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("userId", userId);
        User user = userMapper.selectOne(userQueryWrapper);
        return user;
    }

    @Override
    public Integer FollowRecordUpdate(String userId, String targetId, Integer isFollow) {
        if (!userId.equals(targetId)) {
            focusService.refreshFollowRecord(userId, targetId, isFollow);
            return 1;
        } else {
            System.out.println("用户自己不能关注自己");
            return 0;
        }

    }

    @Override
    public JSONObject getUserInfoById(String userId, String searchId, Integer schoolId) {
        //获取关注/粉丝数
        QueryWrapper<Focus> getFans = new QueryWrapper<>();
        QueryWrapper<Focus> getFocus = new QueryWrapper<>();
        getFans.eq("targetId", searchId);
        getFocus.eq("userId", searchId);
        List<Focus> fansList = focusMapper.selectList(getFans);
        Integer fansCount = fansList.size();
        List<Focus> focusList = focusMapper.selectList(getFocus);
        Integer focusCount = focusList.size();

        User user =getUserByUserId(searchId);
        String intro = user.getIntroduction();
        if (null == intro) intro = "暂无介绍";
        int currentUserState = userService.getUserStateBySchool(user.getUserId(),schoolId).getInteger("userState");
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObject1 = new JSONObject();
        jsonObject.put("userId", user.getUserId());
        jsonObject.put("openId", user.getOpenId());
        jsonObject.put("sex", user.getSex());
        jsonObject.put("stuNumber", user.getStuNum());
        jsonObject.put("city", user.getCity());
        jsonObject.put("nickName", user.getNickName());
        jsonObject.put("headimgUrl", user.getHeadimgUrl());
        jsonObject.put("userState", currentUserState);
        jsonObject.put("introduction", intro);
        jsonObject.put("fansCount", fansCount);
        jsonObject.put("focusCount", focusCount);
        jsonObject.put("userIdentify", user.getUserState());
        if (!userId.equals(searchId)) {
            QueryWrapper<Focus> focusQueryWrapper = new QueryWrapper<>();
            focusQueryWrapper.eq("userId", userId);
            focusQueryWrapper.eq("targetId", searchId);
            Focus focus = focusMapper.selectOne(focusQueryWrapper);
            if (focus != null) {
                jsonObject.put("isFollowed", focus.getState());
            } else {
                jsonObject.put("isFollowed", 0);
            }
        } else {
            jsonObject.put("isFollowed", -1);
        }
        jsonObject1.put("userInfo", jsonObject);
        return jsonObject1;
    }

    @Override
    public JSONObject getFollowedUsers(String userId, Integer type, int pageNum, int pageSize) {
        QueryWrapper<Focus> focusQueryWrapper = new QueryWrapper<>();
        if (type.equals(0)) {
            focusQueryWrapper.eq("targetId", userId);
        } else {
            focusQueryWrapper.eq("userId", userId)
                    .eq("state", 1);
        }
        //获取全部粉丝或关注数量
        List<Focus> count = focusMapper.selectList(focusQueryWrapper);
        Integer total = count.size();
        //获取全部帖子数量
        QueryWrapper<Content> contentQueryWrapper = new QueryWrapper<>();
        contentQueryWrapper.eq("userId", userId);
        List<Content> cc = contentMapper.selectList(contentQueryWrapper);
        Integer contentNum = cc.size();
        //分页获取粉丝详情
        Page<Focus> focusPage = new Page<>(pageNum, pageSize);
        IPage<Focus> focusIPage = focusMapper.selectPage(focusPage, focusQueryWrapper);
        List<Focus> focusInfo = focusIPage.getRecords();
        JSONArray jsonArray = new JSONArray();
        QueryWrapper<User> userQueryWrapper = null;
        for (Integer i = 0; i < focusInfo.size(); i++) {
            JSONObject jsonObject = new JSONObject();
            userQueryWrapper = new QueryWrapper<>();
            if (type.equals(0)) {
                String followedId = focusInfo.get(i).getUserId();
                userQueryWrapper.eq("userId", followedId);
            } else {
                String targetId = focusInfo.get(i).getTargetId();
                userQueryWrapper.eq("userId", targetId);
            }
            User user = userMapper.selectOne(userQueryWrapper);
            if (null == user) { //脏数据处理
                total = total - 1;
                continue;
            }
            String intro = user.getIntroduction();
            if (null == intro) intro = "暂无介绍";
            jsonObject.put("userId", user.getUserId());
            jsonObject.put("sex", user.getSex());
            jsonObject.put("stuNumber", user.getStuNum());
            jsonObject.put("city", user.getCity());
            jsonObject.put("nickName", user.getNickName());
            jsonObject.put("headimgUrl", user.getHeadimgUrl());
            jsonObject.put("introduction", intro);
            jsonArray.add(jsonObject);
        }
        JSONObject userObj = new JSONObject();
        userObj.put("records", jsonArray);
        userObj.put("total", total);
        userObj.put("contentNum", contentNum);
        userObj.put("current", focusIPage.getCurrent());
        userObj.put("pages", focusIPage.getPages());
        userObj.put("size", focusIPage.getSize());
        return userObj;
    }

    @Override
    public JSONObject getLikedContents(String userId, int pageNum, int pageSize, Integer schoolId) {
        QueryWrapper<Admire> admireQueryWrapper = new QueryWrapper<>();
        admireQueryWrapper.eq("userId", userId);
        admireQueryWrapper.eq("isLike", 1);
        admireQueryWrapper.eq("isContent", 1);
        admireQueryWrapper.orderByDesc("createTime");
        Page<Admire> admirePage = new Page<>(pageNum, pageSize);
        IPage<Admire> admireIPage = admireMapper.selectPage(admirePage, admireQueryWrapper);
        List<Admire> admireInfo = admireIPage.getRecords();
        JSONArray jsonArray = new JSONArray();
        for (Integer i = 0; i < admireInfo.size(); i++) {
            JSONObject jsonObject = new JSONObject();
            String targetId = admireInfo.get(i).getTargetId();
            QueryWrapper<Content> contentQueryWrapper = new QueryWrapper<>();
            contentQueryWrapper.eq("contentId", targetId);
            Content content = contentMapper.selectOne(contentQueryWrapper);
            if (null == content) continue;
            if (content.getSchoolId() != schoolId && content.getContentState() != 8) continue; //按校区约束结果，官方贴不过滤
            //过滤删除贴
            if (content.getContentState()==7) continue;
            //过滤私密贴和冻结贴
            if ( (content.getContentState()==6 ||content.getContentState()==12) && !Objects.equals(content.getUserId(), userId)) continue;
            HomeContentVO contentVO = JSON.parseObject(JSON.toJSONString(content), HomeContentVO.class);
            User user = getUserByUserId(contentVO.getUserId());
            contentVO.setHeadimgUrl(user.getHeadimgUrl());
            contentVO.setNickName(user.getNickName());
            contentVO.setIsLike(1);
            //处理匿名信息
            if (contentVO.getContentType() == 3 || contentVO.getIsSpecial() == 1 || contentVO.getIsSpecial() == 4) {
                contentVO = contentService.dealAnonymous(contentVO);
            }
            jsonArray.add(contentVO);
        }
        JSONObject admireObj = new JSONObject();
        admireObj.put("records", jsonArray);
        admireObj.put("total", admireIPage.getTotal());
        admireObj.put("current", admireIPage.getCurrent());
        admireObj.put("pages", admireIPage.getPages());
        admireObj.put("size", admireIPage.getSize());
        return admireObj;
    }

    @Override
    public JSONObject getUserStateBySchool(String userId, Integer schoolId) {
        int state = 0;
        long bannedTimeStamp = 0;
        JSONObject stateObj = new JSONObject();
        //从数据库获取userSchool信息
        QueryWrapper<UserSchool> userSchoolQueryWrapper = new QueryWrapper<>();
        userSchoolQueryWrapper.eq("userId", userId)
                .eq("schoolId", schoolId)
                .orderByAsc("createTime");
        //修改selectOne,防止脏数据报错
        List <UserSchool> userSchoolList = userSchoolMapper.selectList(userSchoolQueryWrapper);
        UserSchool userSchool = new UserSchool();
        if (userSchoolList.size()>0){
            userSchool= userSchoolList.get(0);
        }
        if (null != userSchool) {
            state = userSchool.getUserState();
            //获取用户的userType,如果被禁言，获取用户的禁言时间
            if (userSchool.getUserType() == 2) {
                //查询禁言原因
                //用户处于禁言状态,判断是否到达解禁时间
                QueryWrapper<OperationRecord> operationRecordQueryWrapper = new QueryWrapper<>();
                operationRecordQueryWrapper.eq("target_id",userId)
                        .orderByDesc("operate_time");
                List<OperationRecord> operationRecords = operationRecordMapper.selectList(operationRecordQueryWrapper);
                //判断是否应该解禁，未到解禁时间，返回禁言时间戳
                bannedTimeStamp = operationRecordService.isReachBannedTime(userId, schoolId);
                if (bannedTimeStamp == 1) {
                    userSchool.setUserType(0);
                }
            }
            stateObj.put("userState", state);
            stateObj.put("userType", userSchool.getUserType());
            //兼容长期禁言,存在禁言时长才返回禁言时间
            if (userSchool.getUserType() == 2 && bannedTimeStamp != -1) {
                stateObj.put("bannedTime", new Date(bannedTimeStamp));
            }
        }else{
            userSchoolService.createUserSchoolRecord(userId,schoolId);
            stateObj.put("userState", state);
        }
        return stateObj;
    }

    //新签到，比较登录时间与每日0点
    public Integer signByLoginNew(User user) {
        Date lastLoginTime = user.getLastLoginTime();
        if (null == lastLoginTime) lastLoginTime = user.getUpdateTime(); //无上次登录时间的情况
        Date todayZero = initToday();
        Date yesterdayZero = initYesterday();
        Date todayNow = new Date();
        if (null == user.getHeadimgUrl()) //未认证用户直接跳过，无法积分
            return -1;
        QueryWrapper<Sign> signQueryWrapper = new QueryWrapper<>();
        signQueryWrapper.eq("userId", user.getUserId());
        //取第一个
        Sign sign = signMapper.selectList(signQueryWrapper).get(0);

        if (null == sign) {
            //首次签到，新增签到记录
            Sign newRecord = new Sign();
            newRecord.setUserId(user.getUserId());
            newRecord.setCount(1);
            newRecord.setFirstSign(1);
            newRecord.setCountInSeven(1);
            signMapper.insert(newRecord);
            return 1;
        } else {
            if (lastLoginTime.after(todayZero)) { //今日多次登录
                System.out.println("今日多次签到");
                sign.setFirstSign(0);
                signMapper.updateById(sign);
            } else if (todayZero.after(lastLoginTime)) {//今日首次登录
                System.out.println("今日首次签到");
                sign.setCount(sign.getCount() + 1);
                sign.setFirstSign(1);
                sign.setCountInSeven(sign.getCountInSeven() + 1);
                signMapper.updateById(sign);
            } else if (todayZero.after(lastLoginTime) && lastLoginTime.after(yesterdayZero)) {//连续登录
                System.out.println("连续签到");
                sign.setCount(sign.getCount() + 1);
                sign.setFirstSign(1);
                sign.setCountInSeven(sign.getCountInSeven() + 1);
                signMapper.updateById(sign);
                return sign.getCountInSeven() + 1;
            } else if (yesterdayZero.after(lastLoginTime)) {//距离上次登录已超过一天
                System.out.println("签到时间重置");
                sign.setCount(sign.getCount() + 1);
                sign.setFirstSign(1);
                sign.setCountInSeven(1);
                signMapper.updateById(sign);
                return 1;
            }
        }
        return -1;
    }

    //旧签到，比较两次登录时间之差
    public Integer signByLogin(User user) {
        if (null == user.getHeadimgUrl()) //未认证用户直接跳过，无法积分
            return -1;

        QueryWrapper<Sign> signQueryWrapper = new QueryWrapper<>();
        signQueryWrapper.eq("userId", user.getUserId());
        Sign sign = signMapper.selectOne(signQueryWrapper);


        if (null == sign) {
            //首次签到，新增签到记录
            Sign newRecord = new Sign();
            newRecord.setUserId(user.getUserId());
            newRecord.setCount(1);
            newRecord.setFirstSign(1);
            newRecord.setCountInSeven(1);
            signMapper.insert(newRecord);
            //积分重置为10
            user.setScores(10);
            userMapper.updateById(user);
            return 0;//用以返回前端渲染
        } else {
            //非首次签到，比较本地时间与记录更新时间
            Date localTime = new Date();
            Date updateTime = sign.getUpdateTime();
            int minutes = (int) ((localTime.getTime() - updateTime.getTime()) / (1000 * 60));
            System.out.println("本次与上次登录时间之差为：" + minutes);
            if (minutes > 2880) {//超过48小时
                sign.setCount(sign.getCount() + 1);
                sign.setFirstSign(1);
                sign.setCountInSeven(0);
                signMapper.updateById(sign);
                //积分+10
                user.setScores(user.getScores() + 10);
                userMapper.updateById(user);
                return 1;//用以返回前端渲染
            } else if (1440 < minutes && minutes < 2880) {//次日首次登录
                sign.setCount(sign.getCount() + 1);
                sign.setFirstSign(1);
                sign.setCountInSeven(sign.getCountInSeven() + 1);
                signMapper.updateById(sign);
                //积分增加
                user.setScores(user.getScores() + 10);
                userMapper.updateById(user);
                return sign.getCountInSeven() + 1;//用以返回前端渲染
            } else if (minutes < 1440) {//当日多次登录
                sign.setFirstSign(0);
                signMapper.updateById(sign);
                return -1;
            }

        }


        return -1;
    }

}
