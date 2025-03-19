package com.example.v2.test;

import java.util.Arrays;
import java.util.regex.Pattern;

public class EmailValidatorTest {
	public static void main(String[] args) {
		EmailValidatorTest tester = new EmailValidatorTest();

		// 測試案例1：有效的email地址
		System.out.println("\n=== 測試案例1：有效的email地址 ===");
		String[] validEmails = { "test@example.com", "user.name@domain.com", "user+label@domain.com" };
		tester.testEmailValidation(validEmails);

		// 測試案例2：無效的email地址
		System.out.println("\n=== 測試案例2：無效的email地址 ===");
		String[] invalidEmails = { "invalid.email", "@nodomain.com", "noat.domain.com", "spaces in@domain.com" };
		tester.testEmailValidation(invalidEmails);

		// 測試案例3：混合的email地址
		System.out.println("\n=== 測試案例3：混合的email地址 ===");
		String[] mixedEmails = { "valid@domain.com", "invalid.email", "another.valid@domain.com" };
		tester.testEmailValidation(mixedEmails);
	}

	private boolean validateEmailAddresses(String[] addressArrays) {
		System.out.println("開始驗證email地址陣列");

		// 定義email格式的正則表達式
		String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
		System.out.println("使用的正則表達式模式：" + emailRegex);

		// 遍歷檢查每個email地址
		for (String addresses : addressArrays) {
			System.out.println("\n檢查email地址：" + addresses);

			// 使用Pattern.matches進行驗證
			boolean isValid = Pattern.matches(emailRegex, addresses);
			System.out.println("驗證結果：" + (isValid ? "有效" : "無效"));

			if (!isValid) {
				System.out.println("發現無效的email地址，停止驗證");
				return false;
			}
		}

		System.out.println("\n所有email地址驗證通過");
		return true;
	}

	private void testEmailValidation(String[] emails) {
		System.out.println("測試的email地址：" + Arrays.toString(emails));
		boolean result = validateEmailAddresses(emails);
		System.out.println("最終驗證結果：" + (result ? "全部有效" : "存在無效地址"));
		System.out.println("----------------------------------------");
	}
}