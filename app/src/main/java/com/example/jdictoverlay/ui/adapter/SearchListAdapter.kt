package com.example.jdictoverlay.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.jdictoverlay.R
import org.w3c.dom.Text

class SearchListAdapter(private val context: Context,
                        private val dataset: List<String>) : RecyclerView.Adapter<SearchListAdapter.ViewHolder>() {

        class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
            val textView: TextView = view.findViewById(R.id.recognized_item)
            //val text2: TextView = view.findViewById(R.id.definition)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.recognized_item, parent, false)
        return ViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataset[position]
        Log.d("Hi", "item = " + item)
        holder.textView.text = item
        //holder.text2.text = item
    }

    override fun getItemCount(): Int {
        return dataset.size
    }



}