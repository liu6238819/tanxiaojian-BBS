package com.example.bbs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.bbs.mapper.*;
import com.example.bbs.pojo.*;
import com.example.bbs.result.CommonResult;
import com.example.bbs.service.OperationRecordService;
import com.example.bbs.service.UserSchoolService;
import com.example.bbs.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserSchoolServiceImpl implements UserSchoolService {
    @Autowired
    UserSchoolMapper userSchoolMapper;
    @Autowired
    PlateMapper plateMapper;
    @Autowired
    UserService userService;
    @Autowired
    TokenMapper tokenMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    OperationRecordService operationRecordService;
    @Autowired
    OperationRecordMapper operationRecordMapper;

    @Override
    public void updateType(String userId, int schoolId, int type) {
        QueryWrapper<UserSchool> userSchoolQueryWrapper = new QueryWrapper<>();
        userSchoolQueryWrapper.eq("userId", userId).eq("schoolId", schoolId);
        UserSchool userSchool = userSchoolMapper.selectOne(userSchoolQueryWrapper);
        userSchool.setUserType(type);
        userSchoolMapper.updateById(userSchool);
    }

    @Override
    public UserSchool getUserSchoolItem(String userId, int schoolId) {
        QueryWrapper<UserSchool> userSchoolQueryWrapper = new QueryWrapper<>();
        userSchoolQueryWrapper.eq("userId", userId)
                .eq("schoolId", schoolId)
                .orderByAsc("createTime");
        UserSchool userSchool=new UserSchool();
        List <UserSchool> userSchoolList = userSchoolMapper.selectList(userSchoolQueryWrapper);
        if (userSchoolList.size()==0){
            //无记录
            return null;
        }
        else{
            userSchool= userSchoolList.get(0);
        }
        return userSchool;
    }


    @Override
    public List<UserSchool> getWarnType(String userId) {
        QueryWrapper<UserSchool> userSchoolQueryWrapper = new QueryWrapper<>();
        userSchoolQueryWrapper.eq("userId", userId).eq("userType", 4);
        return userSchoolMapper.selectList(userSchoolQueryWrapper);
    }

    /**
     *
     * @param userId
     * @return
     */
    private int getCommonState(String userId, int schoolId) {
        User user = userService.getUserByUserId(userId);
        UserSchool userSchool = getUserSchoolItem(userId, schoolId);
        // 没有数据，不存在该用户
        if (null == user) {
            return 400;
        }
        // 无头像
        if (StringUtils.isEmpty(user.getHeadimgUrl())) {
            return 0;
        }
        // 有头像无电话
        if (StringUtils.isEmpty(user.getPhone())) {
            return 1;
        }
        // userState为0或不存在，访客

        if (null == userSchool || 0 == userSchool.getUserState()) {
            return 2;
        }
        // userState为1或不存在，审核中
        if (1 == userSchool.getUserState()) {
            return 3;
        }
        // userState为2或不存在，审核成功
        if (2 == userSchool.getUserState()) {
            return 4;
        }
        // userState为3，用户多学校认证
        if (3 == userSchool.getUserState()) {
            return 5;
        }

        //
        return user.getUserState();
    }

    /**
     * @param userId
     * @param schoolId
     * @param plateId
     * @return
     */
    @Override
    public CommonResult permisionCheck(String userId, int schoolId, String plateId) {
        int cateId = 0;
        if (plateId.equals("onCreate")) {//创建板块时
            cateId = 3;
        }else if(plateId.isEmpty()){//不填，则视为判断校内权限
            cateId = 3;
        } else {
            QueryWrapper<Plate> plateQueryWrapper = new QueryWrapper<>();
            plateQueryWrapper.eq("plateId", plateId);
            Plate plate = plateMapper.selectOne(plateQueryWrapper);
            cateId = plate.getCateId();
        }
        int commonState = getCommonState(userId, schoolId);
        UserSchool userSchool = getUserSchoolItem(userId, schoolId);
        int userType = 0;
        if (userSchool != null) {
            userType = userSchool.getUserType();
        }
        //判断是否为特殊板块
        if (cateId != 4) {
            // 未授权及认证用户
            if (0 == commonState) {
                CommonResult commonResult = CommonResult.failed();
                commonResult.setCode(100);
                commonResult.setMessage("请先完成授权登录与身份认证！");
                return commonResult;
            }
            // 在该学校下未认证校友身份用户
            if (1 == commonState || 2 == commonState) {
                CommonResult commonResult = CommonResult.failed();
                commonResult.setCode(200);
                commonResult.setMessage("新用户或审核失败，请进行校友身份认证！");
                return commonResult;
            }
            // 在该学校下审核中用户
            if (3 == commonState) {
                CommonResult commonResult = CommonResult.failed();
                commonResult.setCode(300);
                commonResult.setMessage("身份审核中，请耐心等待或联系客服。");
                return commonResult;
            }
            // 在该学校下审核通过用户
            if (4 == commonState) {
                if (0 == userType) {
                    CommonResult commonResult = CommonResult.success();
                    commonResult.setCode(0);
                    commonResult.setMessage("权限审核通过");
                    return commonResult;
                }
                if (1 == userType) {
                    CommonResult commonResult = CommonResult.failed();
                    commonResult.setCode(500);
                    commonResult.setMessage("被踩/举报次数过多，请注意发言内容！");
                    return commonResult;
                }
                if (2 == userType) {
                    CommonResult commonResult = CommonResult.failed();
                    commonResult.setCode(600);
                    commonResult.setMessage("您当前已被禁言，请联系客服申诉！");
                    return commonResult;
                }
                if (3 == userType) { //管理员
                    CommonResult commonResult = CommonResult.success();
                    commonResult.setCode(0);
                    commonResult.setMessage("权限审核通过");
                    return commonResult;
                }
            }
        } else {
            if (0 == commonState) {
                CommonResult commonResult = CommonResult.failed();
                commonResult.setCode(700);
                commonResult.setMessage("请先完成授权登录");
                return commonResult;
            } else {
                //无关系记录，先插入
                if (null == userSchool) {
                    UserSchool newRecord = new UserSchool();
                    newRecord.setUserState(0);
                    newRecord.setUserType(0);
                    newRecord.setUserId(userId);
                    newRecord.setSchoolId(schoolId);
                    userSchoolMapper.insert(newRecord);
                }
                if (0 == userType) {
                    CommonResult commonResult = CommonResult.success();
                    commonResult.setCode(0);
                    commonResult.setMessage("权限审核通过");
                    return commonResult;
                }
                if (1 == userType) {
                    CommonResult commonResult = CommonResult.failed();
                    commonResult.setCode(500);
                    commonResult.setMessage("被踩/举报次数过多，请注意发言内容！");
                    return commonResult;
                }
                if (2 == userType) {
                    CommonResult commonResult = CommonResult.failed();
                    commonResult.setCode(600);
                    commonResult.setMessage("您当前已被禁言，请联系客服申诉！");
                    return commonResult;
                }
                if (3 == userType) { //管理员
                    CommonResult commonResult = CommonResult.success();
                    commonResult.setCode(0);
                    commonResult.setMessage("权限审核通过");
                    return commonResult;
                }
            }

        }


        CommonResult commonResult = CommonResult.failed();
        commonResult.setCode(400);
        commonResult.setMessage("其他非法类型 userType " + userType + " commonsate  " + commonState);
        return commonResult;
    }

    @Override
    public CommonResult permissionCheckOutBBS(String userId, int schoolId, int schoolFlag) {
        int cateId = 1; //默认仅校内用户有权限
        if (schoolFlag == 0) {//校内外都有权限
            cateId = 4;
        }
        int commonState = getCommonState(userId, schoolId);
        UserSchool userSchool = getUserSchoolItem(userId, schoolId);
        int userType = 0;
        if (userSchool != null) {
            userType = userSchool.getUserType();
        }
        //判断是否为特殊板块
        if (cateId != 4) {
            // 未授权及认证用户
            if (0 == commonState) {
                CommonResult commonResult = CommonResult.failed();
                commonResult.setCode(100);
                commonResult.setMessage("请先完成授权登录与身份认证！");
                return commonResult;
            }
            // 在该学校下未认证校友身份用户
            if (1 == commonState || 2 == commonState) {
                CommonResult commonResult = CommonResult.failed();
                commonResult.setCode(200);
                commonResult.setMessage("新用户或审核失败，请进行校友身份认证！");
                return commonResult;
            }
            // 在该学校下审核中用户
            if (3 == commonState) {
                CommonResult commonResult = CommonResult.failed();
                commonResult.setCode(300);
                commonResult.setMessage("身份审核中，请耐心等待或联系客服。");
                return commonResult;
            }
            // 在该学校下审核通过用户
            if (4 == commonState) {
                if (0 == userType) {
                    CommonResult commonResult = CommonResult.success();
                    commonResult.setCode(0);
                    commonResult.setMessage("权限审核通过");
                    return commonResult;
                }
                if (1 == userType) {
                    CommonResult commonResult = CommonResult.success();
                    commonResult.setCode(0);
                    commonResult.setMessage("权限审核通过");
                    return commonResult;
                }
                if (2 == userType) {
                    //查询禁言原因
                    String note = "";
                    QueryWrapper<OperationRecord> operationRecordQueryWrapper = new QueryWrapper<>();
                    operationRecordQueryWrapper.eq("target_id",userId)
//                            .isNotNull("banned_time")
                            .and(wrapper -> wrapper.isNotNull("banned_time")
                                    .or()
                                    .like("note", "永久禁言"))
                            .orderByDesc("operate_time")
                            .last("LIMIT 5");
                    List<OperationRecord> operationRecords = operationRecordMapper.selectList(operationRecordQueryWrapper);
                    //有记录才处理
                    if(operationRecords.size()>0 ){
                        note = operationRecords.get(0).getNote();
                    }
                    if(StringUtils.isBlank(note)){
                        note = "您当前已被禁言，如有疑问可联系客服申诉。";
                    }else {
                        note = note.split(" ")[0];
                        if (!note.equals("null")){
                            note = "由于【"+note+"】，您当前已被禁言，如有疑问可联系客服申诉。";
                        }
                        else{
                            note = "您当前已被禁言，如有疑问可联系客服申诉。";
                        }
                    }

                    CommonResult commonResult = CommonResult.failed();
                    commonResult.setCode(600);
                    commonResult.setMessage(note);
                    //判断是否应该解禁，到达解禁时间，修改userType，返回权限审核通过
                    long bannedTimeStamp = operationRecordService.isReachBannedTime(userId,schoolId);
                    if(bannedTimeStamp==1){
                        commonResult = CommonResult.success();
                        commonResult.setCode(0);
                        commonResult.setMessage("权限审核通过");
                    }
                    return commonResult;
                }
                if (3 == userType) { //管理员
                    CommonResult commonResult = CommonResult.success();
                    commonResult.setCode(0);
                    commonResult.setMessage("权限审核通过");
                    return commonResult;
                }
            }
            // 用户有其他学校已认证
            if (5 == commonState) {
                CommonResult commonResult = CommonResult.failed();
                commonResult.setCode(800);
                commonResult.setMessage("您已在其他学校认证，请联系客服核实校友身份");
                return commonResult;
            }
        } else {
            if (0 == commonState) {
                CommonResult commonResult = CommonResult.failed();
                commonResult.setCode(700);
                commonResult.setMessage("请先完成授权登录");
                return commonResult;
            } else {
                //无关系记录，先插入
                if (null == userSchool) {
                    UserSchool newRecord = new UserSchool();
                    newRecord.setUserState(0);
                    newRecord.setUserType(0);
                    newRecord.setUserId(userId);
                    newRecord.setSchoolId(schoolId);
                    userSchoolMapper.insert(newRecord);
                }
                if (0 == userType) {
                    CommonResult commonResult = CommonResult.success();
                    commonResult.setCode(0);
                    commonResult.setMessage("权限审核通过");
                    return commonResult;
                }
                if (1 == userType) {
                    CommonResult commonResult = CommonResult.failed();
                    commonResult.setCode(500);
                    commonResult.setMessage("被踩/举报次数过多，请注意发言内容！");
                    return commonResult;
                }
                if (2 == userType) {
                    CommonResult commonResult = CommonResult.failed();
                    commonResult.setCode(600);
                    commonResult.setMessage("您当前已被禁言，请联系客服申诉！");
                    return commonResult;
                }
                if (3 == userType) { //管理员
                    CommonResult commonResult = CommonResult.success();
                    commonResult.setCode(0);
                    commonResult.setMessage("权限审核通过");
                    return commonResult;
                }
            }

        }


        CommonResult commonResult = CommonResult.failed();
        commonResult.setCode(400);
        commonResult.setMessage("其他非法类型 userType " + userType + " commonsate  " + commonState);
        return commonResult;
    }

    @Override
    public int offlineIdentification(String userId, int schoolId, String code) {
        int result = 1;
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("userId", userId);
        User user = userMapper.selectOne(userQueryWrapper);
        if (null == user || StringUtils.isBlank(user.getPhone())) return 2; //用户不存在或未完成授权
        QueryWrapper<Token> tokenQueryWrapper = new QueryWrapper<>();
        tokenQueryWrapper.eq("id", "school" + schoolId).eq("accessToken", code);
        Token token = tokenMapper.selectOne(tokenQueryWrapper);
        if (null == token) return 3; //验证码错误或已失效


        //若原本无记录，新增一条记录在user-school表里面,处理传递过来的数据school数组
        QueryWrapper<UserSchool> userSchoolQueryWrapper = new QueryWrapper<>();
        userSchoolQueryWrapper.eq("userId", userId)
                .eq("schoolId", schoolId)
                .orderByAsc("createTime");
//        UserSchool oldRecord = userSchoolMapper.selectOne(userSchoolQueryWrapper);
        //修改selectOne,防止脏数据报错
        UserSchool oldRecord=new UserSchool();
        List <UserSchool> userSchoolList = userSchoolMapper.selectList(userSchoolQueryWrapper);
        if (userSchoolList==null){
            //查询出错
            return 0;
        }
        if (userSchoolList.size()==0) {
            //无记录
            UserSchool userSchool = new UserSchool();
            userSchool.setUserState(2);
            userSchool.setUserType(0);
            userSchool.setUserId(userId);
            userSchool.setSchoolId(schoolId);
            //记录邀请人
            userSchool.setInviter("offline");
            userSchoolMapper.insert(userSchool);
        } else {
            oldRecord= userSchoolList.get(0);
            //有记录，直接通过，并记录邀请人
            oldRecord.setUserState(2);
            oldRecord.setInviter("offline");
            userSchoolMapper.updateById(oldRecord);
        }

        return result;
    }

    @Override
    public int answerIdentification(String userId, int schoolId) {
        int result = 1;
        User user = userService.getUserByUserId(userId);
        if (null == user || StringUtils.isBlank(user.getPhone())) return 2; //用户不存在或未完成授权

        //若原本无记录，新增一条记录在user-school表里面,处理传递过来的数据school数组
        QueryWrapper<UserSchool> userSchoolQueryWrapper = new QueryWrapper<>();
        userSchoolQueryWrapper.eq("userId", userId)
                .eq("schoolId", schoolId)
                .orderByAsc("createTime");
        //修改selectOne,防止脏数据报错
        UserSchool oldRecord=new UserSchool();
        List <UserSchool> userSchoolList = userSchoolMapper.selectList(userSchoolQueryWrapper);
        if (userSchoolList==null){
            //查询出错
            return 0;
        }
        if (userSchoolList.size()==0) {
            UserSchool userSchool = new UserSchool();
            userSchool.setUserState(2);
            userSchool.setUserType(0);
            userSchool.setUserId(userId);
            userSchool.setSchoolId(schoolId);
            //记录邀请人
            userSchool.setInviter("answer");
            userSchoolMapper.insert(userSchool);
        } else {
            oldRecord= userSchoolList.get(0);
            //有记录，直接通过，并记录邀请人
            oldRecord.setUserState(2);
            oldRecord.setInviter("answer");
            userSchoolMapper.updateById(oldRecord);
        }
        return result;
    }

    @Override
    public int haveIdentifiedInOtherSchool(String userId, int schoolId) {
        //获取该用户全部学校的认证记录
        QueryWrapper<UserSchool> userSchoolQueryWrapper = new QueryWrapper<>();
        userSchoolQueryWrapper.eq("userId", userId)
                .ne("schoolId",schoolId)
                .eq("userState",2);
        List<UserSchool>  otherSchoolRecords = userSchoolMapper.selectList(userSchoolQueryWrapper);
        //查询出错
        if (otherSchoolRecords==null){
            return 0;
        }
        //存在其他已认证记录
        else if (otherSchoolRecords.size()>0){
            QueryWrapper<UserSchool> userSchoolQueryWrapper2 = new QueryWrapper<>();
            userSchoolQueryWrapper2.eq("userId", userId)
                    .eq("schoolId", schoolId)
                    .orderByAsc("createTime");
            UserSchool oldRecord=new UserSchool();
            List <UserSchool> userSchoolList = userSchoolMapper.selectList(userSchoolQueryWrapper2);
            if (userSchoolList==null){
                //查询出错
                return 0;
            }
            if (userSchoolList.size()==0) {
                return 0;
            } else {
                oldRecord= userSchoolList.get(0);
                //有记录，将用户状态改为多学校
                oldRecord.setUserState(3);
                userSchoolMapper.updateById(oldRecord);
            }
            return 1;
        }
        //不存在其他已认证记录
        return 0;
    }

    @Override
    public int createUserSchoolRecord(String userId, int schoolId) {
        //已有userSchool记录
        QueryWrapper<UserSchool> userSchoolQueryWrapper = new QueryWrapper<>();
        userSchoolQueryWrapper.eq("userId", userId)
                .eq("schoolId", schoolId)
                .orderByAsc("createTime");
        List <UserSchool> userSchoolList = userSchoolMapper.selectList(userSchoolQueryWrapper);
        if (userSchoolList.size()>0){
            return 0;
        }
        UserSchool userSchool = new UserSchool();
        userSchool.setUserState(0);
        userSchool.setUserType(0);
        userSchool.setUserId(userId);
        userSchool.setSchoolId(schoolId);
        userSchoolMapper.insert(userSchool);
        return 1;
    }
}
