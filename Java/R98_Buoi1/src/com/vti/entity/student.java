package com.vti.entity;

public class student {
	// property : thuoc tinh

	public String name;
	public int age;

	// có hành động tới VTI để học tập
	public void gotoVTI() {
		System.out.println(this.name + ": Go to VTI to Study!");
	}

	// phuong thuc gioi thieu thong tin cua moi ban hoc vien

	public void showInfo() {
		System.out.println("Toi la :" + this.name + ",toi" + this.age + "tuoi");
	}
	// cach 2

}
