package com.example.bbs.service;


import com.example.bbs.pojo.BlockRecord;
import com.example.bbs.pojo.vo.BlockRecordVO;

import java.util.List;
import java.util.Map;

public interface BlockRecordService {
    int createBlockRecord(String userId ,String targetId);
    int updateBlockRecord(String id,int targetState);
    //用户拉黑别人的列表
    List<BlockRecordVO> getUserBlockList(String userId,int pageNum,int pageSize);

    int judgeBeBlockedByUser(String userId, String searchUserId);

    int updateBlockRecordByUserId(String userId ,String targetId,int targetState);

}
