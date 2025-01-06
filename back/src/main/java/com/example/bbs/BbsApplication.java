package com.example.bbs;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.TimeZone;

@EnableSwagger2
@SpringBootApplication
@EnableWebSocket
@MapperScan("com.example.bbs.mapper")
public class BbsApplication {

    public static void main(String[] args) {
        //spring项目设置时区，解决docker中时间不对的问题
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        SpringApplication.run(BbsApplication.class, args);
    }

    //websocket组件
    @Bean
    public ServerEndpointExporter serverEndpointExporter(){
        return new ServerEndpointExporter();
    }
}
