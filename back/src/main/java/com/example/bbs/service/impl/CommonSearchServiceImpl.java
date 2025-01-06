package com.example.bbs.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.bbs.mapper.*;
import com.example.bbs.pojo.*;
import com.example.bbs.pojo.vo.CommonSearchVO;
import com.example.bbs.pojo.vo.HomeCommentVO;
import com.example.bbs.pojo.vo.HomeContentVO;
import com.example.bbs.pojo.vo.SearchUserVO;
import com.example.bbs.result.CommonResult;
import com.example.bbs.service.CommonSearchService;
import com.example.bbs.service.UserManageService;
import com.example.bbs.service.UserService;
import com.example.bbs.service.VoteService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.sql.Array;
import java.util.*;
import java.util.List;

/**
 * 功能描述：
 *
 * @Author: lyc
 * @Date: 2022/2/10 10:22 上午
 */
@Service
public class CommonSearchServiceImpl implements CommonSearchService {
    String tableName = "";
    String mode = "";
    JSONObject conditionObj;
    @Autowired
    UserManageMapper userManageMapper;

    @Autowired
    AdmireMapper admireMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    PlateMapper plateMapper;
    @Autowired
    CommentMapper commentMapper;
    @Autowired
    ContentMapper contentMapper;
    @Autowired
    SchoolMapper schoolMapper;
    @Autowired
    UserService userService;

    @Autowired
    VoteService voteService;
    @Autowired
    VoteResultMapper voteResultMapper;

    @Autowired
    SearchRecordMapper searchRecordMapper;


    @Override
    public String parseObject(CommonSearchVO jsonObject) {
        Integer type = jsonObject.getType();
        Integer mode = jsonObject.getMode();
        switch (type) {
            case 1:
                tableName = "Content";
                break;
            case 2:
                tableName = "Plate";
                break;
            case 3:
                tableName = "Comment";
                break;
            case 4:
                tableName = "User";
                break;
            case 5:
                tableName = "School";
                break;
        }
        System.out.println("表名是:" + tableName);
        return tableName;
    }

