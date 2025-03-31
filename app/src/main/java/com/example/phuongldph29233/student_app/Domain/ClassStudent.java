package com.example.phuongldph29233.student_app.Domain;

public class ClassStudent extends Student {

    public ClassStudent() {
        super();
    }

    public ClassStudent(String id, String maSV, String tenSV, String ngaySinh, String queQuan,
                        String soDienThoai, String email, Class lopHoc, String ngayNhapHoc,
                        Branch chuyenNganh, String heDaoTao) {
        super(id, maSV, tenSV, ngaySinh, queQuan, soDienThoai, email, lopHoc,
                ngayNhapHoc, chuyenNganh, heDaoTao);
    }

    @Override
    public String toString() {
        return "SinhVienClass{" +
                "id='" + getId() + '\'' +
                ", Mã sinh viên ='" + getStudentID() + '\'' +
                ", Tên sinh viên ='" + getStudentName() + '\'' +
                ", Ngày sinh='" + getStudentBirthday() + '\'' +
                ", Quê quán='" + getStudentHomeTown() + '\'' +
                ", Số điện thoại='" + getStudentPhone() + '\'' +
                ", Email='" + getStudentEmail() + '\'' +
                ", Lớp học='" + getStudentClass() + '\'' +
                ", Ngày nhập học='" + getStudentDateJoin() + '\'' +
                ", Chuyên ngành=" + getStudentBranch() +
                ", Hệ đào tạo='" + getStudentTOT() + '\'' +
                '}';
    }
}
