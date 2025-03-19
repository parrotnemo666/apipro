package com.other.test;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.AddressException;

public class InternetAddressExample {
    public static void main(String[] args) {
        try {
            // 創建一個簡單的 InternetAddress
            InternetAddress address1 = new InternetAddress("user@example.com");
            System.out.println("Simple address: " + address1);

            // 創建帶有個人名稱的 InternetAddress
            InternetAddress address2 = new InternetAddress("user@example.com", "John Doe");
            System.out.println("Address with personal name: " + address2);

            // 解析包含多個地址的字符串
            InternetAddress[] addresses = InternetAddress.parse("user1@example.com, user2@example.com");
            System.out.println("Parsed addresses: " + addresses.length);

            // 驗證地址
            InternetAddress.parse("invalid@email", true); // 將拋出異常

        } catch (AddressException e) {
            System.out.println("Invalid email address: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}