package com.example.bbs.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.bbs.auto.UserTypeAutoEdit;
import com.example.bbs.mapper.BbsConfigMapper;
import com.example.bbs.mapper.CommentMapper;
import com.example.bbs.mapper.ContentMapper;
import com.example.bbs.pojo.*;
import com.example.bbs.pojo.vo.ChildrenCommentVO;
import com.example.bbs.pojo.vo.HomeCommentVO;
import com.example.bbs.service.*;
import com.example.bbs.util.ScoreUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.bbs.util.DateUtil.getChineseName;
import static com.example.bbs.util.DateUtil.idToNum;

@Service
@EnableCaching
public class CommentServiceImpl implements CommentService {
    @Autowired
    ContentMapper contentMapper;
    @Autowired
    CommentMapper commentMapper;
    @Autowired
    AdmireService admireService;
    @Autowired
    UserRecordService userRecordService;
    @Autowired
    UserTypeAutoEdit userTypeAutoEdit;
    @Autowired
    ContentService contentService;
    @Autowired
    ScoreUtil scoreUtil;
    @Autowired
    UserSchoolService userSchoolService;
    @Autowired
    UserService userService;
    @Autowired
    OperationRecordService operationRecordService;

    @Autowired
    CheckRecordService checkRecordService;

    @Autowired
    FuzzySearchService fuzzySearchService;

    @Autowired
    BbsConfigMapper bbsConfigMapper;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public int publishComment(Comment comment) {
        QueryWrapper<Content> contentQueryWrapper = new QueryWrapper<>();
        contentQueryWrapper.eq("contentId", comment.getContentId());
        Content content = contentMapper.selectOne(contentQueryWrapper);
        content.setCommentNum(content.getCommentNum() + 1);
        contentMapper.updateById(content);
        //如果是子评论，主评论的子评论数+1
        Comment replyComment = new Comment();
        if (comment.getIsMain()==0){
            QueryWrapper<Comment> commentQueryWrapper = new QueryWrapper<>();
            commentQueryWrapper.eq("commentId", comment.getReplyCommentId());
            replyComment = commentMapper.selectOne(commentQueryWrapper);
            replyComment.setChildrenNum(replyComment.getChildrenNum()+1);
            commentMapper.updateById(replyComment);
        }
        comment.setCommentText(comment.getCommentText());
        //设置新增评论的id
        String newUUID = UUID.randomUUID().toString().replace("-", "");
        comment.setCommentId(newUUID);
        commentMapper.insert(comment);
        //每日积分激励及记录明细
        if (scoreUtil.getActionNum(comment.getUserId(), 2) <= 3) {
            scoreUtil.changeScore(comment.getUserId(), 2, 15, comment.getCommentId());
        } else {
            System.out.println("今日积分获取已达上限！");
        }
        return 1;
    }


    @Override
    public List<HomeCommentVO> getHomeComments(String userId, String contentId, int pageNum, int pageSize) {
        //分页获取帖子主评论
        List<HomeCommentVO> homeCommentsPage = commentMapper.getHomeCommentsPage(contentId, (pageNum - 1) * pageSize, pageSize);
        //匿名贴特殊处理
        QueryWrapper<Content> contentQueryWrapper = new QueryWrapper<>();
        contentQueryWrapper.eq("contentId", contentId);
        Content content = contentMapper.selectOne(contentQueryWrapper);

        //删除贴处理
        if(content.getContentState()==7){
            return null;
        }

        //获取用户信息，用于前端渲染
        for (HomeCommentVO homeCommentVO : homeCommentsPage) {
            //获取发帖者的认证状态
            int currentUserState = userService.getUserStateBySchool(homeCommentVO.getUserId(),content.getSchoolId()).getInteger("userState");
            homeCommentVO.setUserState(currentUserState);
            //判断当前用户是否点赞
            if (admireService.isLikeByUserId(userId, homeCommentVO.getCommentId(), 0)) {
                homeCommentVO.setViewUserisLike(1);
            }
        }
        //获取每一个主评论的子评论
        for (HomeCommentVO homeCommentVO : homeCommentsPage) {
            List<ChildrenCommentVO> childrenCommentsVO = commentMapper.getChildrenCommentsVO(homeCommentVO.getContentId(), homeCommentVO.getCommentId());
            if (childrenCommentsVO != null && childrenCommentsVO.size() > 0) {
                //获取用户信息，用于前端渲染
                for (ChildrenCommentVO childrenCommentVO : childrenCommentsVO) {
                    //获取发帖者的认证状态
                    int currentUserState = userService.getUserStateBySchool(childrenCommentVO.getUserId(),content.getSchoolId()).getInteger("userState");
                    childrenCommentVO.setUserState(currentUserState);
                    //判断当前用户是否点赞
                    if (admireService.isLikeByUserId(userId, childrenCommentVO.getCommentId(), 0)) {
                        childrenCommentVO.setViewUserisLike(1);
                    }
                    if (content.getContentType() == 3 || content.getIsSpecial() == 1 || content.getIsSpecial() == 4) {//子评论匿名
                        childrenCommentVO = dealAnonymousChild(childrenCommentVO, content);
                    }
                }


                homeCommentVO.setChildren(childrenCommentsVO);
            }
        }
        if (content.getContentType() == 3 || content.getIsSpecial() == 1 || content.getIsSpecial() == 4) {
            homeCommentsPage = dealAnonymous(homeCommentsPage, content);
        }
        return homeCommentsPage;
    }

