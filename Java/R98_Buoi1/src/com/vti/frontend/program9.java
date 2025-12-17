package com.vti.frontend;

import java.util.Scanner;

public class program9 {
	public static void main(String[] args) {

		Scanner scanner = new Scanner(System.in);
		int day = scanner.nextInt();
//		if (day == 1) {
//			System.out.println("Chu Nhat");
//		} else if (day == 2) {
//			System.out.println("Thu2");
//		} else if (day == 3) {
//			System.out.println("Thu3");
//		} else if (day == 4) {
//			System.out.println("Thu4");
//		} else if (day == 5) {
//			System.out.println("Thu5");
//		} else if (day == 6) {
//			System.out.println("Thu6");
//		} else if (day == 7) {
//			System.out.println("Thu7");
//		} else {
//			System.out.println("Khong hop le");
//		}
//		
		
		switch (day) {
		case 1: 
			System.out.println("Chu Nhat");
			break;
		case 2 :
			System.out.println("thu 2");
			break;
		case 3 : 
			System.out.println("thu 3");
			break;
		case 4 : 
			System.out.println("thu 4");
			break;
		case 5 : 
			System.out.println("thu 5");
			break;
		case 6 : 
			System.out.println("thu 6");
			break;
		case 7 : 
			System.out.println("thu 7");
			break;
		default : 
			System.out.println("khong hop le");
			break;
			
			
	}
}
