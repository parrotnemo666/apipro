//package com.other;
//
//import io.swagger.v3.jaxrs2.integration.JaxrsOpenApiContextBuilder;
//import io.swagger.v3.oas.integration.OpenApiConfigurationException;
//import io.swagger.v3.oas.models.OpenAPI;
//import io.swagger.v3.oas.models.info.Info;
//import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
//
//import javax.servlet.ServletConfig;
//import javax.ws.rs.core.Application;
//import javax.ws.rs.core.Context;
//import javax.annotation.PostConstruct;
//import java.util.Set;
//
//public class SwaggerConfig extends Application {
//
//    @Context
//    ServletConfig servletConfig;
//
//    @PostConstruct
//    public void init() {
//        OpenAPI oas = new OpenAPI();
//        Info info = new Info()
//            .title("用戶管理 API")
//            .description("這是一個簡單的用戶管理 API")
//            .version("1.0.0");
//        oas.info(info);
//
//        // 設置 OpenAPI 配置
//        io.swagger.v3.oas.integration.SwaggerConfiguration oasConfig = new io.swagger.v3.oas.integration.SwaggerConfiguration()
//            .openAPI(oas)
//            .prettyPrint(true)
//            .resourcePackages(Set.of("com.example.aes.controller")); // 替換為您的控制器包名
//
//        try {
//            new JaxrsOpenApiContextBuilder()
//                .servletConfig(servletConfig)
//                .application(this)
//                .openApiConfiguration(oasConfig)
//                .buildContext(true);
//        } catch (OpenApiConfigurationException e) {
//            throw new RuntimeException(e.getMessage(), e);
//        }
//    }
//
//    @Override
//    public Set<Class<?>> getClasses() {
//        return Set.of(OpenApiResource.class);
//    }
//}