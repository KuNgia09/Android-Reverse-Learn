package com.kanxue.xposedhook01;

import android.util.Log;

public class Student {
    private static String teacher = null;
    int age;
    String id;
    String name;
    private String nickname;

    public class Person {
        public String name;
        public String sex;

        public Person() {
        }

        public String getpersonname(String arg0) {
            return arg0;
        }
    }

    public String getNickname() {
        return this.nickname;
    }

    public static String getTeacher() {
        return teacher;
    }

    public Student() {
        this.name = null;
        this.id = null;
        this.age = 0;
        this.nickname = null;
        this.name = "default";
        this.id = "default";
        this.age = 100;
    }

    public Student(String name2) {
        this.name = null;
        this.id = null;
        this.age = 0;
        this.nickname = null;
        this.name = name2;
        this.id = "default";
    }

    public Student(String name2, String id2) {
        this.name = null;
        this.id = null;
        this.age = 0;
        this.nickname = null;
        this.name = name2;
        this.id = id2;
    }

    public Student(String name2, String id2, int age2, String teachername, String nickname2) {
        this.name = null;
        this.id = null;
        this.age = 0;
        this.nickname = null;
        this.name = name2;
        this.id = id2;
        this.age = age2;
        teacher = teachername;
        this.nickname = nickname2;
    }

    public static String publicstaticfunc(String arg1, int arg2) {
        String result = privatestaticfunc("privatestaticfunc", 200);
        Log.i("Xposed", "publicstaticfunc is called!---" + arg1 + "---" + arg2);
        return arg1 + "---" + arg2 + "---" + result;
    }

    private static String privatestaticfunc(String arg1, int arg2) {
        Log.i("Xposed", "privatestaticfunc is called!---" + arg1 + "---" + arg2);
        return arg1 + "---" + arg2;
    }

    public String publicfunc(String arg1, int arg2) {
        String result = privatefunc("privatefunc", 300);
        Log.i("Xposed", "publicfunc is called!---" + arg1 + "---" + arg2);
        String tmpresult = new Person().getpersonname("person");
        return arg1 + "---" + arg2 + "---" + result + "---" + tmpresult;
    }

    private String privatefunc(String arg1, int arg2) {
        Log.i("Xposed", "privatefunc is called!---" + arg1 + "---" + arg2);
        return arg1 + "---" + arg2;
    }
}