package com.vti.entity;

import java.text.SimpleDateFormat;
import java.time.LocalDate;

public class Testing_System_2 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// tao ra doi tuong Department
		Department department1 = new Department();
		department1.id = 1;
		department1.name = "Marketing";

		Department department2 = new Department();
		department2.id = 2;
		department2.name = "Sale";

		Department department3 = new Department();
		department3.id = 3;
		department3.name = "BOD";

		// Tao ra đối tượng Position
		Position position1 = new Position();
		position1.id = 1;
		position1.name = PositionName.Dev;

		Position position2 = new Position();
		position2.id = 2;
		position2.name = PositionName.PM;

		Position position3 = new Position();
		position3.id = 2;
		position3.name = PositionName.Scrum_Master;

		// Tao ra doi tuong Account
		Account account1 = new Account();
		account1.id = 1;
		account1.email = "trungtd@gmail.com";
		account1.username = "trungtd";
		account1.fullname = "Trần Duy Trung";
		account1.department = department1;
		account1.position = position1;
		account1.CreateDate = LocalDate.now();

		Account account2 = new Account();
		account2.id = 2;
		account2.email = "lamnt@gmail.com";
		account2.username = "lamnt";
		account2.fullname = "Nguyễn Tuấn Lâm";
		account2.department = department2;
		account2.position = position1;
		account2.CreateDate = LocalDate.now();

		Account account3 = new Account();
		account3.id = 3;
		account3.email = "quynhttd@gmail.com";
		account3.username = "quynhttd";
		account3.fullname = "Trần Thị Diễm Quỳnh";
		account3.department = department3;
		account3.position = position3;
		account2.CreateDate = LocalDate.of(2025, 07, 10);

		// tao cac doi tuong Group
		Group group1 = new Group();
		group1.id = 1;
		group1.name = "nhom 1";
		group1.creator = account2;
		group1.createDate = LocalDate.now();

		Group group2 = new Group();
		group2.id = 2;
		group2.name = "nhom 2";
		group2.creator = account3;
		group2.createDate = LocalDate.now();

		Group group3 = new Group();
		group3.id = 3;
		group3.name = "nhom 1";
		group3.creator = account2;
		group3.createDate = LocalDate.of(2025, 07, 10);

		// khai báo Group mà Account tham gia
		Group[] group_account1 = { group1, group2 };
		account1.groups = group_account1;

		Group[] group_account2 = { group1, group2, group3 };
		account1.groups = group_account1;

		Group[] group_account3 = { group2 };
		account1.groups = group_account1;

		// Question 1
		System.out.println("-----Question 1-----");
		if (account2.department == null) {
			System.out.println("Nhan vien nay chua co phong ban");
		} else {
			System.out.println("phong ban cua nhan vien nay la : " + account2.department.name);
		}
		// Question 2
		System.out.println("-----Question 2-----");
		if (account2.groups == null) {
			System.out.println("Nhan vien nay khong co Group");
		} else {
			int dem = account2.groups.length;
			if (dem == 1 || dem == 2) {
				System.out.println("Group của nhân viên này là Java Fresher, C# Fresher");
			} else if (dem == 3) {
				System.out.println("Nhân viên này là người quan trọng, tham gia nhiều group");
			} else if (dem == 4) {
				System.out.println("Nhân viên này là người hóng chuyện, tham gia tất cả các group");
			}
		}

		// Question 3
		System.out.println("----Question 3-----");
		System.out.println(account2.department == null ? "Nhan vien nay chua co phong ban"
				: "phong ban cua nhan vien nay la : " + account2.department.name);

		// Question 4
		System.out.println("----Question 4-----");
		System.out.println(
				account1.position.name.toString() == "Dev" ? "Đây là Developer" : "Người này không phải là Developer");

		// Question 5
		System.out.println("----Question 5-----");

		if (group1.accounts == null) {
			System.out.println("Nhom k co thanh vien nao");
		} else {
			int dem_so_luong_acc = group1.accounts.length;
			switch (dem_so_luong_acc) {
			case 1:
				System.out.println(" Nhóm có một thành viên ");
				break;
			case 2:
				System.out.println("Nhóm có hai thành viên");
				break;
			case 3:
				System.out.println("Nhóm có ba thành viên");
				break;
			default:
				System.out.println("Nhóm có nhiều thành viên");
				break;
			}

		}

		// Question 6
		System.out.println("----Question 6----");
		if (account2.groups == null) {
			System.out.println("Nhom k co thanh vien nao");
		} else {
			switch (account2.groups.length) {
			case 1:
				System.out.println("Group của nhân viên này là Java Fresher, C# Fresher");
				break;
			case 2:
				System.out.println("Group của nhân viên này là Java Fresher, C# Fresher");
				break;
			case 3:
				System.out.println("Nhân viên này là người quan trọng, tham gia nhiều group");
				break;
			case 4:
				System.out.println("Nhân viên này là người hóng chuyện, tham gia tất cả các group");
				break;
			default:
				System.out.println("Nhân viên này chưa có group");
				break;
			}
		}

		// Question 7
		System.out.println("----Question 7-----");
		String positionName = account1.position.name.toString();
		switch (positionName) {
		case "Dev":
			System.out.println("Đây là Developer");
			break;
		default:
			System.out.println("Người này không phải là Developer");
			break;
		}

		// Question 8 :In ra thông tin các account bao gồm: Email, FullName và tên phòng
		// ban của họ
		System.out.println("-----Question 8-----");
		Account[] infoacc = { account1, account2, account3 };
		for (Account account : infoacc) {
			System.out.println("ID : " + account.id + " , " + "Email : " + account.email + " , " + "Full Name : "
					+ account.fullname + " , " + "Phong Ban : " + account.department);
		}

		// Question 9 : In ra thông tin các phòng ban bao gồm: id và name
		System.out.println("-----Question 9-----");
		Department[] infoD = { department1, department2, department3 };
		for (Department department : infoD) {
			System.out.println("ID : " + department.id + " , " + "Name : " + department.name);
		}

		// Question 10 :In ra thông tin các account bao gồm: Email, FullName và tên
		// phòng ban của họ theo định dạng
		System.out.println("-----Question 10-----");
		Account[] arrayacc = { account1, account2, account3 };
		for (int i = 0; i < arrayacc.length; i++) {

			System.out.println("Thong tin account thu " + (i + 1) + ": ");
			System.out.println("Email : " + arrayacc[i].email);
			System.out.println("FullName : " + arrayacc[i].fullname);
			System.out.println("Phong Ban : " + arrayacc[i].department);
			System.out.println("-------");
		}
		// Question 11 In ra thông tin các phòng ban bao gồm: id và name theo định dạng
		System.out.println("-----Question 11-----");
		Department[] qs11 = { department1, department2, department3 };
		for (int i = 0; i < qs11.length; i++) {
			System.out.println("Thong tin department thu " + (i + 1) + ": ");
			System.out.println("ID : " + qs11[i].id);
			System.out.println("Name : " + qs11[i].name);
			System.out.println("-------");
		}
		// question 12 Chỉ in ra thông tin 2 department đầu tiên theo định dạng như

		System.out.println("-----Question 12-----");
		Account[] qs12 = { account1, account2, account3 };
		for (int i = 0; i < 2; i++) {

			System.out.println("Thong tin account thu " + (i + 1) + ": ");
			System.out.println("Email : " + qs12[i].email);
			System.out.println("FullName : " + qs12[i].fullname);
			System.out.println("Phong Ban : " + qs12[i].department);
			System.out.println("-------");
		}

		// Question 13 : In ra thông tin tất cả các account ngoại trừ account thứ 2
		System.out.println("-----Question 13-----");
