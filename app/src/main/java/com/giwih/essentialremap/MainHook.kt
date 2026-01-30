package com.giwih.essentialremap

import android.view.KeyEvent
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

class MainHook : IXposedHookLoadPackage {

    companion object {
        const val TARGET_SCAN_CODE = 250
    }

    private val keyPressDetector by lazy { 
        KeyPressDetector() 
    }

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
                        
                        if (event.scanCode == TARGET_SCAN_CODE) {
                            
                            when (event.action) {
                                KeyEvent.ACTION_DOWN -> {
                                    if (event.repeatCount == 0) {
                                        keyPressDetector.onKeyDown(
                                            onLongPress = { handleLongPress() }
                                        )
                                    }
                                }
                                KeyEvent.ACTION_UP -> {
                                    keyPressDetector.onKeyUp(
                                        onDoublePress = { handleDoublePress() },
                                        onSinglePress = { handleSinglePress() }
                                    )
                                }
                                // XposedBridge.log("EssentialRemap: Event cancelled, resetting detector")
                                // keyPressDetector.reset()
                            }

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


    private fun handleLongPress() {
        XposedBridge.log("EssentialRemap: ==> LONG PRESS TRIGGERED <==")
    }


    private fun handleDoublePress() {
        XposedBridge.log("EssentialRemap: ==> DOUBLE PRESS TRIGGERED <==")
    }


    private fun handleSinglePress() {
        XposedBridge.log("EssentialRemap: ==> SINGLE PRESS TRIGGERED <==")
    }
}
