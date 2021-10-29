package com.example.xposed01;

import android.util.Log;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookInnerClass implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if (loadPackageParam.packageName.equals("com.kanxue.xposedhook01")) {
            ClassLoader classLoader = loadPackageParam.classLoader;
            Class PersonCLass = classLoader.loadClass("com.kanxue.xposedhook01.Student$Person");
            XposedHelpers.findAndHookMethod(PersonCLass, "getpersonname", String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    Log.i("Xposed", "Enter Student$Person getpersonname afterHookedMethod");
                    param.setResult("jack");

                }
            });
        }
    }
}
