package com.example.phuongldph29233.student_app.Domain;

public class ClassStudent extends Student{

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
                ", maSV='" + getMaSV() + '\'' +
                ", tenSV='" + getTenSV() + '\'' +
                ", ngaySinh='" + getNgaySinh() + '\'' +
                ", queQuan='" + getQueQuan() + '\'' +
                ", soDienThoai='" + getSoDienThoai() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", lopHoc='" + getLopHoc() + '\'' +
                ", ngayNhapHoc='" + getNgayNhapHoc() + '\'' +
                ", chuyenNganh=" + getChuyenNganh() +
                ", heDaoTao='" + getHeDaoTao() + '\'' +
                '}';
    }
}
