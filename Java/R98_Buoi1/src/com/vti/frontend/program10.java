package com.vti.frontend;

public class program10 {
	public static void main(String[] args) {
		// Vòng lặp for
		// Hãy in ra các số từ 1 đến 5
		// System.out.println("Số 1"); // Số + 1
		// System.out.println("Số 2");// Số + 2
		// System.out.println("Số 3");
		// System.out.println("Số 4");
		// System.out.println("Số 5");

		// For: Tao ra bộ chị số tự đông: 1 2 3 4 5
		// i = 1
		// i = 1 + 2
		// i = 2 + 1 = 3
		// for (int i = 10; i > 0; i = i - 1) {
		// System.out.println("Số: " + i);
		// }

		System.out.println("Thông tin các bạn học viên trên lớp");
		String[] nameRailway98 = { "Tiên", "Nam", "Hương", "Bình", "Trình", "Lâm", "Hùng", "Hoàng" };
		System.out.println(nameRailway98[0]);
		System.out.println(nameRailway98[1]);
		System.out.println(nameRailway98[2]);
		System.out.println(nameRailway98[3]);

		// 0-5

		// Sử dụng vòng lặp for để in thông tin các bạn học viên trên lớp
		// i = i + 1 ==> i++
		// i = i - 1 ==> i--

		for (int i = 0; i < nameRailway98.length; i++) {
			System.out.println("Học viên " + (i + 1) + ":" + nameRailway98[i]);
		}
		for (String string : nameRailway98) {
			System.out.println(nameRailway98);
		}
		int index = 1;
	    for (String name : nameRailway98){
	            System.out.println("Sinh viên " + index + " có tên: " + name);
	            index++;
	    }
	    
	    Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("mời bạn nhập vào tuổi");
            int age = scanner.nextInt();

            if (age > 0) {
                System.out.println("Tuổi bạn vừa nhập là: " + age);
                scanner.close(); // Thêm close() khi không cần dùng nữa
                return; // Thoát khỏi hàm main
            }
        }

}