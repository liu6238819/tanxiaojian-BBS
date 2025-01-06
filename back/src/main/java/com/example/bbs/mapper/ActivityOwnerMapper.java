package com.example.bbs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.bbs.pojo.ActivityOwner;
import com.example.bbs.pojo.vo.SignUpActivityVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ActivityOwnerMapper extends BaseMapper<ActivityOwner> {
    List<ActivityOwner> selectActivityOwnerList(@Param("ownerId") String ownerId,  @Param("kind") int kind,@Param("state") int state, @Param("schoolId") int schoolId,  @Param("pageNum") int pageNum, @Param("pageSize") int pageSize);
}
