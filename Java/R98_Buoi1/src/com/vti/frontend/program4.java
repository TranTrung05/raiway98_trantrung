package com.vti.frontend;

import com.vti.entity.student;

public class program4 {
	// tao ra cac ban hoc vien trong chuong trinh
	// sinh vien 1
	public static void main(String[] args) {
		student student1 = new student();
		student1.name = "Trung";
		student1.age = 19;
		// sinh vien 2
		student student2 = new student();
		student2.name = "Quynh";
		student2.age = 20;
		// sinh vien 3
		student student3 = new student();
		student3.name = "Viet Anh";
		student3.age = 21;

		System.out.println("Thong tin hoc vien 1 , name : " + student1.name + ", age : " + student1.age);
		System.out.println("Thong tin hoc vien 2 , name : " + student2.name + ", age : " + student2.age);
		System.out.println("Thong tin hoc vien 3 , name : " + student3.name + ", age : " + student3.age);

		System.out.println("------------------");
		student1.gotoVTI();
		student2.gotoVTI();
		student3.gotoVTI();

	}

}
