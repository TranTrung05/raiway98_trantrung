package com.vti.entity;

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
	}
}
