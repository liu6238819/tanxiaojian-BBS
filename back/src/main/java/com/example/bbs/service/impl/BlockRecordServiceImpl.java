package com.example.bbs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.bbs.mapper.BlockRecordMapper;
import com.example.bbs.pojo.BlockRecord;
import com.example.bbs.pojo.vo.BlockRecordVO;
import com.example.bbs.service.BlockRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
public class BlockRecordServiceImpl implements BlockRecordService {


    @Autowired
    BlockRecordMapper blockRecordMapper;

    //拉黑操作
    @Override
    public int createBlockRecord(String userId, String targetId) {
        //查询数据库中是否已有记录
        QueryWrapper<BlockRecord> blockRecordQueryWrapper = new QueryWrapper<>();
        blockRecordQueryWrapper
                .eq("user_id", userId)
                .eq("target_id", targetId);
        List<BlockRecord>  oldBlockRecords = blockRecordMapper.selectList(blockRecordQueryWrapper);
        //查询出错
        if (oldBlockRecords==null){
            return 0;
        }
        //数据库中没有记录
        int result =0;
        if (oldBlockRecords.size()==0){
            BlockRecord newBlockRecord = new BlockRecord();
            String newUUID = UUID.randomUUID().toString().replace("-", "");
            Date nowDate = new Date();
            newBlockRecord.setId(newUUID);
            newBlockRecord.setUserId(userId);
            newBlockRecord.setTargetId(targetId);
            newBlockRecord.setTargetState(1);
            newBlockRecord.setCreateTime(nowDate);
            newBlockRecord.setUpdateTime(nowDate);
            result = blockRecordMapper.insert(newBlockRecord);

        }
        //数据库中存在记录
        else{
            BlockRecord updateBlockRecord = oldBlockRecords.get(0);
            updateBlockRecord.setTargetState(1);
            result = blockRecordMapper.updateById(updateBlockRecord);
        }
        return result;
    }

    //更新拉黑状态操作
    @Override
    public int updateBlockRecord(String id, int targetState) {
        //查询数据库中的记录
        BlockRecord blockRecord = blockRecordMapper.selectById(id);
        blockRecord.setTargetState(targetState);
        int result = blockRecordMapper.updateById(blockRecord);
        return result;
    }

    //获取拉黑记录（带昵称）
    @Override
    public List<BlockRecordVO> getUserBlockList(String userId, int pageNum,int pageSize) {
        List<BlockRecordVO> blockRecordVOList =new ArrayList<>();
        blockRecordVOList =blockRecordMapper.getBlockRecordVOListByUserId(userId,(pageNum-1)*pageSize,pageSize);
        return blockRecordVOList;
    }

    //判断是否被拉黑
    @Override
    public int judgeBeBlockedByUser(String userId, String searchUserId) {
        int haveBeBlocked =0 ;
        List<BlockRecord> blockRecordList =new ArrayList<>();
        QueryWrapper<BlockRecord> blockRecordQueryWrapper = new QueryWrapper<>();
        blockRecordQueryWrapper
                .eq("target_id", userId)
                .eq("user_id",searchUserId)
                .eq("target_state", 1);
        blockRecordList = blockRecordMapper.selectList(blockRecordQueryWrapper);
        if (blockRecordList.size()==0){
            haveBeBlocked =0;
        }
        else{
            haveBeBlocked =1;
        }
        return haveBeBlocked;
    }

    @Override
    public int updateBlockRecordByUserId(String userId, String targetId, int targetState) {
        //查询数据库中的记录
        List<BlockRecord> blockRecordList =new ArrayList<>();
        QueryWrapper<BlockRecord> blockRecordQueryWrapper = new QueryWrapper<>();
        blockRecordQueryWrapper
                .eq("target_id", targetId)
                .eq("user_id",userId);
        blockRecordList = blockRecordMapper.selectList(blockRecordQueryWrapper);
        BlockRecord blockRecord =new BlockRecord();
        if (blockRecordList.size()>0){
            blockRecord = blockRecordList.get(0);
            blockRecord.setTargetState(targetState);
            int result = blockRecordMapper.updateById(blockRecord);
            return result;
        }
        else{
            return 0;
        }

    }

}
