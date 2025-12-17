package com.vti.frontend;

import java.util.Scanner;

public class pogram8 {
	public static void main(String[] args) {
		System.out.println("nhập số thứ nhất : ");
		// nhập liệu
		Scanner scanner = new Scanner(System.in);
		int number1 = scanner.nextInt();
		System.out.println("số bạn vừa nhập vào là : " + number1);

		System.out.println("nhập số thứ hai : ");
		int number2 = scanner.nextInt();
		System.out.println("số bạn vừa nhập vào là : " + number2);

		if (number1 > number2) {
			System.out.println("number 1 lớn hơn number 2");
		} else if (number1 == number2) {
			System.out.println("number 1 bằng number 2");
		}else {
			System.out.println("number 1 nhỏ hơn number 2");
		}
		
		System.out.println("Mời bạn nhập tuổi ");
		int age = scanner.nextInt();
		if (age > 0 ) {
			System.out.println("thoong tin chinh xac"); 
		}else {
			System.out.println("thoong tin k chinh xac"); 
		}
		// ternary : toan tu 3 ngoi : dieu_kien ? gia_tri_dung : gia_tri_sai
		String ageInfo = (age > 0 ) ? "thong tin chinh xac " : "thong tin k chinh xac "
	}
}
