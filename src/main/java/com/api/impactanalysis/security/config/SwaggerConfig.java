package com.api.impactanalysis.security.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.api.impactanalysis.common.Constants;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@Configuration
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2).select().apis(RequestHandlerSelectors.any()).paths(
                PathSelectors.ant(Constants.API_ROOT_URL)).build().pathMapping("/").apiInfo(metaData()).securitySchemes(securitySchemes());
    }

    private ApiInfo metaData() {
        Contact contact = new Contact("Muhammad Azhar Naeem", "https://www.linkedin.com/in/azharrnaeem", "azharrnaeem@gmail.com");
        return new ApiInfo("Covid Impact Analysis API", "", "1.0", "", contact, "", "", new ArrayList<>());
    }

    private List<? extends SecurityScheme> securitySchemes() {
        return Arrays.asList(new ApiKey("Bearer", "Authorization", "header"));
    }
}