    @Override
    public CommonResult getSearchList(String name, Integer mode, String condition, Integer pageNum, Integer pageSize, String sortType) {

        conditionObj = new JSONObject(JSONObject.parseObject(condition));
        Iterator<String> it = conditionObj.keySet().iterator();
        if (name.equals("User")) {
            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            QueryWrapper<School> schoolQueryWrapper = new QueryWrapper<>();
            while (it.hasNext()) {
                // 获得key
                String key = it.next();
                String value = conditionObj.getString(key);
//                这里需要处理逻辑
                if (key.equals("school")) {
                    schoolQueryWrapper.like("schoolName", value);
                    List<String> ids = new ArrayList<>();
                    List<School> schools = schoolMapper.selectList(schoolQueryWrapper);
                    for (Integer i = 0; i < schools.size(); i++) {
                        value = String.valueOf(schools.get(i).getSchoolId());
                        ids.add(value);
                        userQueryWrapper.or().like("school", value);
                    }
                } else {
                    userQueryWrapper.like(key, value);
                }

            }
            userQueryWrapper.orderByDesc(sortType);
            Page<User> userPage = new Page<>(pageNum, pageSize);
            IPage<User> users = userMapper.selectPage(userPage, userQueryWrapper);
            if (users != null) {
                JSONArray jsonArray = new JSONArray();
                JSONObject jsonObject = new JSONObject();
                for (Integer k = 0; k < users.getRecords().size(); k++) {
                    SearchUserVO searchUserVO = new SearchUserVO();
                    searchUserVO.setUserState(users.getRecords().get(k).getUserState());
                    searchUserVO.setUserType(users.getRecords().get(k).getUserType());
                    searchUserVO.setNickName(users.getRecords().get(k).getNickName());
                    searchUserVO.setPhone("*");
                    //这里的school要做一个中文对应
                    String schoolIds = users.getRecords().get(k).getInviter();
                    searchUserVO.setSchool(schoolIds);
                    searchUserVO.setCreateTime(users.getRecords().get(k).getCreateTime());
                    searchUserVO.setUpdateTime(users.getRecords().get(k).getUpdateTime());
                    searchUserVO.setHeadimgUrl(users.getRecords().get(k).getHeadimgUrl());
                    searchUserVO.setIntroduction(users.getRecords().get(k).getIntroduction());
                    jsonArray.add(searchUserVO);
                }
                jsonObject.put("records", jsonArray);
                jsonObject.put("total", users.getTotal());
                jsonObject.put("size", users.getSize());
                jsonObject.put("current", users.getCurrent());
                jsonObject.put("pages", users.getPages());
                return CommonResult.success(jsonObject);
            } else {
                return CommonResult.failed("用户检索失败");
            }
        }
        if (name.equals("Plate")) {
            QueryWrapper<Plate> plateQueryWrapper = new QueryWrapper<>();
            QueryWrapper<School> schoolQueryWrapper = new QueryWrapper<>();
            String searchUserId = "";
            while (it.hasNext()) {
                // 获得key
                String key = it.next();
                String value = conditionObj.getString(key);
                if (key.equals("schoolId")) {
                    plateQueryWrapper.eq(key, value);
                }
                if (key.equals("searchUserId")) { //搜索人Id，暂无用处
                    searchUserId = value;
                    continue;
                }
                plateQueryWrapper.like(key, value);
            }
            plateQueryWrapper.eq("plateState", 0);
            plateQueryWrapper.orderByDesc("createTime");
            Page<Plate> platePage = new Page<>(pageNum, pageSize);
            IPage<Plate> plates = plateMapper.selectPage(platePage, plateQueryWrapper);
            if (plates != null) {
                return CommonResult.success(plates);
            } else {
                return CommonResult.failed("板块检索失败");
            }
        }
        if (name.equals("Comment")) {
            QueryWrapper<Comment> commentQueryWrapper = new QueryWrapper<>();
            while (it.hasNext()) {
                // 获得key
                String key = it.next();
                String value = conditionObj.getString(key);
                commentQueryWrapper.like(key, value);
            }

            commentQueryWrapper.orderByDesc(sortType);
            Page<Comment> commentPage = new Page<>(pageNum, pageSize);
            IPage<Comment> comments = commentMapper.selectPage(commentPage, commentQueryWrapper);

            if (comments != null) {
                return CommonResult.success(comments);
            } else {
                return CommonResult.failed("评论检索失败");
            }
        }
        if (name.equals("Content")) {
            QueryWrapper<Content> contentQueryWrapper = new QueryWrapper<>();
            String searchUserId = "";
            //新增用户搜索记录
            String userId = conditionObj.getString("searchUserId");
            if (!StringUtils.isBlank(userId)) {
                String text = conditionObj.getString("title");
                //不为空
                if (!StringUtils.isBlank(text) && pageNum == 1) {
                    int schoolId = conditionObj.getInteger("schoolId");
                    addSearchRecord(userId, text, schoolId);
                }
            }
            while (it.hasNext()) {
                // 获得key
                String key = it.next();
                String value = conditionObj.getString(key);
                if (key.equals("searchUserId")) { //搜索人Id
                    searchUserId = value;
                    continue;
                }
                if (key.equals("schoolId")) { //查询学校不用like
                    contentQueryWrapper.eq(key, value);
                    continue;
                }
                if (key.equals("title")) { //标题搜索时还应搜索内容
                    //此处查询条件需用括号括起
                    contentQueryWrapper.and(wrapper -> wrapper.like(key, value).or().like("contentText", value));
                    continue;
                }
                contentQueryWrapper.like(key, value);
            }
            //过滤不正常类型，待审核与待删除
            contentQueryWrapper.ne("contentState", 7);


            contentQueryWrapper.orderByDesc(sortType);
            Page<Content> contentPage = new Page<>(pageNum, pageSize);
            IPage<Content> contents = contentMapper.selectPage(contentPage, contentQueryWrapper);
            List<Content> tempList = contents.getRecords();
            List<Content> result = new ArrayList<>();
            //只保留用户自己的私密帖和冻结贴和待审核贴
            for (int i = 0; i < tempList.size(); i++) {
                Content temp = tempList.get(i);
                if ( (temp.getContentState() != 6 && temp.getContentState() != 12 &&temp.getContentState() != 4) || (temp.getContentState() == 6 && temp.getUserId().equals(searchUserId)) || (temp.getContentState() == 12 && temp.getUserId().equals(searchUserId)) || (temp.getContentState() == 4 && temp.getUserId().equals(searchUserId)) ) {
                    result.add(tempList.get(i));
                }
            }

            if (contents != null) {
                List<HomeContentVO> resultVO = JSON.parseArray(JSON.toJSONString(result), HomeContentVO.class);
                for (Integer i = 0; i < resultVO.size(); i++) {
                    User user = userService.getUserByUserId(resultVO.get(i).getUserId());
                    resultVO.get(i).setNickName(user.getNickName());
                    //匿名贴处理
                    if (resultVO.get(i).getContentType() == 3 || resultVO.get(i).getIsSpecial() == 1 || resultVO.get(i).getIsSpecial() == 4) {
                        resultVO.get(i).setNickName("隐士");
                    }
                    //根据当前用户id和帖子id查询当前帖子是否点赞
                    resultVO.get(i).setIsLike(0);

                }
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("current", contents.getCurrent());
                jsonObject.put("pages", contents.getPages());
                jsonObject.put("size", contents.getSize());
                jsonObject.put("total", contents.getTotal());
                jsonObject.put("records", resultVO);
                return CommonResult.success(jsonObject);
            } else {
                return CommonResult.failed("帖子检索失败");
            }
        }
        if (name.equals("School")) {
            QueryWrapper<School> schoolQueryWrapper = new QueryWrapper<>();
            while (it.hasNext()) {
                // 获得key
                String key = it.next();
                String value = conditionObj.getString(key);
                schoolQueryWrapper.like(key, value);
            }
            Page<School> schoolPage = new Page<>(pageNum, pageSize);
            IPage<School> schools = schoolMapper.selectPage(schoolPage, schoolQueryWrapper);

            if (schools != null) {
                return CommonResult.success(schools);
            } else {
                return CommonResult.failed("学校检索失败");
            }
        } else {
            return CommonResult.failed("请选择合适的搜索类型");
        }


    }