    //对评论的点赞/点踩进行操作
    @Override
    public int upNumByCommentId(String commentId, int isLike) {
        QueryWrapper<Comment> commentQueryWrapper = new QueryWrapper<>();
        commentQueryWrapper.eq("commentId", commentId);
        Comment comment = commentMapper.selectOne(commentQueryWrapper);
        if (isLike == 1) {//点赞
            comment.setUpNum(comment.getUpNum() + 1);
        } else if (isLike == 0) {//取消赞
            if (comment.getUpNum() > 0) {
                comment.setUpNum(comment.getUpNum() - 1);
            }
        } else if (isLike == 2) {//点踩
            comment.setDownNum(comment.getDownNum() + 1);
        } else if (isLike == 3) {//取消踩
            if (comment.getDownNum() > 0) {
                comment.setDownNum(comment.getDownNum() - 1);
            }
        }
        int result = commentMapper.updateById(comment);
        return result;
    }

    @Override
    public Comment getComment(String targetId) {
        QueryWrapper<Comment> commentQueryWrapper = new QueryWrapper<>();
        commentQueryWrapper.eq("commentId", targetId);
        Comment comment = commentMapper.selectOne(commentQueryWrapper);
        return comment;
    }

    @Override
    public void updateCommentType(String targetId, int type) {
        Comment comment = getComment(targetId);
        if (comment.getCommentState() != type) {
            comment.setCommentState(type);
            commentMapper.updateById(comment);
            //评论被折叠或者举报
            if (type == 1 || type == 5) {
                userRecordService.updateCommentRecordNum(targetId, type);
            }
            //触发用户类型检查和变更
            userTypeAutoEdit.changeUserType(targetId, 0);
        }
    }

    @Override
    public int getCommentType(String targetId) {
        QueryWrapper<Comment> commentQueryWrapper = new QueryWrapper<>();
        commentQueryWrapper.eq("commentId", targetId);
        Comment comment = commentMapper.selectOne(commentQueryWrapper);
        return comment.getCommentState();
    }

    @Override
    public void informComment(String targetId) {
        Comment comment = getComment(targetId);
        comment.setInformNum(comment.getInformNum() + 1);
        commentMapper.updateById(comment);
    }

    @Override
    public void informCommentWithSchoolId(String targetId,int schoolId) {
        Comment comment = getComment(targetId);
        //获取配置中举报上限
        QueryWrapper<BbsConfig> batchConfigQueryWrapper = new QueryWrapper<>();
        batchConfigQueryWrapper
                .eq("school_id", schoolId)
                .eq("config_type", "functionConfig");
        BbsConfig result = bbsConfigMapper.selectOne(batchConfigQueryWrapper);
        Object configObject =  JSON.parseObject(result.getConfigJson().toString());
        JSONObject configJsonObject = (JSONObject) configObject;
        JSONArray configJsonArray = configJsonObject.getJSONArray("data");
        int informLimit = 10;
        for (Object config:configJsonArray) {
            JSONObject configJO = (JSONObject)config;
            //在举报配置项中
            if(configJO.getString("function_name").equals("informConfig")){
                //获取举报上限
                informLimit =configJO.getInteger("informed_limit");
                break;
            }
        }
        comment.setInformNum(comment.getInformNum() + 1);
        //如果大于被举报上限，将帖子状态更改为待删除
        if (comment.getInformNum()>=informLimit){
            comment.setCommentState(4);
        }
        commentMapper.updateById(comment);
        //生成操作记录
        OperationRecord operationRecord =new OperationRecord();
        operationRecord.setSchoolId(schoolId);
        operationRecord.setRecordState(1);
        operationRecord.setOperator("admin");
        operationRecord.setTargetKind(3);
        operationRecord.setTargetId(targetId);
        operationRecord.setNote("到达举报上限，改评论为下架");
        String changeInfo = "{\"data\":{\"oldForm\":{\"contentid\":\"" + comment.getContentId() + "\",\"commentState\":0},\"newForm\":{\"contentid\":\"" + comment.getContentId() + "\",\"commentState\":4}}}";
        operationRecord.setChangeInfo(changeInfo);
        operationRecordService.addOperationRecord(operationRecord);
    }

