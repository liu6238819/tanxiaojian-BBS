package com.example.bbs.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 功能描述：
 *
 * @Author: lyc
 * @Date: 2022/2/7 3:59 下午
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SearchPlateVO {
    private String cateId;
    private String userId;
    private String school;
    private String name;
    private Integer plateState;
}
