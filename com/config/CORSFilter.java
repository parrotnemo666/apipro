package com.config;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;

////CORS 過濾器類
//public class CORSFilter implements ContainerResponseFilter {
//	@Override
//	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
//			throws IOException {
//		responseContext.getHeaders().add("Access-Control-Allow-Origin", "0"
//				+ "*");
//		responseContext.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
//		responseContext.getHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
//	}
//}
