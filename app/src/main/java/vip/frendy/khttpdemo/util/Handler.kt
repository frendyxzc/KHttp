package com.iimedia.appbase.extension

import android.os.Handler
import android.os.Looper

/**
 * Created by iiMedia on 2017/6/27.
 */

fun postDelayedToUI(runnable: () -> Unit, delay: Long) {
    val mainHandler = Handler(Looper.getMainLooper())
    mainHandler.postDelayed(runnable, delay)
}

fun postToUI(runnable: () -> Unit) {
    val mainHandler = Handler(Looper.getMainLooper())
    mainHandler.post(runnable)
}