package com.example.jdictoverlay

import android.app.ActivityManager
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.jdictoverlay.service.OverlayService
import com.google.android.material.button.MaterialButton


class MainActivity : AppCompatActivity() {

    private var minimizeBtn: MaterialButton? = null
    private var searchView: SearchView ?= null
    private var writeBtn: MaterialButton ?= null
    private var listView: RecyclerView ?= null
    // opens with layout open
    var isOpen = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*
        setContentView(R.layout.activity_main)

        minimizeBtn = findViewById(R.id.close_open)

        minimizeBtn?.setOnClickListener {
            Log.d("Hi", "CLICKED")
            attemptstartService()
            Log.d("Hi", "ATTEMPTED")
        }*/
        attemptstartService()

        /*
        setContentView(R.layout.search_layout)

        val list: List<String> = listOf("Hi1", "Hi2", "Hi3")
        listView = findViewById<RecyclerView>(R.id.recycler_view)
        listView?.adapter = SearchListAdapter(this, list)
        listView?.setHasFixedSize(true)

        minimizeBtn = findViewById(R.id.close_open)
        searchView = findViewById(R.id.search_bar)
        writeBtn = findViewById(R.id.write_search_input)
        /*
        minimizeBtn?.setOnClickListener {
            openCloseLayout(minimizeBtn!!)
       }*/
        minimizeBtn!!.setOnClickListener(object : DoubleClickListener() {
            override fun onDoubleClick() {
                Log.d("HI", "ONDOUBLE")
            }

            override fun onSingleClick() {
                Log.d("HI", "ONSINGLE")
                openCloseLayout(minimizeBtn!!)
            }

        })
        listView */
    }

    private fun attemptstartService() {
        Log.d("Hi", "ATTEMPT START SERVICE")
        if(isMyServiceRunning()) {

            Log.d("Hi", "SERVICE IS RUNNING")
            stopService(Intent(this@MainActivity, OverlayService::class.java))
        }
        if(checkOverlayDisplayPermission()){
            Log.d("Hi", "START THE SERVICE")
            startService(Intent(this@MainActivity, OverlayService::class.java))
            finish()
            Log.d("Hi", "IN MAIN AFTER START SERVICE")

        }
        else{

            Log.d("Hi", "REQUEST DISPLAY")

            requestOverlayDisplayPermission()
            //finish()
            //startService()
        }
    }

    private fun isMyServiceRunning() : Boolean {
        // The ACTIVITY_SERVICE is needed to retrieve a
        // ActivityManager for interacting with the global system
        // It has a constant String value "activity".
        val manager = this.getSystemService(ACTIVITY_SERVICE) as ActivityManager

        // A loop is needed to get Service information that are currently running in the System.
        // So ActivityManager.RunningServiceInfo is used. It helps to retrieve a
        // particular service information, here its this service.
        // getRunningServices() method returns a list of the services that are currently running
        // and MAX_VALUE is 2147483647. So at most this many services can be returned by this method.
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            // If this service is found as a running, it will return true or else false.
            if (OverlayService::class.java.getName() == service.service.className) {
                return true
            }
        }
        return false
    }

    private fun requestOverlayDisplayPermission() {
        // create alert dialog
        val builder = AlertDialog.Builder(this)
        // allow tap outside alert to cancel
        builder.setCancelable(true)
        // set title of alert
        builder.setTitle("Screen Overlay Permission Needed")
        // set message of alert
        builder.setMessage("Enable 'Display over other apps' from System Settings.")
        // set event of positive button
        // The event of the Positive-Button is set
        // The event of the Positive-Button is set
        builder.setPositiveButton(
            "Open Settings"
        ) { dialog, which ->
            // The app will redirect to the 'Display over other apps' in Settings.
            // This is an Implicit Intent. This is needed when any Action is needed
            // to perform, here it is
            // redirecting to an other app(Settings).
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )

            // This method will start the intent. It takes two parameter,
            // one is the Intent and the other is
            // an requestCode Integer. Here it is -1.
            startActivityForResult(intent, RESULT_OK)
        }
        val dialog = builder.create()
        // show dialog on screen
        dialog.show()
    }

    private fun checkOverlayDisplayPermission() : Boolean {
        // Android Version is lesser than Marshmallow
        // or the API is lesser than 23
        // doesn't need 'Display over other apps' permission enabling.
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            // If 'Display over other apps' is not enabled it
            // will return false or else true
            return Settings.canDrawOverlays(this)
        } else {
            return true
        }
    }

/*
    fun openCloseLayout(button: MaterialButton) {
        var drawable : Drawable?= null
        // layout is open, so shows close before click
        // closing layout with onClick
        if(isOpen) {
            drawable = ContextCompat.getDrawable(minimizeBtn!!.context, R.drawable.ic_open_icon)
            Log.d("HI", "IS OPEN, WILL CLOSE")
            searchView?.visibility = View.GONE
            writeBtn?.visibility = View.GONE
            listView?.visibility = View.GONE

        }
        // layout is closed, shows open before click
        // opening layout with onClick
        else {
            drawable = ContextCompat.getDrawable(minimizeBtn!!.context, R.drawable.ic_close_icon)
            Log.d("HI", "IS CLOSED, WILL OPEN")
            searchView?.visibility = View.VISIBLE
            writeBtn?.visibility = View.VISIBLE
            listView?.visibility = View.VISIBLE
        }
        isOpen = !isOpen
        minimizeBtn?.icon = drawable
    }*/

}