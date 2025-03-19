package com.other.test;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@Path("/test")
public class TestController {

    @POST
    @Path("/echo")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response echoJson(Map<String, Object> input) {
        // 創建一個新的 Map 來存儲響應數據
        Map<String, Object> response = new HashMap<>(input);
        
        // 添加一個新字段到響應中
        response.put("serverMessage", "Hello from server!");
        
        // 如果輸入中有 "name" 字段，則個性化響應
        if (input.containsKey("name")) {
            response.put("greeting", "Hello, " + input.get("name") + "!");
        }

        // 返回 JSON 響應
        return Response.ok(response).build();
    }
}