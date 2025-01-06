package com.example.bbs.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RemindWithCountVO {
    int remindCount; //all时为总通知数，非all为对应类型通知数
    int admireCount; //点赞通知数
    int followCount; //关注通知数
    int replyCount;  //回复通知数
    List<RemindVO> data;
}
