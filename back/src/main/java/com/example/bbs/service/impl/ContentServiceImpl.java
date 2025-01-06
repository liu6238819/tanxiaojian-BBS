package com.example.bbs.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.bbs.auto.UserTypeAutoEdit;
import com.example.bbs.mapper.*;
import com.example.bbs.pojo.*;
import com.example.bbs.pojo.vo.HomeCommentVO;
import com.example.bbs.pojo.vo.HomeContentVO;
import com.example.bbs.service.*;
import com.example.bbs.util.ScoreUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.example.bbs.util.DateUtil.getChineseName;
import static com.example.bbs.util.DateUtil.idToNum;

@Service
@EnableCaching
public class ContentServiceImpl implements ContentService {
    @Autowired
    ContentMapper contentMapper;
    @Autowired
    SchoolInfoMapper schoolInfoMapper;
    @Autowired
    AdmireMapper admireMapper;
    @Autowired
    UserTypeAutoEdit userTypeAutoEdit;
    @Autowired
    UserRecordService userRecordService;
    @Autowired
    VoteService voteService;
    @Autowired
    VoteResultMapper voteResultMapper;
    @Autowired
    PlateMapper plateMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    ScoreUtil scoreUtil;
    @Autowired
    UserSchoolService userSchoolService;
    @Autowired
    UserService userService;
    @Autowired
    CommentMapper commentMapper;
    @Autowired
    MarkMapper markMapper;
    @Autowired
    BbsConfigMapper bbsConfigMapper;
    @Autowired
    OperationRecordService operationRecordService;
    @Autowired
    ShortUrlService shortUrlService;
    @Autowired
    CheckRecordService checkRecordService;
    @Autowired
    FuzzySearchService fuzzySearchService;
    @Autowired
    FocusMapper followMapper;
    @Autowired
    RedisService redisService;

