package com.example.bbs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.bbs.pojo.BlockRecord;
import com.example.bbs.pojo.vo.BlockRecordVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BlockRecordMapper extends BaseMapper<BlockRecord> {
    List<BlockRecordVO> getBlockRecordVOListByUserId(@Param("userId") String userId, @Param("pageNum") int pageNum, @Param("pageSize") int pageSize);
}
