package com.giwih.essentialremap

import android.os.Handler
import android.os.Looper
import de.robv.android.xposed.XposedBridge

class KeyPressDetector(
    private val longPressDelay: Long = 500,
    private val doublePressDelay: Long = 300
) {
    companion object {
        private const val NOT_PRESSED = 0
        private const val SINGLE_PRESSED = 1
    }

    // IMPORTANT: Use MainLooper, to avoid a mistake "Can't create handler..."
    private val handler: Handler by lazy { Handler(Looper.getMainLooper()) }

    private var doublePressState: Int = NOT_PRESSED
    private var longPressHandled: Boolean = false
    
    // We keep links to current tasks so that they can be canceled.
    private var pendingLongPressRunnable: Runnable? = null
    private var pendingSinglePressRunnable: Runnable? = null

    /** Processing ACTION_DOWN */
    fun onKeyDown(eventTime: Long, onLongPress: (() -> Unit)): Boolean {        
        longPressHandled = false
        
        // Resetting previous single-click expectations if clicked again very quickly
        if (pendingSinglePressRunnable != null) {
            handler.removeCallbacks(pendingSinglePressRunnable!!)
            pendingSinglePressRunnable = null
        }

        val longPressRunnable = Runnable {
            longPressHandled = true
            XposedBridge.log("EssentialRemap: Long Press Detected via Handler!")
            onLongPress.invoke()
        }
        pendingLongPressRunnable = longPressRunnable
        handler.postDelayed(longPressRunnable, longPressDelay)

        return true
    }

    /** Processing ACTION_UP */ 
    fun onKeyUp(eventTime: Long, onDoublePress: (() -> Unit)? = null, onSinglePress: (() -> Unit)? = null): Boolean {
        if (pendingLongPressRunnable != null) {
            handler.removeCallbacks(pendingLongPressRunnable!!)
            pendingLongPressRunnable = null
        }

        // If the long press managed to work, reset everything and exit.
        if (longPressHandled) {
            XposedBridge.log("EssentialRemap: Long press handled, resetting state")
            doublePressState = NOT_PRESSED

            return true
        }

        // logic of double tapping
        when (doublePressState) {
            NOT_PRESSED -> {
                // first click
                doublePressState = SINGLE_PRESSED
                
                val singlePressRunnable = Runnable {
                    if (doublePressState == SINGLE_PRESSED) {
                        XposedBridge.log("EssentialRemap: Single Press Confirmed (Timeout)")
                        doublePressState = NOT_PRESSED
                        onSinglePress?.invoke()
                    }
                }
                pendingSinglePressRunnable = singlePressRunnable
                handler.postDelayed(singlePressRunnable, doublePressDelay)
            }
            
            SINGLE_PRESSED -> {
                // second click
                XposedBridge.log("EssentialRemap: Double Press Detected immediately!")
                onDoublePress?.invoke()
                
                doublePressState = NOT_PRESSED
            }
            
            else -> {
                doublePressState = NOT_PRESSED
            }
        }
        return true
    }

    fun reset() {
        handler.removeCallbacksAndMessages(null)
        doublePressState = NOT_PRESSED
        longPressHandled = false
        pendingLongPressRunnable = null
        pendingSinglePressRunnable = null
    }
}