    @Override
    public int addSearchRecord(String userId, String text, int schoolId) {
        SearchRecord searchRecord = new SearchRecord();
        String newUUID = UUID.randomUUID().toString().replace("-", "");
        Date nowDate = new Date();
        searchRecord.setId(newUUID);
        searchRecord.setUserId(userId);
        searchRecord.setSchoolId(schoolId);
        searchRecord.setText(text);
        searchRecord.setSearchTime(nowDate);
        searchRecord.setCreateTime(nowDate);
        searchRecord.setUpdateTime(nowDate);
        //在redis中插入用户搜索记录
        int result = searchRecordMapper.insert(searchRecord);
        return result;
    }

    private JSONArray mapperId2Name(String schoolIds) {
        String[] arr = schoolIds.replace("[", "").replace("]", "").split(",");
        JSONArray jsonArray = new JSONArray();

        for (int i = 0; i < arr.length; i++) {
            JSONObject jsonObject = new JSONObject();
            QueryWrapper<School> schoolQueryWrapper = new QueryWrapper<>();
            schoolQueryWrapper.eq("schoolId", arr[i]);
            School school = schoolMapper.selectOne(schoolQueryWrapper);
            jsonObject.put("id", arr[i]);
            jsonObject.put("name", school.getSchoolName());
            System.out.println(arr);
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }
}
