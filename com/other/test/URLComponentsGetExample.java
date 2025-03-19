package com.other.test;
import java.net.URL;
import java.net.MalformedURLException;

public class URLComponentsGetExample {
    public static void main(String[] args) {
        try {
            URL url = new URL("https://www.example.com:8080/path/to/resource?param1=value1#section");

            System.out.println("完整 URL: " + url.toString());
            System.out.println("協議: " + url.getProtocol());
            System.out.println("主機名: " + url.getHost());
            System.out.println("端口: " + url.getPort());
            System.out.println("路徑: " + url.getPath());
            System.out.println("查詢字符串: " + url.getQuery());
            System.out.println("片段: " + url.getRef());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}