//		Account[] arrayacc = { account1, account2, account3 };
		for (int i = 0; i < arrayacc.length; i++) {
			if (i != 1) {
				System.out.println("Thong tin account thu " + (i + 1) + ": ");
				System.out.println("Email : " + arrayacc[i].email);
				System.out.println("FullName : " + arrayacc[i].fullname);
				System.out.println("Phong Ban : " + arrayacc[i].department);
				System.out.println("-------");
			}
		}

		// Question 14 In ra thông tin tất cả các account có id < 4
		System.out.println("-----Question 14-----");
		Account[] qs14 = { account1, account2, account3 };
		for (int i = 0; i < qs14.length; i++) {
			if (qs14[i].id < 4) {
				System.out.println("Thong tin account thu " + (i + 1) + ": ");
				System.out.println("Email : " + arrayacc[i].email);
				System.out.println("FullName : " + arrayacc[i].fullname);
				System.out.println("Phong Ban : " + arrayacc[i].department);
				System.out.println("-------");
			}
		}
		// question 15 In ra các số chẵn nhỏ hơn hoặc bằng 20
		System.out.println("-----Question 15-----");
		for (int i = 0; i <= 20; i++) {
			if (i % 2 == 0) {
				System.out.println(i);
			}
		}

		// Question 16 Làm lại các Question ở phần FOR bằng cách sử dụng WHILE kết hợp
		// với lệnh break, continue
		System.out.println("------QUESTION 16-------");
		System.out.println("------question 16 - 10 -------");
		Account[] reqs10 = { account1, account2, account3 };
		int i = 0;
		while (i < reqs10.length) {
			System.out.println("Thong tin account thu " + (i + 1) + ": ");
			System.out.println("Email : " + arrayacc[i].email);
			System.out.println("FullName : " + arrayacc[i].fullname);
			System.out.println("Phong Ban : " + arrayacc[i].department);
			System.out.println("-------");
			i++;

		}

		System.out.println("------QUESTION 16-------");
		System.out.println("------question 16 - 11 -------");
		int j = 0;
		while (j < qs11.length) {
			System.out.println("Thong tin department thu " + (j + 1) + ": ");
			System.out.println("ID : " + qs11[j].id);
			System.out.println("Name : " + qs11[j].name);
			System.out.println("-------");
			j++;
		}
		System.out.println("------QUESTION 16-------");
		System.out.println("------question 16 - 12 -------");
		int x = 0;
		while (x < qs12.length) {
			System.out.println("Thong tin account thu " + (x + 1) + ": ");
			System.out.println("Email : " + qs12[x].email);
			System.out.println("FullName : " + qs12[x].fullname);
			System.out.println("Phong Ban : " + qs12[x].department);
			System.out.println("-------");
			x++;
		}
		// question 17 Làm lại các Question ở phần FOR bằng cách sử dụng DO-WHILE kết
		// hợp với lệnh break, continue
		System.out.println("------QUESTION 17-------");
		System.out.println("------question 17 - 10 -------");
		i = 0;
		do {
			System.out.println("Thong tin account thu " + (i + 1) + ": ");
			System.out.println("Email : " + reqs10[i].email);
			System.out.println("FullName : " + reqs10[i].fullname);
			System.out.println("Phong Ban : " + reqs10[i].department);
			System.out.println("-------");
			i++;

		} while (i < reqs10.length);
		// Exercise 2 (Optional): System out printf
		// Question 1 Khai báo 1 số nguyên = 5 và sử dụng lệnh System out printf để in
		// ra số nguyên đó
		System.out.println("---------EXERCISE 2 -------");
		System.out.println("---Question 1--- ");
		int a = 5;
		System.out.printf("So nguyen la : %d \n", a);

		// Question 2: Khai báo 1 số nguyên = 100 000 000 và sử dụng lệnh System out
		// printf để in ra số nguyên đó thành định dạng như sau: 100,000,000
		System.out.println("---Question 2---");
		int b = 100000000;
		System.out.printf("So nguyen la : %d \n ", b);

		// question 3 : Khai báo 1 số thực = 5,567098 và sử dụng lệnh System out printf
		// để in ra số thực đó chỉ bao gồm 4 số đằng sau
		System.out.println("---Question 3---");
		Double c = 5.567098;
		System.out.printf("so ma ta can la : %.4f \n", c);

		// Question 4 :
		System.out.println("---Question 4---");
		String qs4_ex2 = "Nguyen Van A";
		System.out.printf("Toi ten la : %s va toi dang doc than ", qs4_ex2);

		// Question 5
		System.out.println("-------------Question 5:-------------");
		String pattern = "dd/MM/yyyy HH:mm:ss";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		String date = simpleDateFormat.format(new Date());
		System.out.println(date);
	}
}
