package com.example.jdictoverlay

import android.app.Application
import android.graphics.PixelFormat
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import com.example.jdictoverlay.data.JDictDatabase

class BaseApplication : Application() {
    val database: JDictDatabase by lazy {
        JDictDatabase.getDatabase(this)
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("Hi", "DB ON CREATE BASE APP")
    }
}