package com.example.bbs.pojo.vo;

import com.example.bbs.pojo.SchoolInfo;
import com.example.bbs.pojo.Vote;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.json.JSONObject;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class HomeContentVO implements Serializable {
    private String contentId;
    private String userId;

    private String title;
    private String contentText;
    private String contentUrl;
    private int upNum;
    private int downNum; //点踩数
    private int commentNum;
    private int readNum;  //阅读量
    private int realReadNum;  //真实读量
    private int contentType;
    private int contentState;
    private int noComment; //不可评论状态
    private int alumniOnly;
    private int schoolId;
    private int isSpecial; //特殊类型标识
    private int visibleRange;
    private Date createTime;
    private String place;
    private String plateId;
    private String plateName;
    private String name;

    private List<Vote> votes;
    private List<SchoolInfo> schoolInfos;

    private String nickName;
    private String headimgUrl;
    private String introduction;

    private String doVote;//标识用户是否已进行过投票，空则未投票
    private int isLike;
    private int isMark;
    private int markCount;
    private String markNickName;

    //标识用户认证状态
    private int userState = 0;

    //网文社身份标识
    private int userIdentify;

    private List<HomeCommentVO>  hotComments = new ArrayList<>() ;

    //模糊搜索中重复帖子个数
    private int rowNumber;

    //是否展示图片
    private int showImage = 0;
}
