package com.vti.frontend;

import java.util.Scanner;

public class program7 {
	public static void main(String[] args) {
		System.out.println("-------Chương trình tính tổng 2 số -------");
		System.out.println("nhập số thứ nhất : ");
		// nhập liệu
		Scanner scanner = new Scanner(System.in);
		int number1 = scanner.nextInt();
		System.out.println("số bạn vừa nhập vào là : " + number1);

		System.out.println("nhập số thứ hai : ");
		int number2 = scanner.nextInt();
		System.out.println("số bạn vừa nhập vào là : " + number2);

		int result = number1 + number2;
		System.out.println("KQ là  : " + result);
		// in ra thông tin tên + tuôi ng dùng
		System.out.println("Mời bạn nhập tên : ");
		String name = scanner.next(); // Tran Duy Trung => Tran
		// lấy đầy đủ
		String name = scanner.nextLine();
		System.out.println("Name :  " + name);
		scanner.close();// đóng scanner ==> tránh rò rỉ bộ nhớ

	}
}
