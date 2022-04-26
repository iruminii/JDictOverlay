package com.example.jdictoverlay.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.os.*
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jdictoverlay.BaseApplication
import com.example.jdictoverlay.R
import com.example.jdictoverlay.databinding.ListItemSearchLayoutBindingImpl
import com.example.jdictoverlay.model.DictEntry
import com.example.jdictoverlay.repository.JDictRepository
import com.example.jdictoverlay.ui.adapter.JDictListAdapter
import com.example.jdictoverlay.ui.adapter.SearchListAdapter
import com.google.android.material.button.MaterialButton
import kotlin.math.ceil
import kotlin.math.min

private val OPEN_CLOSE_BTN_X = 40
private val OPEN_CLOSE_BTN_Y = 40
private val PADDING = 16

//https://stackoverflow.com/questions/4481226/creating-a-system-overlay-window-always-on-top

class OverlayService : LifecycleService() {

    private var windowManager: WindowManager?= null
    private var LAYOUT_TYPE: Int? = null
    private var overlayWindowLayoutParam: WindowManager.LayoutParams ?= null

    private var overlaySearchView: ViewGroup? = null
    private var minimizeBtn: MaterialButton? = null
    private var searchView: SearchView?= null
    private var writeBtn: MaterialButton?= null
    private var searchListView: RecyclerView?= null
    // service opens with layout open
    private var isOpen = true

    // get measurements of the close open button
    private var btnWidth : Double ?= null
    private var btnHeight : Double ?= null
    // get measurements of the padding
    private var paddingx : Double ?= null
    private var paddingy : Double ?= null

    // get height of the searchbar and if theres anything inside it
    private var height : Int ?= null
    private var width : Int ?= null

    //..................................
    private var list: List<String> = listOf("Hi1", "Hi2", "Hi3", "Hi4")
    //..................................
    private var repositoryDb : JDictRepository ?= null


    override fun onCreate() {
        super.onCreate()

        Log.d("Hi", "OVERLAY SERVICE STARTED")

        // get measurements of device screen
        val metrics = resources.displayMetrics
        width = metrics.widthPixels
        height = metrics.heightPixels

        // getting the px values of the open close button
        val density = metrics.density
        btnWidth = ceil(OPEN_CLOSE_BTN_X.times(density)).toDouble()
        btnHeight = ceil(OPEN_CLOSE_BTN_Y.times(density)).toDouble()
        // get px of 16dp padding
        paddingx = ceil(PADDING.times(density)).toDouble()
        paddingy = ceil(PADDING.times(density)).toDouble()

        // get windowmanager
        windowManager = this.getSystemService(WINDOW_SERVICE) as WindowManager

        // create instance of layout inflater to get layout xml
        // inflate this layout in a new view (no root)
        val inflater = LayoutInflater.from(baseContext)
        overlaySearchView = inflater.inflate(R.layout.search_layout, null) as ViewGroup

        val searchAdapter = JDictListAdapter { dictEntry ->
            val action = searchView?.setOnClickListener(this)

        }

        // get entries from db
        repositoryDb = JDictRepository((this.application as BaseApplication).database.jDictDao())

        // recyclerview list adapter for entries in db
        repositoryDb!!.mappedEntries.observe(this) {
            dictEntry -> dictEntry.let {
                adapter.submitList(it)
        }
        }

        minimizeBtn = overlaySearchView?.findViewById(R.id.close_open)
        searchView = overlaySearchView?.findViewById(R.id.search_bar)
        writeBtn = overlaySearchView?.findViewById(R.id.write_search_input)

        //..................................
        searchListView = overlaySearchView?.findViewById<RecyclerView>(R.id.recycler_view)
        searchListView?.layoutManager = LinearLayoutManager(this)
        //listView?.adapter = SearchListAdapter(this, list)

        //..................................


        // WindowManager layout params
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // If API Level is more than 26, we need TYPE_APPLICATION_OVERLAY
            LAYOUT_TYPE = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            // If API Level is lesser than 26, then we can
            // use TYPE_SYSTEM_ERROR,
            // TYPE_SYSTEM_OVERLAY, TYPE_PHONE, TYPE_PRIORITY_PHONE.
            // But these are all
            // deprecated in API 26 and later. Here TYPE_TOAST works best.
            LAYOUT_TYPE = WindowManager.LayoutParams.TYPE_TOAST;
        }

        // calc size of overlay window
        // set it to translucent
        overlayWindowLayoutParam = WindowManager.LayoutParams(
            width!!,
            (height!! * 0.40f).toInt(),
            LAYOUT_TYPE!!,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        )
        overlayWindowLayoutParam?.gravity = Gravity.TOP or Gravity.START
        overlayWindowLayoutParam?.x = 0
        overlayWindowLayoutParam?.y = 0

