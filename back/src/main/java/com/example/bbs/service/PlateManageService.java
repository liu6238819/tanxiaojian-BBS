package com.example.bbs.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.bbs.pojo.Plate;
import com.example.bbs.pojo.User;
import com.example.bbs.pojo.vo.EditPlateInfoVO;
import com.example.bbs.pojo.vo.SearchPlateVO;
import com.example.bbs.pojo.vo.SearchUserVO;

/**
 * 功能描述：
 *
 * @Author: lyc
 * @Date: 2022/2/7 1:31 下午
 */
public interface PlateManageService {
    IPage<Plate> getSearchPlateList(SearchPlateVO searchPlateVO, int pageNum, int pageSize);
    Boolean judgeTokenValid(String token);
    Integer updatePlateInfo(EditPlateInfoVO editPlateInfoVO);
}
