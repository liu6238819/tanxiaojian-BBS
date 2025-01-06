package com.example.bbs.service;

import com.example.bbs.pojo.Cate;
import com.example.bbs.pojo.Plate;
import com.example.bbs.pojo.User_Plate;
import io.swagger.models.auth.In;

import java.util.List;

public interface PlateService {
    Plate createPlate(Plate plate);

    List<Cate> getClassLists();

    List<Plate> getPlateLists(Integer schoolId,Integer cateId);

    Plate getPlateInfo(String plateId);

    boolean joinPlate(User_Plate user_pLate);

    boolean leavePlate(User_Plate user_pLate);

    boolean deletePlate(User_Plate user_pLate);

    boolean isJoin(String plateId,String userId);

    List<Plate> getFocusedPlates(String userId,Integer schoolId);
}
