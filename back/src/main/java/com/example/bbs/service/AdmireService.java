package com.example.bbs.service;

import com.example.bbs.pojo.Admire;

public interface AdmireService {
    boolean isLikeByUserId(String userId,String targetId,int isContent);

    int editLikeNum(String userId, String targetId, int isContent, int isLike,StringBuilder message);

    String getPlateId(Admire admire);

}
