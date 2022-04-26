package com.example.jdictoverlay.service

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.view.View

//https://stackoverflow.com/questions/15353457/single-click-and-double-click-of-a-button-in-android

abstract class DoubleClickListener : View.OnClickListener {

    private val DEFAULT_QUALIFICATION_SPAN = 200L
    private var isSingleEvent = false
    private val doubleClickQualificationSpanInMillis =
        DEFAULT_QUALIFICATION_SPAN
    private var timestampLastClick = 0L
    private val handler = Handler(Looper.getMainLooper())
    private val runnable: () -> Unit = {
        if (isSingleEvent) {

            Log.d("HI", "DOUBLELISTENER IS SINGLE")
            onSingleClick()
        }
    }
    override fun onClick(v: View) {

        Log.d("HI", "DOUBLELISTENER ONCLICK")
        if (SystemClock.elapsedRealtime() - timestampLastClick < doubleClickQualificationSpanInMillis) {

            Log.d("HI", "DOUBLELISTENER IS DOUBLE")
            isSingleEvent = false
            handler.removeCallbacks(runnable)
            onDoubleClick()
            return
        }
        isSingleEvent = true
        handler.postDelayed(runnable, DEFAULT_QUALIFICATION_SPAN)
        timestampLastClick = SystemClock.elapsedRealtime()
    }

    abstract fun onDoubleClick()
    abstract fun onSingleClick()
}