package com.config;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;



//這個和webxml只要一個就好
@ApplicationPath("/api2") // API 的根路徑，前面需要添加專案名稱
public class MyJerseyApplication extends ResourceConfig {

	public MyJerseyApplication() {
		// 自動掃描並註冊 com.example 包中的所有資源類
		packages("com.controller");
		// 掃描使用HTTP+自訂義狀態碼的controller
		packages("com.example.v2.controller");
		
//		 packages("com.example.v2.swagController");
	        
//	        // 註冊 OpenApiResource，用於生成 Swagger 文檔
//	        register(OpenApiResource.class);
	        
//	        // 註冊 CORS 過濾器  
//	        register(CORSFilter.class);  
	        }
}



// 進入swagger
// http://localhost:8087/apipro/api/openapi.json

//	        http://localhost:8087/apipro/swagger-ui/index.html

// 配置 Swagger
//	        OpenAPI oas = new OpenAPI();
//	        Info info = new Info()
//	            .title("您的 API 文檔")
//	            .version("1.0.0")
//	            .description("API 描述");
//	        oas.info(info);
//
//	        // 添加服務器配置，包含專案名
//	        Server server = new Server();
//	        server.setUrl("/apipro/api");
//	        server.setDescription("API 服務器");
//	        oas.addServersItem(server);
//
//	        // 設置 Swagger 屬性
//	        property("swagger.api.title", "您的 API 文檔");
//	        property("swagger.api.basepath", "/apipro/api");
