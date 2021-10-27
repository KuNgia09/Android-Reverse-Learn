package com.example.packclassloader;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.widget.TextView;

import com.example.packclassloader.databinding.ActivityMainBinding;

import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'packclassloader' library on application startup.
    static {
        System.loadLibrary("packclassloader");
    }

    private ActivityMainBinding binding;

    private static String pluginDexPath="/data/local/tmp/4.dex";
    public static Context appcontext = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("class","MainActivity onCreate");
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Example of a call to a native method
        TextView tv = binding.sampleText;
        tv.setText(stringFromJNI());



        appcontext = this.getApplicationContext();
        //LoadDex(this);
//        startActivityFirstMethod(this);
//        startActivitySecondMethod(this);
        startActivityThirdMethod(this);

    }

    /**
     * 根据classLoader获取Dex Elements
     * @param classLoader
     * @return
     */
    public static Object getDexElementsInClassLoader(ClassLoader classLoader){
        try {
            Class basedexclassloaderclass = classLoader.loadClass("dalvik.system.BaseDexClassLoader");
            Field pathlistfield = basedexclassloaderclass.getDeclaredField("pathList");
            pathlistfield.setAccessible(true);
            Object pathlistobj = pathlistfield.get(classLoader);
            Class pathlistclass = classLoader.loadClass("dalvik.system.DexPathList");
            Field dexElementsfield = pathlistclass.getDeclaredField("dexElements");
            dexElementsfield.setAccessible(true);
            Object dexelementarray =  dexElementsfield.get(pathlistobj);
            return dexelementarray;

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将2个DexElement进行合并
     * @param dexelement1
     * @param dexelement2
     * @return
     */
    public static Object combineDexElements(Object dexelement1,Object dexelement2){
        if(dexelement1==null || dexelement2==null){
            return null;
        }
        int length1= Array.getLength(dexelement1);
        int length2=Array.getLength(dexelement2);
        int length=length1+length2;
        Object combinedDexElements=Array.newInstance(dexelement1.getClass().getComponentType(),length);
        for(int i=0;i<length;i++){
            if(i<length1){
                Array.set(combinedDexElements,i,Array.get(dexelement1,i));
            }else{
                Array.set(combinedDexElements,i,Array.get(dexelement2,i-length1));
            }
        }
        return combinedDexElements;

    }

    public static boolean setDexElementInClassLoader(ClassLoader classLoader,Object dexElement){
        try {
            Class basedexclassloaderclass = classLoader.loadClass("dalvik.system.BaseDexClassLoader");
            Field pathlistfield = basedexclassloaderclass.getDeclaredField("pathList");
            pathlistfield.setAccessible(true);
            Object pathlistobj = pathlistfield.get(classLoader);
            Class pathlistclass = classLoader.loadClass("dalvik.system.DexPathList");
            Field dexElementsfield = pathlistclass.getDeclaredField("dexElements");
            dexElementsfield.setAccessible(true);
            dexElementsfield.set(pathlistobj,dexElement);
            return true;

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;

    }
    public static void replaceClassLoader(ClassLoader dexclassloader) {
        ClassLoader pathClassloader = MainActivity.class.getClassLoader();
        try {
            Class ActivityThreadClass = pathClassloader.loadClass("android.app.ActivityThread");
            //public static ActivityThread currentActivityThread()
            Method currentActivityThreadMethod = ActivityThreadClass.getDeclaredMethod("currentActivityThread");
            currentActivityThreadMethod.setAccessible(true);
            Object currentActivityThread = currentActivityThreadMethod.invoke(null);
            Field mPackagesfield = ActivityThreadClass.getDeclaredField("mPackages");
            mPackagesfield.setAccessible(true);
            ArrayMap mPackagesobj = (ArrayMap) mPackagesfield.get(currentActivityThread);
            if (appcontext != null) {
                String packagename = appcontext.getPackageName();
                //final ArrayMap<String, WeakReference<LoadedApk>> mPackages = new ArrayMap<>();
                WeakReference wr = (WeakReference) mPackagesobj.get(packagename);
                Object loadedapk = wr.get();
                Class LoadedApkClass = pathClassloader.loadClass("android.app.LoadedApk");
                Field mClassLoaderfield = LoadedApkClass.getDeclaredField("mClassLoader");
                mClassLoaderfield.setAccessible(true);
                ClassLoader pathclassloader = (ClassLoader) mClassLoaderfield.get(loadedapk);
                Log.e("mClassloader", pathClassloader.toString());
                mClassLoaderfield.set(loadedapk, dexclassloader);

            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }




    public static void startActivityFirstMethod(Context context) {
        ClassLoader pathClassloader = MainActivity.class.getClassLoader();
        DexClassLoader dexClassLoader = new DexClassLoader(pluginDexPath, context.getApplicationContext().getCacheDir().getAbsolutePath(), null, pathClassloader);
        getClassLoaderClasses(dexClassLoader);
        replaceClassLoader(dexClassLoader);
        try {
            Class TestActivityClass = dexClassLoader.loadClass("com.kanxue.test02.TestActivity");
            Log.e("class", TestActivityClass.toString());
            context.startActivity(new Intent(context, TestActivityClass));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public static void startActivitySecondMethod(Context context) {
        ClassLoader pathClassloader = MainActivity.class.getClassLoader();
        ClassLoader bootClassloader = pathClassloader.getParent();
        DexClassLoader dexClassLoader = new DexClassLoader("/sdcard/4.dex", context.getApplicationContext().getCacheDir().getAbsolutePath(), null, bootClassloader);

        try {
            //  private final ClassLoader parent;
            Field parentfield = ClassLoader.class.getDeclaredField("parent");
            parentfield.setAccessible(true);
            parentfield.set(pathClassloader, dexClassLoader);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        try {
            Class TestActivityClass = dexClassLoader.loadClass("com.kanxue.test02.TestActivity");
            Log.e("class", TestActivityClass.toString());
            context.startActivity(new Intent(context, TestActivityClass));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public static void startActivityThirdMethod(Context context) {
        ClassLoader pathClassloader = MainActivity.class.getClassLoader();
        DexClassLoader dexClassLoader = new DexClassLoader(pluginDexPath, context.getApplicationContext().getCacheDir().getAbsolutePath(), null, pathClassloader);
        getClassLoaderClasses(dexClassLoader);
        Object pathClassLoaderElement=getDexElementsInClassLoader(pathClassloader);
        Object dexCLassLoaderElement=getDexElementsInClassLoader(dexClassLoader);
        Object finalDexElement=combineDexElements(pathClassLoaderElement,dexCLassLoaderElement);
        setDexElementInClassLoader(pathClassloader,finalDexElement);
        try {
            Class TestActivityClass = dexClassLoader.loadClass("com.kanxue.test02.TestActivity");
            Log.e("class", TestActivityClass.toString());
            context.startActivity(new Intent(context, TestActivityClass));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * A native method that is implemented by the 'packclassloader' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    public static void LoadDex(Context context) {
        //public DexClassLoader(String dexPath, String optimizedDirectory,
        //56            String librarySearchPath, ClassLoader parent)
        ClassLoader pathClassloader = MainActivity.class.getClassLoader();
        DexClassLoader dexClassLoader = new DexClassLoader("/data/local/tmp/4.dex", context.getApplicationContext().getCacheDir().getAbsolutePath(), null, pathClassloader);
        Log.e("class","dex classLoader:"+dexClassLoader.toString());
        getClassLoaderClasses(dexClassLoader);
        try {
            Class testClass = dexClassLoader.loadClass("com.kanxue.test02.TestClass");
            Log.e("class", testClass.toString());
            Method testFuncMethod = testClass.getDeclaredMethod("testFunc");
            testFuncMethod.setAccessible(true);
            testFuncMethod.invoke(null);
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
    public static void getClassLoaderClasses(ClassLoader classLoader) {
        try {
            Class basedexclassloaderclass = classLoader.loadClass("dalvik.system.BaseDexClassLoader");
            Field pathlistfield = basedexclassloaderclass.getDeclaredField("pathList");
            pathlistfield.setAccessible(true);
            Object pathlistobj = pathlistfield.get(classLoader);
            Class pathlistclass = classLoader.loadClass("dalvik.system.DexPathList");
            Field dexElementsfield = pathlistclass.getDeclaredField("dexElements");
            dexElementsfield.setAccessible(true);
            Object[] dexelementarray = (Object[]) dexElementsfield.get(pathlistobj);
            //forName
            //loadClass
            Class Elementclass = classLoader.loadClass("dalvik.system.DexPathList$Element");
            Field dexFilefield = Elementclass.getDeclaredField("dexFile");
            dexFilefield.setAccessible(true);

            Class DexFileclass = classLoader.loadClass("dalvik.system.DexFile");
            Field mCookiefield = DexFileclass.getDeclaredField("mCookie");
            mCookiefield.setAccessible(true);

            Method[] methods = DexFileclass.getDeclaredMethods();
            Method getClassNameListMethod = null;
            for (Method i : methods) {
                if (i.getName().equals("getClassNameList")) {
                    getClassNameListMethod = i;
                    getClassNameListMethod.setAccessible(true);
                }
            }
            for (Object element : dexelementarray) {

                Object dexfileobj = dexFilefield.get(element);
                Object mCookieobj = mCookiefield.get(dexfileobj);
                String[] classlist = (String[]) getClassNameListMethod.invoke(dexfileobj, mCookieobj);
                for (String classname : classlist) {
                    Log.i("classloader", classLoader + "--->" + classname);
                }
            }
            //private Element[] dexElements;

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}