    @Override
    public String getPlateId(Comment comment) {
        Content content = contentService.getContent(comment.getContentId());
        return content.getPlateId();
    }

    private ChildrenCommentVO dealAnonymousChild(ChildrenCommentVO childrenCommentVO, Content content) {
        String newName = getChineseName(content.getContentId(),childrenCommentVO.getUserId());
        String code = "";

        for (int j = 0; j < 7; j++) {
            if (j % 2 == 0) { //错位取ID
                code += content.getContentId().charAt(j);
            } else {
                code += childrenCommentVO.getUserId().charAt(j);
            }
        }
        //根据用户ID和帖子ID，生成该帖子下的唯一用户头像
        String regEx = "[^0-9]"; //提取所有数字
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(code);
        String num = m.replaceAll("").trim();
        if (num.equals("")) num = "0";
        int imageId = Integer.valueOf(num);
        imageId = imageId % 50;
        String headimgUrl = "https://image.tanxiaojian.zone/user/anonymous/" + String.valueOf(imageId) +
                ".png";
        if (content.getSchoolId()==9999){
            imageId = imageId % 34;
            headimgUrl = "https://image.tanxiaojian.zone/user/NXBAnonymous/" + String.valueOf(imageId) +
                    ".jpg";
        }
        childrenCommentVO.setHeadimgUrl(headimgUrl);
        //设置昵称最后的编码
        Integer preNum = idToNum(content.getContentId());
        Integer sufNum = idToNum(childrenCommentVO.getUserId());
        Integer total = preNum + sufNum;
        newName = newName + total;
        childrenCommentVO.setNickName(newName);
        return childrenCommentVO;
    }

    private List<HomeCommentVO> dealAnonymous(List<HomeCommentVO> homeCommentsPage, Content content) {
        for (int i = 0; i < homeCommentsPage.size(); i++) {
            HomeCommentVO commentVO = homeCommentsPage.get(i);
            //根据用户ID和帖子ID，生成该帖子下的唯一用户昵称
            //String newName = "隐士#";
            String newName = getChineseName(content.getContentId(),commentVO.getUserId());
            String code = "";

            for (int j = 0; j < 7; j++) {
                if (j % 2 == 0) { //错位取ID
                    code += content.getContentId().charAt(j);
                } else {
                    code += commentVO.getUserId().charAt(j);
                }
            }
            //根据用户ID和帖子ID，生成该帖子下的唯一用户头像
            String regEx = "[^0-9]"; //提取所有数字
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(code);
            String num = m.replaceAll("").trim();
            if (num.equals("")) num = "0";
            int imageId = Integer.valueOf(num);
            imageId = imageId % 50;
            String headimgUrl = "https://image.tanxiaojian.zone/user/anonymous/" + String.valueOf(imageId) +
                    ".png";
            if (content.getSchoolId()==9999){
                imageId = imageId % 34;
                headimgUrl = "https://image.tanxiaojian.zone/user/NXBAnonymous/" + String.valueOf(imageId) +
                        ".jpg";
            }
            commentVO.setHeadimgUrl(headimgUrl);
            //设置昵称最后的编码
            Integer preNum = idToNum(content.getContentId());
            Integer sufNum = idToNum(commentVO.getUserId());
            Integer total = preNum + sufNum;
            newName = newName + total;
            commentVO.setNickName(newName);
        }

        return homeCommentsPage;
    }

