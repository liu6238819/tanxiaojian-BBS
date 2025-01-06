package com.example.bbs.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 功能描述：
 *
 * @Author: lyc
 * @Date: 2022/2/7 5:25 下午
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class EditPlateInfoVO {
    private String plateId;
    private Integer plateState;

}
