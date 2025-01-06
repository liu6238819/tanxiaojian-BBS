package com.example.bbs.service;

public interface GroupMemberService {
    Boolean needShowPopup(String groupType, int schoolId , String unionId);
    Boolean addNoRemindRecord(String userId, String nickName, int schoolId, String unionId);
}