    @Override
    //含请求类型的获取主评论
    public List<HomeCommentVO> getHomeCommentsWithType(String userId, String contentId, int pageNum, int pageSize,String requestType) {
        //分页获取帖子主评论
        List<HomeCommentVO> homeCommentsPageWithType = commentMapper.getHomeCommentsPageFilterFreeze(contentId, (pageNum - 1) * pageSize, pageSize,requestType,userId);
        //匿名贴特殊处理
        QueryWrapper<Content> contentQueryWrapper = new QueryWrapper<>();
        contentQueryWrapper.eq("contentId", contentId);
        Content content = contentMapper.selectOne(contentQueryWrapper);

        //删除贴处理
        if(content.getContentState()==7){
            return null;
        }

        //获取用户信息，用于前端渲染
        for (HomeCommentVO homeCommentVO : homeCommentsPageWithType) {
            //获取发帖者的认证状态
            int currentUserState = userService.getUserStateBySchool(homeCommentVO.getUserId(),content.getSchoolId()).getInteger("userState");
            homeCommentVO.setUserState(currentUserState);
            //判断当前用户是否点赞
            if (admireService.isLikeByUserId(userId, homeCommentVO.getCommentId(), 0)) {
                homeCommentVO.setViewUserisLike(1);
            }
        }
        //获取每一个主评论的子评论
        for (HomeCommentVO homeCommentVO : homeCommentsPageWithType) {
            List<ChildrenCommentVO> childrenCommentsVO = commentMapper.getTwoChildrenCommentsVO(homeCommentVO.getContentId(), homeCommentVO.getCommentId());
            //判断是否有更多子评论
            if (childrenCommentsVO.size()==2||homeCommentVO.getChildrenNum()>=2){
                homeCommentVO.setHaveMoreChildren(1);
            }
            else{
                homeCommentVO.setHaveMoreChildren(0);
            }
            if (childrenCommentsVO != null && childrenCommentsVO.size() > 0) {
                //获取用户信息，用于前端渲染
                for (ChildrenCommentVO childrenCommentVO : childrenCommentsVO) {
                    //获取发帖者的认证状态
                    int currentUserState = userService.getUserStateBySchool(childrenCommentVO.getUserId(),content.getSchoolId()).getInteger("userState");
                    childrenCommentVO.setUserState(currentUserState);
                    //判断当前用户是否点赞
                    if (admireService.isLikeByUserId(userId, childrenCommentVO.getCommentId(), 0)) {
                        childrenCommentVO.setViewUserisLike(1);
                    }
                    if (content.getContentType() == 3 || content.getIsSpecial() == 1 || content.getIsSpecial() == 4) {//子评论匿名
                        childrenCommentVO = dealAnonymousChild(childrenCommentVO, content);
                    }
                }
                homeCommentVO.setChildrenShowState(1); //字评论显示状态，1展开
                homeCommentVO.setChildren(childrenCommentsVO);
            }
            else{
                homeCommentVO.setChildrenShowState(0); //没有子评论
            }
        }
        if (content.getContentType() == 3 || content.getIsSpecial() == 1 || content.getIsSpecial() == 4) {
            homeCommentsPageWithType = dealAnonymous(homeCommentsPageWithType, content);
        }
        return homeCommentsPageWithType;
    }

    @Override
//    分页获取子评论
    public List<ChildrenCommentVO> getChildrenCommentsVOPage(String userId, String contentId,  String commentId, int pageNum,  int pageSize, String queryTime){
        QueryWrapper<Content> contentQueryWrapper = new QueryWrapper<>();
        contentQueryWrapper.eq("contentId", contentId);
        Content content = contentMapper.selectOne(contentQueryWrapper);
        List<ChildrenCommentVO> childrenCommentsVO = commentMapper.getChildrenCommentsVOPageFilterFreeze(contentId, commentId, pageNum, pageSize, queryTime, userId);
        if (childrenCommentsVO != null && childrenCommentsVO.size() > 0) {
            //获取用户信息，用于前端渲染
            for (ChildrenCommentVO childrenCommentVO : childrenCommentsVO) {
                //获取发帖者的认证状态
                int currentUserState = userService.getUserStateBySchool(childrenCommentVO.getUserId(),content.getSchoolId()).getInteger("userState");
                childrenCommentVO.setUserState(currentUserState);
                //判断当前用户是否点赞
                if (admireService.isLikeByUserId(userId, childrenCommentVO.getCommentId(), 0)) {
                    childrenCommentVO.setViewUserisLike(1);
                }
                if (content.getContentType() == 3 || content.getIsSpecial() == 1 || content.getIsSpecial() == 4) {//子评论匿名
                    childrenCommentVO = dealAnonymousChild(childrenCommentVO, content);
                }
            }
        }
        return childrenCommentsVO;
    }

