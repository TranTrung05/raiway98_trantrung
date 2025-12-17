package com.vti;

import java.util.Scanner;

public class program10 {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
//		for (int i = 10 ; i > 0 ; i = i -1) {
//			System.out.println("So : " + i);
//		}

		System.out.println("Thong tin cac ban hoc vien trong lop ");
		String[] namerw98 = { "Tien", "Nam", "Trung", "Quynh", "Lam", "Trinh" };
		for (int i = 0; i < namerw98.length; i = i + 1) {
			System.out.println(namerw98[i]);
		}
	}
}