        // inflate the view with search_layout viewgroup
        // and window params
        windowManager?.addView(overlaySearchView, overlayWindowLayoutParam)
/*
        overlayView!!.setOnTouchListener(object : View.OnTouchListener {
            var x:  Double ?= null
            var y: Double?= null
            var mx: Double?= null
            var my: Double?= null
            var minx: Double ?= null
            var miny: Double ?= null


            override fun onTouch(v: View?, m: MotionEvent?): Boolean {
                // get touch event coords
                mx = m?.getRawX()?.toDouble()
                my = m?.getRawY()?.toDouble()

                // if overlay views are visible, get size of the window
                if(isOpen) {
                    x = overlayWindowLayoutParam?.width?.toDouble()
                    y = overlayWindowLayoutParam?.height?.toDouble()
                    minx = 0.0
                    miny = 0.0
                }

                // if overlay views are hidden, get size of only the open/close button
                else {
                    x = btnWidth?.plus(paddingx!!)
                    y = btnHeight?.plus(paddingy!!)
                    minx = paddingx?.toDouble()
                    miny = paddingy?.toDouble()
                    /*
                    overlayView?.post {
                        // actual width and height of button
                        x = minimizeBtn?.width?.toDouble()?.plus(paddingx!!)
                        y = minimizeBtn?.height?.toDouble()?.plus(paddingy!!)
                        minx = paddingx?.toDouble()
                        miny = paddingy?.toDouble()
                    }*/
                }

                if(minx!! < mx!! && mx!! < x!! &&
                    miny!! < my!! && my!! < y!!) {
                    Log.d("HI", "INSIDE TOUCH EVENT")
                }
                else {

                    Log.d("HI", "OUTSIDE TOUCH EVENT")
                    return false
                }
                return true
            }
        })
*/
        minimizeBtn!!.setOnClickListener(object : DoubleClickListener() {

            override fun onDoubleClick() {
                Log.d("HI", "ONDOUBLE")
                // stopSelf() method is used to stop the service if
                // it was previously started
                stopSelf()

                // The window is removed from the screen
                windowManager!!.removeView(overlaySearchView)
            }

            override fun onSingleClick() {
                Log.d("HI", "ONSINGLE")
                openCloseLayout(minimizeBtn!!)
            }
        })
    }

    fun openCloseLayout(button: MaterialButton) {
        var drawable : Drawable?= null
        // layout is open, so shows close before click
        // closing layout with onClick
        if(isOpen) {
            drawable = ContextCompat.getDrawable(minimizeBtn!!.context, R.drawable.ic_open_icon)
            Log.d("HI", "IS OPEN, WILL CLOSE")
            //searchView?.visibility = View.GONE
            //writeBtn?.visibility = View.GONE
            //listView?.visibility = View.GONE
            // hide softkeyboard
            val inputMethodManager: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

            // The soft keyboard slides back in
            inputMethodManager.hideSoftInputFromWindow(overlaySearchView!!.applicationWindowToken, 0)


            // update view window to be as big as the open close button
            val overlayWindowLayoutUpdateParam: WindowManager.LayoutParams = overlayWindowLayoutParam as WindowManager.LayoutParams
            overlayWindowLayoutUpdateParam?.width = btnWidth!!.plus(paddingx!!).toInt()
            overlayWindowLayoutUpdateParam?.height = btnHeight!!.plus(paddingy!!).toInt()
            overlayWindowLayoutUpdateParam.gravity = Gravity.TOP or Gravity.START
            overlayWindowLayoutUpdateParam.x = 0
            overlayWindowLayoutUpdateParam.y = 0
            windowManager!!.updateViewLayout(overlaySearchView, overlayWindowLayoutUpdateParam)
        }
        // layout is closed, shows open before click
        // opening layout with onClick
        else {
            drawable = ContextCompat.getDrawable(minimizeBtn!!.context, R.drawable.ic_close_icon)
            Log.d("HI", "IS CLOSED, WILL OPEN")
            searchView?.visibility = View.VISIBLE
            writeBtn?.visibility = View.VISIBLE
            searchListView?.visibility = View.VISIBLE

            // update view window to be the width of the screen and
            // height of up to X entries in the recyclerview
            val overlayWindowLayoutUpdateParam: WindowManager.LayoutParams = overlayWindowLayoutParam as WindowManager.LayoutParams
            overlayWindowLayoutUpdateParam?.width = width!!
            overlayWindowLayoutUpdateParam?.height = (height!! * 0.40f).toInt()
            overlayWindowLayoutUpdateParam.gravity = Gravity.TOP or Gravity.START
            overlayWindowLayoutUpdateParam.x = 0
            overlayWindowLayoutUpdateParam.y = 0
            windowManager!!.updateViewLayout(overlaySearchView, overlayWindowLayoutUpdateParam)
        }
        isOpen = !isOpen
        minimizeBtn?.icon = drawable
    }

    // It is called when stopService()
    // method is called in MainActivity
    override fun onDestroy() {
        super.onDestroy()
        stopSelf()
        // Window is removed from the screen
        windowManager!!.removeView(overlaySearchView)
    }

}