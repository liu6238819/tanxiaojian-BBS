package com.example.bbs.pojo.vo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 功能描述：
 *
 * @Author: lyc
 * @Date: 2022/2/10 10:41 上午
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CommonSearchVO {
    Integer type;
    Integer mode;
    String condition;
}
