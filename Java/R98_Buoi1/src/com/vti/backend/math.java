package com.vti.backend;

public class math {
	// tinh tong
	public static void sum() {
		System.out.println("nhap vao so thu 1 : ");
		int a = 5;
		System.out.println("nhap vao so thu 2 : ");
		int b = 10;
		int tong = a + b;
		System.out.println("tong cua 2 so la : " + tong);
	}

	// tinh hieu
	public static void minus() {
		System.out.println("nhap vao so thu 1 : ");
		int a = 5;
		System.out.println("nhap vao so thu 2 : ");
		int b = 10;
		int hieu = a - b;
		System.out.println("hieu cua 2 so la : " + hieu);
	}

	// tinh tich
	public static int multiply(int number1, int number2) {
		int tich = number1 * number2;
		return tich;
	}
}
