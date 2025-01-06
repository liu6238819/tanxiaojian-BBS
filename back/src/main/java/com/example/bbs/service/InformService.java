package com.example.bbs.service;

public interface InformService {
    void addInformInfo(String userId, String targetId, int isContent, String informInfo);

    void addInformInfoMoreInfo(String userId,String informedUserId,String contentId,String targetText, String targetId, int isContent,String informInfo, String informUrl,int schoolId);

    int ifAlreadyInformThisTarget(String userId ,int schoolId, String targetId);
}
