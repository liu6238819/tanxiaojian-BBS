package com.example.bbs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.bbs.pojo.Mark;
import com.example.bbs.pojo.Remind;
import com.example.bbs.pojo.vo.RemindVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface RemindMapper extends BaseMapper<Remind>{
   List<RemindVO> getRemindsPage(@Param("userId") String userId,@Param("queryType") String queryType, @Param("pageNum") int pageNum, @Param("pageSize") int pageSize);

   int selectNums(@Param("userId") String userId,@Param("queryType") String queryType);

   boolean saveBatchOfRemind(List<Remind> list);

   List<RemindVO> getRemindsPageDistinguishSchoolId0715(@Param("userId") String userId,@Param("queryType") String queryType, @Param("pageNum") int pageNum, @Param("pageSize") int pageSize);

   List<RemindVO> getRemindsPageDistinguishSchoolId0102(@Param("userId") String userId,@Param("queryType") String queryType, @Param("pageNum") int pageNum, @Param("pageSize") int pageSize);
}


