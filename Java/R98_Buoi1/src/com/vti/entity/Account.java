package com.vti.entity;

import java.time.LocalDate;

public class Account {
	public int id;
	public String email;
	public String username;
	public String fullname;
	public Department department;
	public Position position;
	public LocalDate CreateDate;
	public Group[] groups; // mang cac Group

}
