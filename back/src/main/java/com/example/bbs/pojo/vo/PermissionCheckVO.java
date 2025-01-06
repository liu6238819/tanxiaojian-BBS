package com.example.bbs.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PermissionCheckVO {
    private String plateId;

    private int schoolFlag; //标识权限开放类型，0校内外，1仅校内

    private String userId;

    private int schoolId;

}