    @Override
    //含请求类型的获取主评论（更多联表查询）
    public List<HomeCommentVO> getHomeCommentsWithTypeNew(String userId, String contentId, int pageNum, int pageSize,String requestType) {
        //获取主贴信息
        QueryWrapper<Content> contentQueryWrapper = new QueryWrapper<>();
        contentQueryWrapper.eq("contentId", contentId);
        Content content = contentMapper.selectOne(contentQueryWrapper);
        int schoolId = content.getSchoolId();
        //分页获取帖子主评论
        List<HomeCommentVO> homeCommentsPageWithType = commentMapper.getHomeCommentsPageWithTypeNew(contentId, (pageNum - 1) * pageSize, pageSize,requestType,userId,schoolId);

        //删除贴处理
        if(content.getContentState()==7){
            return null;
        }
        //获取每一个主评论的子评论
        for (HomeCommentVO homeCommentVO : homeCommentsPageWithType) {
            //获取前两条子评论
            List<ChildrenCommentVO> childrenCommentsVO = commentMapper.getTwoChildrenCommentsVONew(homeCommentVO.getContentId(), homeCommentVO.getCommentId(),userId,schoolId);
            //判断是否有更多子评论
            if (childrenCommentsVO.size()>2){
                childrenCommentsVO.remove(2);
                homeCommentVO.setHaveMoreChildren(1);
            }
            else{
                homeCommentVO.setHaveMoreChildren(0);
            }
            if (childrenCommentsVO != null && childrenCommentsVO.size() > 0) {
                //获取用户信息，用于前端渲染
                for (ChildrenCommentVO childrenCommentVO : childrenCommentsVO) {
                    //处理匿名贴
                    if (content.getContentType() == 3 || content.getIsSpecial() == 1 || content.getIsSpecial() == 4) {//子评论匿名
                        childrenCommentVO = dealAnonymousChild(childrenCommentVO, content);
                    }
                }
                homeCommentVO.setChildrenShowState(1); //字评论显示状态，1展开
                homeCommentVO.setChildren(childrenCommentsVO);
            }
            else{
                homeCommentVO.setChildrenShowState(0); //没有子评论
            }
        }
        if (content.getContentType() == 3 || content.getIsSpecial() == 1 || content.getIsSpecial() == 4) {
            homeCommentsPageWithType = dealAnonymous(homeCommentsPageWithType, content);
        }
        return homeCommentsPageWithType;
    }


    private int judgeUserAdmireComment(String commentId, String userId){
        int isAdmire=0;
        if (admireService.isLikeByUserId(userId, commentId, 0)) {
            isAdmire=1;
            return isAdmire;
        }
        return isAdmire;
    }


