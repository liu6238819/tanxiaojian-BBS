package com.example.bbs.service;

import com.example.bbs.pojo.OperationRecord;

import java.util.List;

public interface OperationRecordService {
    int addOperationRecord(OperationRecord operationRecord);
    long isReachBannedTime(String userId ,int schoolId);
    public List<OperationRecord> selectBannedList(int schoolId, String targetId);
}
