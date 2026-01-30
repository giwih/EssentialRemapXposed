package com.giwih.essentialremap

import android.view.KeyEvent
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

import android.content.ComponentName
import android.content.Intent
import android.content.Context

class MainHook : IXposedHookLoadPackage {

    override fun handleLoadPackage(lpparam: LoadPackageParam) {

        if (lpparam.packageName != "android") return

        try {
            val phoneWindowManagerClass = XposedHelpers.findClass(
                "com.android.server.policy.PhoneWindowManager",
                lpparam.classLoader
            )

            XposedBridge.hookAllMethods(
                phoneWindowManagerClass,
                "interceptKeyBeforeQueueing",
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        val event = param.args[0] as KeyEvent
                        
                        if (event.scanCode == 250) {
                            
                            if (event.action == KeyEvent.ACTION_DOWN) {
                                try {
                                    val intent = Intent().apply {
                                        component = ComponentName(BuildConfig.TARGET_PKG, BuildConfig.TARGET_CLS)
                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    }

                                    // val mContext = XposedHelpers.getObjectField(param.thisObject, "mContext") as Context
                                    // mContext.startActivity(intent)
                                    (XposedHelpers.getObjectField(param.thisObject, "mContext") as Context).startActivity(intent)
                                    
                                    XposedBridge.log("EssentialRemap: Activity started via Context")
                                } catch (t: Throwable) {
                                    XposedBridge.log("EssentialRemap ERROR: ${t.message}")
                                }
                            }

                            // Disabling:
                            // We return 0 so that the system ignores this event.
                            param.result = 0 
                        }
                    }
                }
            )
            XposedBridge.log("EssentialRemap: Hook on PhoneWindowManager installed")
            
        } catch (e: Throwable) {
            XposedBridge.log("EssentialRemap Error: " + e.message)
        }
    }
}