    @Override
    public List<HomeCommentVO> getHomeCommentsPageWithHighLight(String userId, String contentId,int pageNum, int pageSize, String requestType ,String commentId) {
        //获取帖子信息
        QueryWrapper<Content> contentQueryWrapper = new QueryWrapper<>();
        contentQueryWrapper.eq("contentId", contentId);
        Content content = contentMapper.selectOne(contentQueryWrapper);

        List<HomeCommentVO> homeCommentsPageWithType = commentMapper.getHomeCommentsPageFilterFreeze(contentId, (pageNum - 1) * pageSize, pageSize,requestType,userId);

        //在开头加一条特定评论
        Comment highLightComment = commentMapper.selectById(commentId);
        HomeCommentVO highLightCommentVo = new HomeCommentVO();
        //这条评论的状态是正常的
        if (highLightComment.getCommentState()==1 ||highLightComment.getCommentState()==0){
            if (highLightComment!=null){
                //主评论
                if (highLightComment.getIsMain()==1){
                    highLightCommentVo = commentMapper.getCommentVoByCommentId(commentId);
                }
                //子评论,找对应主评论
                else{
                    highLightCommentVo = commentMapper.getCommentVoByCommentId(highLightComment.getReplyCommentId());
                }
            }
            if (highLightCommentVo != null) {
                // 删除可能存在的原始评论，基于 commentId 比较
                HomeCommentVO finalHighLightCommentVo = highLightCommentVo;
                homeCommentsPageWithType.removeIf(commentVo ->
                        commentVo.getCommentId().equals(finalHighLightCommentVo.getCommentId())
                );

                // 添加 highLightCommentVo 到第一位
                homeCommentsPageWithType.add(0, highLightCommentVo);
            }
        }
        //删除贴处理
        if(content.getContentState()==7){
            return null;
        }
        //获取用户信息，用于前端渲染
        for (HomeCommentVO homeCommentVO : homeCommentsPageWithType) {
            //获取通知发起人的认证状态，使用长期redis
            int currentUserState = userService.getUserStateBySchool(homeCommentVO.getUserId(),content.getSchoolId()).getInteger("userState");
            homeCommentVO.setUserState(currentUserState);
            //判断当前用户是否点赞(使用redis版)
            if (judgeUserAdmireComment(homeCommentVO.getCommentId(),userId)==1) {
                homeCommentVO.setViewUserisLike(1);
            }
        }
        int index =0;
        //获取每一个主评论的子评论
        for (HomeCommentVO homeCommentVO : homeCommentsPageWithType) {
            if (highLightCommentVo != null && highLightComment.getIsMain()==0 && index==0 && (highLightComment.getCommentState()==1 ||highLightComment.getCommentState()==0)){
                List<ChildrenCommentVO> allChildrenCommentVOList = commentMapper.getAllChildrenCommentVoFilterFreeze(contentId,homeCommentVO.getCommentId());
                if (allChildrenCommentVOList != null && allChildrenCommentVOList.size() > 0) {
                    //获取用户信息，用于前端渲染
                    for (ChildrenCommentVO childrenCommentVO : allChildrenCommentVOList) {
                        //获取通知发起人的认证状态，使用长期redis
                        int currentUserState = userService.getUserStateBySchool(childrenCommentVO.getUserId(),content.getSchoolId()).getInteger("userState");
                        childrenCommentVO.setUserState(currentUserState);
                        //判断当前用户是否点赞(使用redis版)
                        if (judgeUserAdmireComment(childrenCommentVO.getCommentId(),userId)==1) {
                            childrenCommentVO.setViewUserisLike(1);
                        }
                        if (content.getContentType() == 3 || content.getIsSpecial() == 1 || content.getIsSpecial() == 4) {//子评论匿名
                            childrenCommentVO = dealAnonymousChild(childrenCommentVO, content);
                        }
                    }
                    homeCommentVO.setChildrenShowState(1); //字评论显示状态，1展开
                    homeCommentVO.setChildren(allChildrenCommentVOList);
                }
                else{
                    homeCommentVO.setChildrenShowState(0); //没有子评论
                }
                index = index + 1;
                continue;
            }

            List<ChildrenCommentVO> threeChildrenCommentsVO = commentMapper.getThreeChildrenCommentsFilterFreeze(homeCommentVO.getContentId(), homeCommentVO.getCommentId(),userId);

            List<ChildrenCommentVO> childrenCommentsVO = new ArrayList<>();
            //判断是否有更多子评论
            if (threeChildrenCommentsVO.size()>2){
                //只返回前两条子评论
                childrenCommentsVO = threeChildrenCommentsVO.subList(0,2);
                homeCommentVO.setHaveMoreChildren(1);
            }
            else{
                childrenCommentsVO = threeChildrenCommentsVO;
                homeCommentVO.setHaveMoreChildren(0);
            }
            if (childrenCommentsVO != null && childrenCommentsVO.size() > 0) {
                //获取用户信息，用于前端渲染
                for (ChildrenCommentVO childrenCommentVO : childrenCommentsVO) {
                    //获取通知发起人的认证状态，使用长期redis
                    int currentUserState = userService.getUserStateBySchool(childrenCommentVO.getUserId(),content.getSchoolId()).getInteger("userState");
                    childrenCommentVO.setUserState(currentUserState);
                    //判断当前用户是否点赞(使用redis版)
                    if (judgeUserAdmireComment(childrenCommentVO.getCommentId(),userId)==1) {
                        childrenCommentVO.setViewUserisLike(1);
                    }
                    if (content.getContentType() == 3 || content.getIsSpecial() == 1 || content.getIsSpecial() == 4) {//子评论匿名
                        childrenCommentVO = dealAnonymousChild(childrenCommentVO, content);
                    }
                }
                homeCommentVO.setChildrenShowState(1); //字评论显示状态，1展开
                homeCommentVO.setChildren(childrenCommentsVO);
            }
            else{
                homeCommentVO.setChildrenShowState(0); //没有子评论
            }
        }
        if (content.getContentType() == 3 || content.getIsSpecial() == 1 || content.getIsSpecial() == 4) {
            homeCommentsPageWithType = dealAnonymous(homeCommentsPageWithType, content);
        }
        return homeCommentsPageWithType;
    }

}
