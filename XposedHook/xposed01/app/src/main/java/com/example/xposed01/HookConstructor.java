package com.example.xposed01;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Hook 构造函数
 */
public class HookConstructor implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        XposedBridge.log("app name:"+loadPackageParam.packageName);
        if(loadPackageParam.packageName.equals("com.kanxue.xposedhook01")){
            ClassLoader classLoader = loadPackageParam.classLoader;
            Class StudentClass = classLoader.loadClass("com.kanxue.xposedhook01.Student");
            XposedHelpers.findAndHookConstructor(StudentClass, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    XposedBridge.log("com.kanxue.xposedhook01.Student() is called!!beforeHookedMethod");
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    XposedBridge.log("com.kanxue.xposedhook01.Student() is called!!afterHookedMethod");
                }
            });

            //    public Student(String name2) {
            //        this.name = name2;
            //        this.id = "default";
            //    }
            //       public java.lang.Object thisObject;
            //        public java.lang.Object[] args;
            //        private java.lang.Object result;
            XposedHelpers.findAndHookConstructor(StudentClass, String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    java.lang.Object[] argsobjarray = param.args;
                    String name = (String) argsobjarray[0];
                    XposedBridge.log("com.kanxue.xposedhook01.Student(String) is called!!beforeHookedMethod--" + name);
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    XposedBridge.log("com.kanxue.xposedhook01.Student(String) is called!!afterHookedMethod");
                }
            });
            //    public Student(String name2, String id2) {
            //        this.name = name2;
            //        this.id = id2;
            //    }

            XposedHelpers.findAndHookConstructor("com.kanxue.xposedhook01.Student", loadPackageParam.classLoader, String.class, String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    java.lang.Object[] argsobjarray = param.args;
                    String name = (String) argsobjarray[0];
                    String id = (String) argsobjarray[1];
                    XposedBridge.log("com.kanxue.xposedhook01.Student(String,String) is called!!beforeHookedMethod--" + name + "---" + id);

                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    XposedBridge.log("com.kanxue.xposedhook01.Student(String,String) is called!!afterHookedMethod");
                }
            });

            //public Student(String name2, String id2, int age2)
            XposedHelpers.findAndHookConstructor(StudentClass, String.class, String.class, int.class,String.class,String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    java.lang.Object[] argsobjarray = param.args;
                    String name = (String) argsobjarray[0];
                    String id = (String) argsobjarray[1];
                    int age = (int) (argsobjarray[2]);
                    argsobjarray[1] = "2050";
                    argsobjarray[2] = 100;

                    String teacher= (String) argsobjarray[3];
                    String nickname= (String) argsobjarray[4];


                    XposedBridge.log("com.kanxue.xposedhook01.Student(String,String) is called!!beforeHookedMethod--" + name + "---" + id + "--" + age+"---"+teacher+"---"+nickname);
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);

                    Object thisobj = param.thisObject;


                   // Field nicknameField=stuClass.getDeclaredField("nickname");
                   // XposedBridge.log(stuClass+"--nicknameField->"+nicknameField);
                   // nicknameField.setAccessible(true);
                   // nicknameField.set(thisobj,"bear");

                    XposedHelpers.setObjectField(thisobj,"nickname","chick");
                    Object returnobj = param.getResult();
                    XposedBridge.log(thisobj + "---" + returnobj);
                    XposedBridge.log("com.kanxue.xposedhook01.Student(String,String,int) is called!!afterHookedMethod");
                }
            });

        }
    }
}
