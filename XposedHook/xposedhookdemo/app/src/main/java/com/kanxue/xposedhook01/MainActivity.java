package com.kanxue.xposedhook01;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    public void printStudent(Student stu) {
        Log.i("Xposed", stu.name + "--" + stu.id + "---" + stu.age + "---" + stu.getNickname() + "---" + Student.getTeacher());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Student astudent = new Student();
        Student bstudent = new Student("xiaoming");
        Student cstudent = new Student("xiaohua", "2020");
        Student dstudent = new Student("xiaohong", "2010", 20, "teacher", "panda");
        // printStudent(astudent);
        // printStudent(bstudent);
        // printStudent(cstudent);
        // printStudent(dstudent);
        // Log.i("Xposed", Student.publicstaticfunc("publicstaticfunc", 100));
        Log.i("Xposed", dstudent.publicfunc("publicfunc", 500));

    }
}