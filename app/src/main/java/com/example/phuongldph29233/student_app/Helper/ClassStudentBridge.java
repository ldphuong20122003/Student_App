//package com.example.phuongldph29233.student_app.Helper;
//
//import com.example.phuongldph29233.student_app.Domain.Class;
//import com.example.phuongldph29233.student_app.Domain.Student;
//
//public class ClassStudentBridge {
//    private static final DatabaseHelper<Student> studentHelper = new DatabaseHelper<>("Students");
//    private static final DatabaseHelper<Class> classHelper = new DatabaseHelper<>("Classes");
//
//    // Cập nhật quan hệ khi thay đổi lớp của sinh viên
//    public static void updateStudentClass(String studentId, String newClassId) {
//        studentHelper.get(studentId, Student.class, new DatabaseHelper.DatabaseGetCallback<Student>() {
//            @Override
//            public void onSuccess(Student student) {
//                String oldClassId = student.getClassId();
//
//                // 1. Cập nhật lớp mới cho sinh viên
//                student.setClassId(newClassId);
//                studentHelper.update(studentId, student, null);
//
//                // 2. Xóa khỏi lớp cũ (nếu có)
//                if (oldClassId != null) {
//                    classHelper.get(oldClassId, Class.class, new DatabaseHelper.DatabaseGetCallback<Class>() {
//                        @Override
//                        public void onSuccess(Class oldClass) {
//                            oldClass.removeStudent(studentId);
//                            classHelper.update(oldClassId, oldClass, null);
//                        }
//                    });
//                }
//
//                // 3. Thêm vào lớp mới (nếu có)
//                if (newClassId != null) {
//                    classHelper.get(newClassId, Class.class, new DatabaseHelper.DatabaseGetCallback<Class>() {
//                        @Override
//                        public void onSuccess(Class newClass) {
//                            newClass.addStudent(studentId);
//                            classHelper.update(newClassId, newClass, null);
//                        }
//                    });
//                }
//            }
//        });
//    }
//}
