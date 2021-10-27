package com.qule.myapplication;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;
import dalvik.system.DexFile;

public class ClassLoaderTest {

    public void test1() {
        ClassLoader classLoader01 = MainActivity.class.getClassLoader();
        //获取类所在的classLoader
        ClassLoader classLoader02 = String.class.getClassLoader();
        //2021-10-18 16:46:22.779 8273-8273/com.example.myapplication E/ClassLoader: ClassLoader01:dalvik.system.PathClassLoader[DexPathList[[zip file "/data/app/com.example.myapplication-c08nkDa3VG5CHKQ4p_ubDA==/base.apk"],nativeLibraryDirectories=[/data/app/com.example.myapplication-c08nkDa3VG5CHKQ4p_ubDA==/lib/arm64, /system/lib64, /product/lib64]]]
        //2021-10-18 16:46:22.780 8273-8273/com.example.myapplication E/ClassLoader: ClassLoader02:java.lang.BootClassLoader@91d8062
        Log.e("ClassLoader", "ClassLoader01:" + classLoader01.toString());
        Log.e("ClassLoader", "ClassLoader02:" + classLoader02.toString());

    }

    public void test2() {
        ClassLoader classLoader01 = MainActivity.class.getClassLoader();
        Log.e("ClassLoader", "ClassLoader01:" + classLoader01.toString());
        //获取类所在的classLoader
        ClassLoader classLoader02 = String.class.getClassLoader();
        ClassLoader parent = classLoader01.getParent();
        while (parent != null) {
            Log.e("parent-classLoader", "parent classloader:" + parent);

            parent = parent.getParent();
        }
        //2021-10-18 16:53:23.679 8831-8831/com.example.myapplication E/ClassLoader: ClassLoader01:dalvik.system.PathClassLoader[DexPathList[[zip file "/data/app/com.example.myapplication-11OyaB3BBvz1B7c8ye3FPQ==/base.apk"],nativeLibraryDirectories=[/data/app/com.example.myapplication-11OyaB3BBvz1B7c8ye3FPQ==/lib/arm64, /system/lib64, /product/lib64]]]
        //2021-10-18 16:53:23.679 8831-8831/com.example.myapplication E/parent-classLoader: parent classloader:java.lang.BootClassLoader@91d8062
    }

    public void test3() {
        ClassLoader classLoader01 = MainActivity.class.getClassLoader();

        //获取类所在的classLoader
        ClassLoader classLoader02 = String.class.getClassLoader();

        try {
            classLoader02.loadClass("com.qule.myapplication.FirstFragment");
        } catch (ClassNotFoundException e) {
            Log.e("loadClass", "failed load class:" + " FirstFragment");
            e.printStackTrace();
        }

        try {
            Class<?> FirstFragment = classLoader01.loadClass("com.qule.myapplication.FirstFragment");
            Log.e("loadClass", "load class " + FirstFragment.getName() + " success");
        } catch (ClassNotFoundException e) {
            Log.e("loadClass", "failed load class:" + " FirstFragment");
            e.printStackTrace();
        }

        try {
            //双亲委派机制
            Class<?> a = classLoader01.loadClass("java.lang.String");
            Log.e("loadClass", "load class " + a.getName() + " success");
        } catch (ClassNotFoundException e) {
            Log.e("loadClass", "failed load class:" + " java.lang.String");
            e.printStackTrace();
        }

    }

    public void test4(Context context) {
        ClassLoader classLoader01 = MainActivity.class.getClassLoader();
        ClassLoader classLoader02 = String.class.getClassLoader();

        DexClassLoader dexClassLoader = new DexClassLoader("/data/local/tmp/plugindex.dex",
                context.getApplicationContext().getCacheDir().getAbsolutePath(), null, classLoader02);

        try {
            Class<?> pluginDexTestClass = dexClassLoader.loadClass("com.qule.plugindex.PluginDexTest");
            Log.e("pluginDex", "pluginDex class:" + pluginDexTestClass.toString());
            //2021-10-18 23:56:06.482 14382-14382/com.qule.myapplication E/pluginDex: pluginDex class:class com.qule.plugindex.PluginDexTest
            //2021-10-18 23:56:06.483 14382-14382/com.qule.myapplication E/pluginDex: test method is called
            Method testMethod = pluginDexTestClass.getDeclaredMethod("testMethod");
            testMethod.invoke(null);
            // 如果使用bootClassLoader作为父类classLoader 无法找到类MainActivity
            Class<?> testClass = dexClassLoader.loadClass("com.qule.myapplication.MainActivity");
            Log.e("pluginDex", "load class success:" + testClass.getName());


        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }


    }
    public void getClassListInClassLoader(ClassLoader classLoader){
        try {
            Class<?>  BaseDexClassLoader=Class.forName("dalvik.system.BaseDexClassLoader");
            Field pathListField=BaseDexClassLoader.getDeclaredField("pathList");
            pathListField.setAccessible(true);
            Object pathListObj=pathListField.get(classLoader);
            Class<?> DexPathListClass=Class.forName("dalvik.system.DexPathList");
            Field DexElementField= DexPathListClass.getDeclaredField("dexElements");
            DexElementField.setAccessible(true);
            //获取dex elements
            Object[] dexElementArrayObj=(Object[]) DexElementField.get(pathListObj);

            Class<?>  elementClass=Class.forName("dalvik.system.DexPathList$Element");
            Field dexFileField=elementClass.getDeclaredField("dexFile");
            dexFileField.setAccessible(true);

            Class<?> dexFileClass=Class.forName("dalvik.system.DexFile");
            Method getClassNameListMethod=dexFileClass.getDeclaredMethod("getClassNameList",Object.class);
            getClassNameListMethod.setAccessible(true);
            Field mCookieField=dexFileClass.getDeclaredField("mCookie");
            mCookieField.setAccessible(true);

            Field mFileNameField=dexFileClass.getDeclaredField("mFileName");
            mFileNameField.setAccessible(true);
            Log.e("classNameList","dexElementArrayObj size:"+dexElementArrayObj.length);
            for(Object dexElementObj:dexElementArrayObj){
                Object dexFileObj=dexFileField.get(dexElementObj);
                Object mCookieObj=mCookieField.get(dexFileObj);
                Object mFileNameObj=mFileNameField.get(dexFileObj);
                String[] classNameList=(String[])getClassNameListMethod.invoke(null,mCookieObj);
                for(String className:classNameList){
                    Log.e("classNameList",mFileNameObj+"-->"+className);
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    public void test5() {
        ClassLoader classLoader01 = MainActivity.class.getClassLoader();
        ClassLoader classLoader02 = String.class.getClassLoader();
        getClassListInClassLoader(classLoader01);
    }


    public void test6(Context context) {
        ClassLoader classLoader01 = MainActivity.class.getClassLoader();
        ClassLoader classLoader02 = String.class.getClassLoader();

        DexClassLoader dexClassLoader = new DexClassLoader("/data/local/tmp/plugindex.dex",
                context.getApplicationContext().getCacheDir().getAbsolutePath(), null, classLoader02);
        getClassListInClassLoader(dexClassLoader);

    }
}
