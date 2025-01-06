package com.example.bbs.config;

import org.springframework.context.annotation.Bean;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.springframework.context.annotation.Configuration;
/**
 * 功能描述：
 *
 * @Author: lyc
 * @Date: 2022/1/11 8:21 下午
 */
@Configuration
public class MyBatisPlusConfig {
    //分页插件
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }
}
