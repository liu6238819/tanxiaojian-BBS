package com.example.bbs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.bbs.pojo.UserRecord;
import org.apache.ibatis.annotations.Param;

public interface UserRecordMapper extends BaseMapper<UserRecord> {
    UserRecord getUserRecordByDate(@Param("userId") String userId, @Param("schoolId") int schoolId, @Param("date") String date);
}