    public int publishContent(Content content) {
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
            System.out.println("两次发帖间隔：" + minutes);
            if (minutes < 5) {
                System.out.println("发帖过于频繁！");
                return -2;
            }
        }
        String newUUID = UUID.randomUUID().toString().replace("-", "");
        content.setContentId(newUUID);
        content.setTitle(content.getTitle());
        content.setContentText(content.getContentText());
        //增加帖子敏感词审核
        int checkRes = checkRecordService.CheckContentSensitiveWord(content);
        if (checkRes==1){
            content.setContentState(4);
        }
        contentMapper.insert(content);
        //更新板块表中内容数量即+1
        QueryWrapper<Plate> plateQueryWrapper = new QueryWrapper<>();
        plateQueryWrapper.eq("plateId", content.getPlateId());
        Plate plate = plateMapper.selectOne(plateQueryWrapper);
        plate.setContentNum(plate.getContentNum() + 1);
        plateMapper.update(plate, plateQueryWrapper);
        //投票贴
        if (content.getContentType() == 1) {
            for (Vote vote : content.getVotes()) {
                vote.setContentId(content.getContentId());
                voteService.addVote(vote);
            }
        }
        //每日积分激励及记录明细
        if (scoreUtil.getActionNum(content.getUserId(), 1) <= 1) {
            scoreUtil.changeScore(content.getUserId(), 1, 25, content.getContentId());
        } else {
            System.out.println("今日积分获取已达上限！");
        }
        return 1;
    }

    @Override
    public int publishContent0605(Content content) {
        //前端传入帖子id，使用前端传入的，没有就重新生成一个
        if (StringUtils.isBlank(content.getContentId())) {
            String newUUID = UUID.randomUUID().toString().replace("-", "");
            content.setContentId(newUUID);
        }
        content.setTitle(content.getTitle());
        content.setContentText(content.getContentText());
        contentMapper.insert(content);
        //更新板块表中内容数量即+1
        QueryWrapper<Plate> plateQueryWrapper = new QueryWrapper<>();
        plateQueryWrapper.eq("plateId", content.getPlateId());
        Plate plate = plateMapper.selectOne(plateQueryWrapper);
        plate.setContentNum(plate.getContentNum() + 1);
        plateMapper.update(plate, plateQueryWrapper);
        //投票贴
        if (content.getContentType() == 1) {
            for (Vote vote : content.getVotes()) {
                vote.setContentId(content.getContentId());
                voteService.addVote(vote);
            }
        }
        //每日积分激励及记录明细
        if (scoreUtil.getActionNum(content.getUserId(), 1) <= 1) {
            scoreUtil.changeScore(content.getUserId(), 1, 25, content.getContentId());
        } else {
            System.out.println("今日积分获取已达上限！");
        }
        return 1;
    }

    @Override
    public List<HomeContentVO> getHomeContents(String userId, String searchUserId, String plateId, int pageNum, int pageSize, int schoolId, String requestType, int contentType, int duration) {
        //分页查询帖子信息及发帖人信息
        List<HomeContentVO> homeContentsVOList = null;
        if (requestType.equals("time")) {
            homeContentsVOList = contentMapper.getContentsPageByTime(schoolId, searchUserId, plateId, (pageNum - 1) * pageSize, pageSize, contentType);

        } else if (requestType.equals("hot")) {
            homeContentsVOList = contentMapper.getContentsPageByHot(schoolId, searchUserId, plateId, (pageNum - 1) * pageSize, pageSize, duration);
            //return前判断查询是否出错
            if (null == homeContentsVOList) {
                return null;
            } else {
                return homeContentsVOList;
            }
        } else if (requestType.equals("follow")) {
            homeContentsVOList = contentMapper.getContentsPageByFollow(schoolId, userId, (pageNum - 1) * pageSize, pageSize);

        } else if (requestType.equals("top")) {
            homeContentsVOList = contentMapper.getTopContents(schoolId, plateId, (pageNum - 1) * pageSize, pageSize);
            //return前判断查询是否出错
            if (null == homeContentsVOList) {
                return null;
            } else {
                return homeContentsVOList;
            }
        }
        if (null == homeContentsVOList) {
            return null;
        } else {
            for (HomeContentVO homeContentsVO : homeContentsVOList) {
                //判断当前帖子是否为投票贴
                if (homeContentsVO.getContentType() == 1) {
                    homeContentsVO.setVotes(voteService.getVotesByContentId(homeContentsVO.getContentId()));
                    //查询该用户是否投票

                    QueryWrapper<VoteResult> voteResultQueryWrapper = new QueryWrapper<>();
                    voteResultQueryWrapper
                            .eq("contentId", homeContentsVO.getContentId())
                            .eq("userId", userId)
                    ;
                    List<VoteResult> voteList = voteResultMapper.selectList(voteResultQueryWrapper);
                    if (voteList.size() != 0) {
                        homeContentsVO.setDoVote("voted!");
                    }

                }
                //判断当前帖子是否为信息集合贴
                if (homeContentsVO.getContentType() == 4 || homeContentsVO.getIsSpecial() == 2) {
                    QueryWrapper<SchoolInfo> schoolInfoQueryWrapper = new QueryWrapper<>();
                    schoolInfoQueryWrapper.eq("contentId", homeContentsVO.getContentId());
                    List<SchoolInfo> schoolInfoList = schoolInfoMapper.selectList(schoolInfoQueryWrapper);
                    homeContentsVO.setSchoolInfos(schoolInfoList);
                }
                //根据当前用户id和帖子id查询当前帖子是否点赞
                QueryWrapper<Admire> likeQueryWrapper = new QueryWrapper<>();
                likeQueryWrapper
                        .eq("userId", userId)
                        .eq("targetId", homeContentsVO.getContentId())
                        .eq("isContent", 1);
                Admire admire = admireMapper.selectOne(likeQueryWrapper);
                if (admire != null) {
                    homeContentsVO.setIsLike(admire.getIsLike());
                }

                //根据当前用户id和帖子id查询当前帖子是否马住
                QueryWrapper<Mark> markQueryWrapper = new QueryWrapper<>();
                markQueryWrapper
                        .eq("user_id", userId)
                        .eq("content_id", homeContentsVO.getContentId());
                Mark mark = markMapper.selectOne(markQueryWrapper);
                if (mark != null) {
                    homeContentsVO.setIsMark(mark.getIsMark());
                }

                //查询帖子的马住人数
                QueryWrapper<Mark> markQueryWrapper2 = new QueryWrapper<>();
                markQueryWrapper2
                        .eq("is_mark", 1)
                        .eq("content_id", homeContentsVO.getContentId())
                        .orderByAsc("create_time");
                List<Mark> mark2 = markMapper.selectList(markQueryWrapper2);
                if (mark2 != null) {
                    homeContentsVO.setMarkCount(mark2.size());
                    if (mark2.size() > 0) {
                        User markUser = userMapper.selectById(mark2.get(0).getUserId());
                        String markNickName = markUser.getNickName();
                        //处理匿名贴
                        if (homeContentsVO.getContentType() == 3 || homeContentsVO.getIsSpecial() == 1 || homeContentsVO.getIsSpecial() == 4) { //匿名贴处理
                            HomeContentVO markUserContentVo = new HomeContentVO();
                            markUserContentVo.setContentId(homeContentsVO.getContentId());
                            markUserContentVo.setUserId(markUser.getUserId());
                            markUserContentVo = dealAnonymous(markUserContentVo);
                            markNickName = markUserContentVo.getNickName();
                        }
                        homeContentsVO.setMarkNickName(markNickName);

                    }
                }

                //获取发帖者的认证状态
                int currentUserState = userService.getUserStateBySchool(homeContentsVO.getUserId(), homeContentsVO.getSchoolId()).getInteger("userState");
                homeContentsVO.setUserState(currentUserState);
                //处理匿名贴
                if (homeContentsVO.getContentType() == 3 || homeContentsVO.getIsSpecial() == 1 || homeContentsVO.getIsSpecial() == 4) { //匿名贴处理
                    homeContentsVO = dealAnonymous(homeContentsVO);
                }
                //获取板块名称
                QueryWrapper<Plate> plateQueryWrapper = new QueryWrapper<>();
                plateQueryWrapper.eq("plateId", homeContentsVO.getPlateId());
                Plate plate = plateMapper.selectOne(plateQueryWrapper);
                if (plate != null) {
                    homeContentsVO.setPlateName(plate.getName());
                }

                //获取帖子前两条主评论
                if (homeContentsVO.getCommentNum() > 0 && requestType.equals("time")) {
                    List<HomeCommentVO> mainComments = commentMapper.getMainCommentsByContentId(homeContentsVO.getContentId());
                    //匿名处理
                    if (homeContentsVO.getContentType() == 3 || homeContentsVO.getIsSpecial() == 1 || homeContentsVO.getIsSpecial() == 4) {
                        for (HomeCommentVO mainComment : mainComments) {
                            mainComment = hotCommentDealAnonymous(mainComment, homeContentsVO);
                        }
                    }
                    homeContentsVO.setHotComments(mainComments);

                }

            }
            return homeContentsVOList;
        }
    }

    //对帖子点赞/点踩进行操作
    @Override
    public int upAndDownNumEditByContentId(String contentId, int isLike) {
        QueryWrapper<Content> contentQueryWrapper = new QueryWrapper<>();
        contentQueryWrapper.eq("contentId", contentId);
        Content content = contentMapper.selectOne(contentQueryWrapper);
        if (isLike == 1) {//点赞
            content.setUpNum(content.getUpNum() + 1);
        } else if (isLike == 0) {//取消赞
            if (content.getUpNum() > 0) {
                content.setUpNum(content.getUpNum() - 1);
            }
        } else if (isLike == 2) {//点踩
            content.setDownNum(content.getDownNum() + 1);
        } else if (isLike == 3) {//取消踩
            if (content.getDownNum() > 0) {
                content.setDownNum(content.getDownNum() - 1);
            }
        }
        int result = contentMapper.updateById(content);
        return result;
    }

    @Override
    public Content getContent(String targetId) {
        QueryWrapper<Content> contentQueryWrapper = new QueryWrapper<>();
        contentQueryWrapper.eq("contentId", targetId);
        Content content = contentMapper.selectOne(contentQueryWrapper);
        return content;
    }

    @Override
    public void updateContentType(String targetId, int type) {
        Content content = getContent(targetId);
        //若数据库本身的状态与要变更的状态一致，则不用变更
        if (content.getContentType() != type) {
            content.setContentType(type);
            contentMapper.updateById(content);
            //如果帖子状态要变更为折叠或者举报，则更新用户记录表
            if (type == 1 || type == 5) {
                userRecordService.updateContentRecordNum(targetId, type);
            }
            //触发用户类型检查和变更
            userTypeAutoEdit.changeUserType(targetId, 1);
        }
    }

    @Override
    public int getContentType(String targetId) {
        QueryWrapper<Content> contentQueryWrapper = new QueryWrapper<>();
        contentQueryWrapper.eq("contentId", targetId);
        Content content = contentMapper.selectOne(contentQueryWrapper);
        return content.getContentType();
    }

    @Override
    public void informContent(String contentId) {
        Content content = getContent(contentId);
        content.setInformNum(content.getInformNum() + 1);
        contentMapper.updateById(content);
    }

    @Override
    public void informContentWithSchoolId(String contentId, int schoolId) {
        Content content = getContent(contentId);
        //获取配置中举报上限
        QueryWrapper<BbsConfig> batchConfigQueryWrapper = new QueryWrapper<>();
        batchConfigQueryWrapper
                .eq("school_id", schoolId)
                .eq("config_type", "functionConfig");
        BbsConfig result = bbsConfigMapper.selectOne(batchConfigQueryWrapper);
        Object configObject = JSON.parseObject(result.getConfigJson().toString());
        JSONObject configJsonObject = (JSONObject) configObject;
        JSONArray configJsonArray = configJsonObject.getJSONArray("data");
        int informLimit = 10;
        for (Object config : configJsonArray) {
            JSONObject configJO = (JSONObject) config;
            //在弹窗配置项中
            if (configJO.getString("function_name").equals("informConfig")) {
                //获取不再提醒天数
                informLimit = configJO.getInteger("informed_limit");
                break;
            }
        }
        content.setInformNum(content.getInformNum() + 1);
        //如果大于被举报上限，将帖子状态更改为待删除
        if (content.getInformNum() >= informLimit) {
            content.setContentState(7);
            contentMapper.updateById(content);
            //生成操作记录
            OperationRecord operationRecord = new OperationRecord();
            operationRecord.setSchoolId(schoolId);
            operationRecord.setRecordState(1);
            operationRecord.setOperator("admin");
            operationRecord.setTargetKind(2);
            operationRecord.setTargetId(contentId);
            operationRecord.setNote("到达举报上限，改帖子为删除");
            operationRecord.setChangeInfo("{\"data\":{\"oldForm\":{\"contentState\":0},\"newForm\":{\"contentState\":7}}}");
            operationRecordService.addOperationRecord(operationRecord);
        }
        //否则，直接更新
        else {
            contentMapper.updateById(content);
        }
    }

    @Override
    public void addReadNum(String contentId, String userId) {
        //if (StringUtils.isBlank(userId)) return;
        Content content = getContent(contentId);
        if (null == content) return;
        content.setReadNum(content.getReadNum() + 1);
        contentMapper.updateById(content);
    }

    @Override
    public HomeContentVO getContentById(String contentId, String userId) {
        HomeContentVO homeContentVO = contentMapper.getContentById(contentId);
        if (homeContentVO.getContentType() == 1) {//投票贴处理
            homeContentVO.setVotes(voteService.getVotesByContentId(homeContentVO.getContentId()));
            //查询该用户是否投票
            QueryWrapper<VoteResult> voteResultQueryWrapper = new QueryWrapper<>();
            voteResultQueryWrapper
                    .eq("contentId", homeContentVO.getContentId())
                    .eq("userId", userId)
            ;
            List<VoteResult> voteList = voteResultMapper.selectList(voteResultQueryWrapper);
            if (voteList.size() != 0) {
                homeContentVO.setDoVote("voted!");
            }

        }
        if (homeContentVO.getContentType() == 4 || homeContentVO.getIsSpecial() == 2) {//信息集合贴处理
            QueryWrapper<SchoolInfo> schoolInfoQueryWrapper = new QueryWrapper<>();
            schoolInfoQueryWrapper.eq("contentId", homeContentVO.getContentId());
            List<SchoolInfo> schoolInfoList = schoolInfoMapper.selectList(schoolInfoQueryWrapper);
            homeContentVO.setSchoolInfos(schoolInfoList);
        }
        //获取发帖者的认证状态，长期redis版
        int currentUserState = userService.getUserStateBySchool(homeContentVO.getUserId(), homeContentVO.getSchoolId()).getInteger("userState");
        homeContentVO.setUserState(currentUserState);
        //根据当前用户id和帖子id查询当前帖子是否点赞
        QueryWrapper<Admire> likeQueryWrapper = new QueryWrapper<>();
        likeQueryWrapper
                .eq("userId", userId)
                .eq("targetId", homeContentVO.getContentId())
                .eq("isContent", 1);
        Admire admire = admireMapper.selectOne(likeQueryWrapper);
        if (admire != null) {
            homeContentVO.setIsLike(admire.getIsLike());
        }

        //根据当前用户id和帖子id查询当前帖子是否马住
        QueryWrapper<Mark> markQueryWrapper = new QueryWrapper<>();
        markQueryWrapper
                .eq("user_id", userId)
                .eq("content_id", homeContentVO.getContentId());
        //缓解mark记录不唯一的情况
        List<Mark> markList = markMapper.selectList(markQueryWrapper);
        //查询出错时，继续流程
        if (markList == null) {
        }
        if (markList.size() > 0) {
            Mark mark = markList.get(0);
            homeContentVO.setIsMark(mark.getIsMark());
        }

        //获取板块名称
        QueryWrapper<Plate> plateQueryWrapper = new QueryWrapper<>();
        plateQueryWrapper.eq("plateId", homeContentVO.getPlateId());
        Plate plate = plateMapper.selectOne(plateQueryWrapper);
        if (plate != null) {
            homeContentVO.setPlateName(plate.getName());
        }

        //删除贴处理
        if (homeContentVO.getContentState() == 7) {
            homeContentVO.setTitle("【帖子已不存在】");
            homeContentVO.setContentText("【帖子已不存在】");
            homeContentVO.setHotComments(null);
        }
        //私密贴处理（当事人可见）
        if ((homeContentVO.getContentState() == 6) && !homeContentVO.getUserId().equals(userId)) {
            homeContentVO.setTitle("【帖子已不存在】");
            homeContentVO.setContentText("【帖子已不存在】");
            homeContentVO.setHotComments(null);
        }
        //冻结贴处理（当事人可见）
        if ((homeContentVO.getContentState() == 12) && !homeContentVO.getUserId().equals(userId)) {
            homeContentVO.setTitle("【帖子已不存在】");
            homeContentVO.setContentText("【帖子已不存在】");
            homeContentVO.setHotComments(null);
        }


        return homeContentVO;
    }

    @Override
    public HomeContentVO dealAnonymous(HomeContentVO content) {
        //根据用户ID和帖子ID，生成该帖子下的唯一用户昵称
        //String newName = "隐士#";
        String newName = getChineseName(content.getContentId(), content.getUserId());
        String code = "";
        for (int j = 0; j < 7; j++) {
            if (j % 2 == 0) {
                code += content.getContentId().charAt(j);
            } else {
                code += content.getUserId().charAt(j);
            }
        }
        //根据用户ID和帖子ID，生成该帖子下的唯一用户头像
        String regEx = "[^0-9]"; //提取所有数字
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(code);
        String num = m.replaceAll("").trim();
        //纯字符串处理
        if (num.equals("")) num = "0";
        int imageId = Integer.valueOf(num);
        imageId = imageId % 50;
        //图片地址
        String headimgUrl = "https://image.test.zone/" + String.valueOf(imageId) +
                ".png";
        if (content.getSchoolId()==9999){
            imageId = imageId % 34;
            headimgUrl = "https://image.test.zone/anonymous/" + String.valueOf(imageId) +
                    ".jpg";
        }
        content.setHeadimgUrl(headimgUrl);
        //设置昵称最后的编码
        Integer preNum = idToNum(content.getContentId());
        Integer sufNum = idToNum(content.getUserId());
        Integer total = preNum + sufNum;
        newName = newName + total;
        content.setNickName(newName);
        return content;
    }

    @Override
    public List<HomeContentVO> getHistoryContents(String userId, int pageNum, int pageSize, int schoolId, String requestType, int contentType, int duration) {
        List<HomeContentVO> homeContentsVOList = new ArrayList<>();
        //时光机
        if (requestType.equals("timeMachine")) {

            homeContentsVOList = contentMapper.getContentsPageBeforeTime(schoolId, (pageNum - 1) * pageSize, pageSize, contentType, duration);
        }
        //历史热帖
        else if (requestType.equals("historyHot")) {
            int hotStandardNum = 0;
            QueryWrapper<BbsConfig> bbsConfigQueryWrapper = new QueryWrapper<>();
            bbsConfigQueryWrapper
                    .eq("config_type", "dailySelection")
                    .eq("school_id", schoolId);
            BbsConfig dailySelectionConfig = bbsConfigMapper.selectOne(bbsConfigQueryWrapper);
            Object configJson = dailySelectionConfig.getConfigJson();
            JSONObject configJsonObject = JSONObject.parseObject(configJson.toString());
            hotStandardNum = Integer.parseInt(configJsonObject.get("hotStandardNum").toString());
            homeContentsVOList = contentMapper.getContentsPageHistoryHot(schoolId, (pageNum - 1) * pageSize, pageSize, duration, hotStandardNum);
        }
        if (null == homeContentsVOList) {
            return null;
        } else {
            for (HomeContentVO homeContentsVO : homeContentsVOList) {
                //判断当前帖子是否为投票贴
                if (homeContentsVO.getContentType() == 1) {
                    homeContentsVO.setVotes(voteService.getVotesByContentId(homeContentsVO.getContentId()));
                    //查询该用户是否投票

                    QueryWrapper<VoteResult> voteResultQueryWrapper = new QueryWrapper<>();
                    voteResultQueryWrapper
                            .eq("contentId", homeContentsVO.getContentId())
                            .eq("userId", userId)
                    ;
                    List<VoteResult> voteList = voteResultMapper.selectList(voteResultQueryWrapper);
                    if (voteList.size() != 0) {
                        homeContentsVO.setDoVote("voted!");
                    }

                }
                //判断当前帖子是否为信息集合贴
                if (homeContentsVO.getContentType() == 4 || homeContentsVO.getIsSpecial() == 2) {
                    QueryWrapper<SchoolInfo> schoolInfoQueryWrapper = new QueryWrapper<>();
                    schoolInfoQueryWrapper.eq("contentId", homeContentsVO.getContentId());
                    List<SchoolInfo> schoolInfoList = schoolInfoMapper.selectList(schoolInfoQueryWrapper);
                    homeContentsVO.setSchoolInfos(schoolInfoList);
                }
                //根据当前用户id和帖子id查询当前帖子是否点赞
                QueryWrapper<Admire> likeQueryWrapper = new QueryWrapper<>();
                likeQueryWrapper
                        .eq("userId", userId)
                        .eq("targetId", homeContentsVO.getContentId())
                        .eq("isContent", 1);
                Admire admire = admireMapper.selectOne(likeQueryWrapper);
                if (admire != null) {
                    homeContentsVO.setIsLike(admire.getIsLike());
                }

                //根据当前用户id和帖子id查询当前帖子是否马住
                QueryWrapper<Mark> markQueryWrapper = new QueryWrapper<>();
                markQueryWrapper
                        .eq("user_id", userId)
                        .eq("content_id", homeContentsVO.getContentId());
                Mark mark = markMapper.selectOne(markQueryWrapper);
                if (mark != null) {
                    homeContentsVO.setIsMark(mark.getIsMark());
                }

                //查询帖子的马住人数
                QueryWrapper<Mark> markQueryWrapper2 = new QueryWrapper<>();
                markQueryWrapper2
                        .eq("is_mark", 1)
                        .eq("content_id", homeContentsVO.getContentId())
                        .orderByAsc("create_time");
                List<Mark> mark2 = markMapper.selectList(markQueryWrapper2);
                if (mark2 != null) {
                    homeContentsVO.setMarkCount(mark2.size());
                    if (mark2.size() > 0) {
                        User markUser = userMapper.selectById(mark2.get(0).getUserId());
                        String markNickName = markUser.getNickName();
                        //处理匿名贴
                        if (homeContentsVO.getContentType() == 3 || homeContentsVO.getIsSpecial() == 1 || homeContentsVO.getIsSpecial() == 4) { //匿名贴处理
                            HomeContentVO markUserContentVo = new HomeContentVO();
                            markUserContentVo.setContentId(homeContentsVO.getContentId());
                            markUserContentVo.setUserId(markUser.getUserId());
                            markUserContentVo = dealAnonymous(markUserContentVo);
                            markNickName = markUserContentVo.getNickName();
                        }
                        homeContentsVO.setMarkNickName(markNickName);

                    }
                }

                //获取发帖者的认证状态
                int currentUserState = userService.getUserStateBySchool(homeContentsVO.getUserId(), homeContentsVO.getSchoolId()).getInteger("userState");
                homeContentsVO.setUserState(currentUserState);
                //处理匿名贴
                if (homeContentsVO.getContentType() == 3 || homeContentsVO.getIsSpecial() == 1 || homeContentsVO.getIsSpecial() == 4) { //匿名贴处理
                    homeContentsVO = dealAnonymous(homeContentsVO);
                }
                //获取板块名称
                QueryWrapper<Plate> plateQueryWrapper = new QueryWrapper<>();
                plateQueryWrapper.eq("plateId", homeContentsVO.getPlateId());
                Plate plate = plateMapper.selectOne(plateQueryWrapper);
                if (plate != null) {
                    homeContentsVO.setPlateName(plate.getName());
                }

                //获取帖子前两条主评论
                if (homeContentsVO.getCommentNum() > 0 && requestType.equals("timeMachine")) {
                    List<HomeCommentVO> mainComments = commentMapper.getMainCommentsByContentId(homeContentsVO.getContentId());
                    //匿名处理
                    if (homeContentsVO.getContentType() == 3 || homeContentsVO.getIsSpecial() == 1 || homeContentsVO.getIsSpecial() == 4) {
                        for (HomeCommentVO mainComment : mainComments) {
                            mainComment = hotCommentDealAnonymous(mainComment, homeContentsVO);
                        }
                    }
                    homeContentsVO.setHotComments(mainComments);

                }

            }
            return homeContentsVOList;
        }
    }

    //带有请求时间的查询首页帖子列表
    @Override
    public List<HomeContentVO> getHomeContentsWithQueryTimeUseRedis0511(String userId, String searchUserId, String plateId, int pageNum, int pageSize, int schoolId, String requestType, int contentType, int duration, String queryTime, int hideSecondHand) {
        //分页查询帖子信息及发帖人信息
        List<HomeContentVO> homeContentsVOList = null;
        if (requestType.equals("time")) {
            if (hideSecondHand == 1) {
                homeContentsVOList = contentMapper.getContentsPageFilterFAndS(schoolId, userId, plateId, (pageNum - 1) * pageSize, pageSize, contentType, queryTime);
            } else {
                homeContentsVOList = contentMapper.getContentsPageFilterFreeze(schoolId, userId, plateId, (pageNum - 1) * pageSize, pageSize, contentType, queryTime);
            }
        }
        if (null == homeContentsVOList) {
            return null;
        } else {
            //处理帖子点赞和码住相关信息
            homeContentsVOList= handleContentAdmireAndMarkInfo(homeContentsVOList, userId);
            //处理帖子其他信息
            for (HomeContentVO homeContentsVO : homeContentsVOList) {
                //判断当前帖子是否为投票贴
                if (homeContentsVO.getContentType() == 1) {
                    homeContentsVO.setVotes(voteService.getVotesByContentId(homeContentsVO.getContentId()));
                    //查询该用户是否投票
                    QueryWrapper<VoteResult> voteResultQueryWrapper = new QueryWrapper<>();
                    voteResultQueryWrapper
                            .eq("contentId", homeContentsVO.getContentId())
                            .eq("userId", userId)
                    ;
                    List<VoteResult> voteList = voteResultMapper.selectList(voteResultQueryWrapper);
                    if (voteList.size() != 0) {
                        homeContentsVO.setDoVote("voted!");
                    }

                }
                //判断当前帖子是否为信息集合贴
                if (homeContentsVO.getContentType() == 4 || homeContentsVO.getIsSpecial() == 2) {
                    QueryWrapper<SchoolInfo> schoolInfoQueryWrapper = new QueryWrapper<>();
                    schoolInfoQueryWrapper.eq("contentId", homeContentsVO.getContentId());
                    List<SchoolInfo> schoolInfoList = schoolInfoMapper.selectList(schoolInfoQueryWrapper);
                    homeContentsVO.setSchoolInfos(schoolInfoList);
                }

                //处理匿名贴
                if (homeContentsVO.getContentType() == 3 || homeContentsVO.getIsSpecial() == 1 || homeContentsVO.getIsSpecial() == 4) { //匿名贴处理
                    homeContentsVO = dealAnonymous(homeContentsVO);
                }
                //获取发帖者的认证状态，当前统一为2（仅认证后才可发帖）
                homeContentsVO.setUserState(2);
                //设置板块名称，默认板块不需要请求数据库
                if (homeContentsVO.getPlateId().equals("")) {
                    homeContentsVO.setPlateName("校园广场");
                } else {
                    //使用redis获取板块名称，过期时间600s
                    Plate plate = redisService.getPlateByIdUseRedis(homeContentsVO.getPlateId(),600);
                    if (plate != null) {
                        homeContentsVO.setPlateName(plate.getName());
                    }
                }

                //获取帖子前两条主评论
                if (homeContentsVO.getCommentNum() > 0 && requestType.equals("time")) {
                    List<HomeCommentVO> mainComments = commentMapper.getMainCommentsByContentId(homeContentsVO.getContentId());
                    //匿名处理
                    if (homeContentsVO.getContentType() == 3 || homeContentsVO.getIsSpecial() == 1 || homeContentsVO.getIsSpecial() == 4) {
                        for (HomeCommentVO mainComment : mainComments) {
                            mainComment = hotCommentDealAnonymous(mainComment, homeContentsVO);
                        }
                    }
                    homeContentsVO.setHotComments(mainComments);

                }

            }
            return homeContentsVOList;
        }
    }

    @Override
    public List<HomeContentVO> getAllHomeContents(String userId, int pageNum, int pageSize, int schoolId) {
        //分页查询帖子信息及发帖人信息
        List<HomeContentVO> homeContentsVOList = null;
        homeContentsVOList = contentMapper.getAllContentsPage(schoolId, userId,  (pageNum - 1) * pageSize, pageSize);
        if (null == homeContentsVOList) {
            return null;
        } else {
            //处理帖子点赞和码住相关信息
            homeContentsVOList = handleContentAdmireAndMarkInfo(homeContentsVOList, userId);
            //处理帖子其他信息
            for (HomeContentVO homeContentsVO : homeContentsVOList) {
                //判断当前帖子是否为投票贴
                if (homeContentsVO.getContentType() == 1) {
                    homeContentsVO.setVotes(voteService.getVotesByContentId(homeContentsVO.getContentId()));
                    //查询该用户是否投票
                    QueryWrapper<VoteResult> voteResultQueryWrapper = new QueryWrapper<>();
                    voteResultQueryWrapper
                            .eq("contentId", homeContentsVO.getContentId())
                            .eq("userId", userId)
                    ;
                    List<VoteResult> voteList = voteResultMapper.selectList(voteResultQueryWrapper);
                    if (voteList.size() != 0) {
                        homeContentsVO.setDoVote("voted!");
                    }

                }
                //判断当前帖子是否为信息集合贴
                if (homeContentsVO.getContentType() == 4 || homeContentsVO.getIsSpecial() == 2) {
                    QueryWrapper<SchoolInfo> schoolInfoQueryWrapper = new QueryWrapper<>();
                    schoolInfoQueryWrapper.eq("contentId", homeContentsVO.getContentId());
                    List<SchoolInfo> schoolInfoList = schoolInfoMapper.selectList(schoolInfoQueryWrapper);
                    homeContentsVO.setSchoolInfos(schoolInfoList);
                }

                //处理匿名贴
                if (homeContentsVO.getContentType() == 3 || homeContentsVO.getIsSpecial() == 1 || homeContentsVO.getIsSpecial() == 4) { //匿名贴处理
                    homeContentsVO = dealAnonymous(homeContentsVO);
                }
                //获取发帖者的认证状态，当前统一为2（仅认证后才可发帖）
                homeContentsVO.setUserState(2);
                //设置板块名称，默认板块不需要请求数据库
                if (homeContentsVO.getPlateId().equals("") ) {
                    homeContentsVO.setPlateName("校园广场");
                } else {
                    //使用redis获取板块名称，过期时间600s
                    Plate plate = plateMapper.selectById(homeContentsVO.getPlateId());
                    if (plate != null) {
                        homeContentsVO.setPlateName(plate.getName());
                    }
                }

            }
            return homeContentsVOList;
        }
    }

    private List<HomeContentVO> handleContentAdmireAndMarkInfo( List<HomeContentVO> homeContentsVOList, String userId){
        //建立一个contentId的数组
        String[] contentIdList = homeContentsVOList.stream()
                .map(HomeContentVO::getContentId)
                .toArray(String[]::new);
        //获取该用户所有点赞记录
        QueryWrapper<Admire> likeQueryWrapper = new QueryWrapper<>();
        likeQueryWrapper
                .eq("userId", userId)
                .eq("isContent", 1)
                .in("targetId", contentIdList);
        List<Admire> admireList = admireMapper.selectList(likeQueryWrapper);
        //获取该用户所有马住记录
        QueryWrapper<Mark> markQueryWrapper = new QueryWrapper<>();
        markQueryWrapper
                .eq("user_id", userId)
                .in("content_id", contentIdList);
        List<Mark> userMarkList = markMapper.selectList(markQueryWrapper);
        //获取所有帖子的马住记录
        QueryWrapper<Mark> markQueryWrapper2 = new QueryWrapper<>();
        markQueryWrapper2
                .eq("is_mark", 1)
                .in("content_id", contentIdList)
                .orderByAsc("create_time");
        List<Mark> markList = markMapper.selectList(markQueryWrapper2);

        for (HomeContentVO homeContentsVO : homeContentsVOList) {
            //根据帖子id判断当前帖子是否点赞
            for (int i = 0; i < admireList.size(); i++) {
                if (Objects.equals(homeContentsVO.getContentId(), admireList.get(i).getTargetId())) {
                    homeContentsVO.setIsLike(admireList.get(i).getIsLike());
                    break;
                }
            }
            //根据帖子id判断当前帖子是否马住
            for (int i = 0; i < userMarkList.size(); i++) {
                if (Objects.equals(homeContentsVO.getContentId(), userMarkList.get(i).getContentId())) {
                    homeContentsVO.setIsMark(userMarkList.get(i).getIsMark());
                    break;
                }
            }
            //根据帖子id判断当前帖子的马住人数
            String currentContentId = homeContentsVO.getContentId();
            int markCount = (int) markList.stream()
                    .filter(Mark -> Mark.getContentId().equals(currentContentId))
                    .count();
            homeContentsVO.setMarkCount(markCount);
            //如果有人在蹲，获取一个mark的用户昵称
            if (markCount > 0) {
                for (int i = 0; i < markList.size(); i++) {
                    if (Objects.equals(homeContentsVO.getContentId(), markList.get(i).getContentId())) {
                        User markUser = userService.getUserByUserId(markList.get(i).getUserId());
                        String markNickName = markUser.getNickName();
                        //处理匿名贴情况
                        if (homeContentsVO.getContentType() == 3 || homeContentsVO.getIsSpecial() == 1 || homeContentsVO.getIsSpecial() == 4) {
                            HomeContentVO markUserContentVo = new HomeContentVO();
                            markUserContentVo.setContentId(homeContentsVO.getContentId());
                            markUserContentVo.setUserId(markUser.getUserId());
                            markUserContentVo = dealAnonymous(markUserContentVo);
                            markNickName = markUserContentVo.getNickName();
                        }
                        homeContentsVO.setMarkNickName(markNickName);
                        break;
                    }
                }
            }
        }
        return homeContentsVOList;
    }

    @Override
    public List<HomeContentVO> searchContentsByContentType(String userId, int pageNum, int pageSize, int schoolId, int contentType, String keywords, String queryTime) {
        //分页查询帖子信息及发帖人信息
        List<HomeContentVO> homeContentsVOList = null;
        homeContentsVOList = contentMapper.searchContentsPageByContentType(schoolId, userId, pageSize, contentType, queryTime, keywords);
        if (null == homeContentsVOList) {
            return null;
        } else {
            //建立一个contentId的数组
            String[] contentIdList = homeContentsVOList.stream()
                    .map(HomeContentVO::getContentId)
                    .toArray(String[]::new);
            //获取该用户所有点赞记录
            QueryWrapper<Admire> likeQueryWrapper = new QueryWrapper<>();
            likeQueryWrapper
                    .eq("userId", userId)
                    .eq("isContent", 1)
                    .in("targetId", contentIdList);
            List<Admire> admireList = admireMapper.selectList(likeQueryWrapper);
            //获取该用户所有马住记录
            QueryWrapper<Mark> markQueryWrapper = new QueryWrapper<>();
            markQueryWrapper
                    .eq("user_id", userId)
                    .in("content_id", contentIdList);
            List<Mark> userMarkList = markMapper.selectList(markQueryWrapper);
            //获取所有帖子的马住记录
            QueryWrapper<Mark> markQueryWrapper2 = new QueryWrapper<>();
            markQueryWrapper2
                    .eq("is_mark", 1)
                    .in("content_id", contentIdList)
                    .orderByAsc("create_time");
            List<Mark> markList = markMapper.selectList(markQueryWrapper2);


            for (HomeContentVO homeContentsVO : homeContentsVOList) {
                //判断当前帖子是否为投票贴
                if (homeContentsVO.getContentType() == 1) {
                    homeContentsVO.setVotes(voteService.getVotesByContentId(homeContentsVO.getContentId()));
                    //查询该用户是否投票
                    QueryWrapper<VoteResult> voteResultQueryWrapper = new QueryWrapper<>();
                    voteResultQueryWrapper
                            .eq("contentId", homeContentsVO.getContentId())
                            .eq("userId", userId)
                    ;
                    List<VoteResult> voteList = voteResultMapper.selectList(voteResultQueryWrapper);
                    if (voteList.size() != 0) {
                        homeContentsVO.setDoVote("voted!");
                    }

                }
                //根据帖子id判断当前帖子是否点赞
                for (int i = 0; i < admireList.size(); i++) {
                    if (Objects.equals(homeContentsVO.getContentId(), admireList.get(i).getTargetId())) {
                        homeContentsVO.setIsLike(admireList.get(i).getIsLike());
                        break;
                    }
                }
                //根据帖子id判断当前帖子是否马住
                for (int i = 0; i < userMarkList.size(); i++) {
                    if (Objects.equals(homeContentsVO.getContentId(), userMarkList.get(i).getContentId())) {
                        homeContentsVO.setIsMark(userMarkList.get(i).getIsMark());
                        break;
                    }
                }
                //根据帖子id判断当前帖子的马住人数
                String currentContentId = homeContentsVO.getContentId();
                int markCount = (int) markList.stream()
                        .filter(Mark -> Mark.getContentId().equals(currentContentId))
                        .count();
                homeContentsVO.setMarkCount(markCount);
                //如果有人在蹲，获取一个mark的用户昵称
                if (markCount > 0) {
                    for (int i = 0; i < markList.size(); i++) {
                        if (Objects.equals(homeContentsVO.getContentId(), markList.get(i).getContentId())) {
                            User markUser = userMapper.selectById(markList.get(i).getUserId());
                            String markNickName = markUser.getNickName();
                            //处理匿名贴情况
                            if (homeContentsVO.getContentType() == 3 || homeContentsVO.getIsSpecial() == 1 || homeContentsVO.getIsSpecial() == 4) {
                                HomeContentVO markUserContentVo = new HomeContentVO();
                                markUserContentVo.setContentId(homeContentsVO.getContentId());
                                markUserContentVo.setUserId(markUser.getUserId());
                                markUserContentVo = dealAnonymous(markUserContentVo);
                                markNickName = markUserContentVo.getNickName();
                            }
                            homeContentsVO.setMarkNickName(markNickName);
                            break;
                        }
                    }
                }

                //处理匿名贴
                if (homeContentsVO.getContentType() == 3 || homeContentsVO.getIsSpecial() == 1 || homeContentsVO.getIsSpecial() == 4) { //匿名贴处理
                    homeContentsVO = dealAnonymous(homeContentsVO);
                }
                //获取发帖者的认证状态，当前统一为2（仅认证后才可发帖）
                homeContentsVO.setUserState(2);
                //设置板块名称，默认板块不需要请求数据库
                if (homeContentsVO.getPlateId().equals("")) {
                    homeContentsVO.setPlateName("校园广场");
                } else {
                    //使用redis获取板块名称，过期时间600s
                    Plate plate = plateMapper.selectById(homeContentsVO.getPlateId());
                    if (plate != null) {
                        homeContentsVO.setPlateName(plate.getName());
                    }
                }
            }
            return homeContentsVOList;
        }

    }

    //根据传入的帖子id列表返回图片
    @Override
    public JSONArray getImageBase64ByContentIds(String ContentIds) {
        String[] contentIdList = ContentIds.split(",");
        JSONArray imageBase64List = new JSONArray();
        for (String contentId : contentIdList) {
            //获取每个帖子信息
            Content content = contentMapper.selectById(contentId);
            if (!content.getContentUrl().isEmpty()) {
                //帖子的图片列表
                String[] imageUrlList = content.getContentUrl().split(",");
                if (imageUrlList.length > 0) {
                    //每个图片获取对应的base64
                    for (String imageUrl : imageUrlList) {
                        String imageBase64 = getImageBase64FromUrl(imageUrl);
                        if (imageBase64 != null) {
                            //构建一个contentId+base64的结构体
                            JSONObject contentBase64 = new JSONObject();
                            contentBase64.put("contentId", contentId);
                            contentBase64.put("contentBase64", imageBase64);
                            imageBase64List.add(contentBase64);
                        }

                    }
                }
            }
        }
        return imageBase64List;
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

    //根据sql请求到的首页列表返回图片base64
    @Override
    public JSONArray getImageBase64WithQueryTime(int pageNum, int pageSize, int schoolId, int contentType, String queryTime, String plateId) {
        List<Content> contentsVOList = null;
        JSONArray imageBase64List = new JSONArray();
        contentsVOList = contentMapper.getImageBase64WithQueryTime(schoolId, plateId, (pageNum - 1) * pageSize, pageSize, contentType, queryTime);
        for (Content content : contentsVOList) {
            //如果该帖子无图片，返回的是一个base64为""的结构
            if (content.getContentUrl() == null || content.getContentUrl().isEmpty()) {
                //构建一个contentId+base64的结构体，并填入返回数组
                JSONObject contentBase64 = new JSONObject();
                contentBase64.put("contentId", content.getContentId());
                contentBase64.put("base64", "");
                imageBase64List.add(contentBase64);
            } else {
                //帖子的图片列表
                String[] imageUrlList = content.getContentUrl().split(",");
                if (imageUrlList.length > 0) {
                    //每个图片获取对应的base64
                    for (String imageUrl : imageUrlList) {
                        String imageBase64 = getImageBase64FromUrl(imageUrl);
                        if (imageBase64 != null) {
                            //构建一个contentId+base64的结构体，并填入返回数组
                            JSONObject contentBase64 = new JSONObject();
                            contentBase64.put("contentId", content.getContentId());
                            contentBase64.put("base64", imageBase64);
                            imageBase64List.add(contentBase64);
                        }

                    }
                }
            }
        }

        return imageBase64List;
    }

    @Override
    public List<HomeContentVO> getFocusContentPage(String userId, int schoolId, String queryTime, int pageSize) {
        //分页查询帖子信息及发帖人信息
        List<HomeContentVO> homeContentsVOList = null;
        //获取该用户的关注列表
        QueryWrapper<Focus> followQueryWrapper = new QueryWrapper<>();
        followQueryWrapper.eq("userId",userId);
        followQueryWrapper.eq("state",1);
        List<Focus> focusList = followMapper.selectList(followQueryWrapper);
        List<String> focusUserIdList = focusList.stream()
                .map(Focus::getTargetId) // 提取 targetId
                .collect(Collectors.toList()); // 转为 List 集合
        //查询关注人发布的帖子列表
        homeContentsVOList =contentMapper.getFocusContentPage(focusUserIdList,schoolId,queryTime,pageSize);
        //处理帖子
        if (null == homeContentsVOList) {
            return null;
        } else {
            //处理帖子点赞和码住相关信息
            homeContentsVOList= handleContentAdmireAndMarkInfo(homeContentsVOList, userId);
            //处理帖子其他信息
            for (HomeContentVO homeContentsVO : homeContentsVOList) {
                //判断当前帖子是否为投票贴
                if (homeContentsVO.getContentType() == 1) {
                    homeContentsVO.setVotes(voteService.getVotesByContentId(homeContentsVO.getContentId()));
                    //查询该用户是否投票
                    QueryWrapper<VoteResult> voteResultQueryWrapper = new QueryWrapper<>();
                    voteResultQueryWrapper
                            .eq("contentId", homeContentsVO.getContentId())
                            .eq("userId", userId)
                    ;
                    List<VoteResult> voteList = voteResultMapper.selectList(voteResultQueryWrapper);
                    if (voteList.size() != 0) {
                        homeContentsVO.setDoVote("voted!");
                    }

                }

                //获取发帖者的认证状态，当前统一为2（仅认证后才可发帖）
                homeContentsVO.setUserState(2);
                //设置板块名称，默认板块不需要请求数据库
                if (homeContentsVO.getPlateId().equals("")) {
                    homeContentsVO.setPlateName("校园广场");
                } else {
                    //使用redis获取板块名称，过期时间600s
                    Plate plate = plateMapper.selectById(homeContentsVO.getPlateId());
                    if (plate != null) {
                        homeContentsVO.setPlateName(plate.getName());
                    }
                }

                //获取帖子前两条主评论
                if (homeContentsVO.getCommentNum() > 0) {
                    List<HomeCommentVO> mainComments = commentMapper.getMainCommentsByContentId(homeContentsVO.getContentId());
                    homeContentsVO.setHotComments(mainComments);

                }

            }
            return homeContentsVOList;
        }
    }

    private HomeCommentVO hotCommentDealAnonymous(HomeCommentVO hotComment, HomeContentVO content) {
        HomeCommentVO commentVO = hotComment;
        //根据用户ID和帖子ID，生成该帖子下的唯一用户昵称
        //String newName = "隐士#";
        String newName = getChineseName(content.getContentId(), commentVO.getUserId());
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
        String headimgUrl = "https://image.test.zone/" + String.valueOf(imageId) +
                ".png";
        if (content.getSchoolId()==9999){
            imageId = imageId % 34;
            headimgUrl = "https://image.test.zone/anonymous/" + String.valueOf(imageId) +
                    ".jpg";
        }
        commentVO.setHeadimgUrl(headimgUrl);
        //设置昵称最后的编码
        Integer preNum = idToNum(content.getContentId());
        Integer sufNum = idToNum(commentVO.getUserId());
        Integer total = preNum + sufNum;
        newName = newName + total;
        commentVO.setNickName(newName);
        return commentVO;
    }

}
