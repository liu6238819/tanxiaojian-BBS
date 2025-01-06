package com.example.bbs.config;

import com.google.common.base.Predicates;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 功能描述：
 *
 * @Author: lyc
 * @Date: 2022/1/11 8:30 下午
 */
// localhost:8080/swagger-ui.html

@EnableSwagger2
@Configuration
public class Swagger2Config {

    @Bean
    public Docket webApiConfig(){
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("BBS")
                .apiInfo(webApiInfo())
                .pathMapping("/")
                .select()
                .apis(RequestHandlerSelectors.any())// 对所有api进行监控
                .paths(Predicates.not(PathSelectors.regex("/error.*")))
                .paths(PathSelectors.regex("/.*"))// 对根下所有路径进行监控
                .build();
    }

    private ApiInfo webApiInfo(){
        return new ApiInfoBuilder()
                .title("BBS")
                .description("BBS接口文档")
                .version("1.0")
                //.contact(new Contact("KuangShen", "", "773085545@qq.com"))
                .build();
    }
}
