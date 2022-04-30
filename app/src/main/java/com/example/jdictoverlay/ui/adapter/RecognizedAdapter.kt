package com.example.jdictoverlay.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.jdictoverlay.databinding.ListItemDictEntryBinding
import com.example.jdictoverlay.databinding.RecognizedItemBinding
import com.example.jdictoverlay.model.DictEntry

class RecognizedAdapter(
    private val clickListener:(String) -> Unit
) : ListAdapter<String, RecognizedAdapter.RecognizedViewHolder> (DiffCallback){

    class RecognizedViewHolder (
        private var binding: RecognizedItemBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(candidate: String) {
            binding.recognizedItem.text = candidate
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback: DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem.length == newItem.length
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecognizedViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return RecognizedViewHolder(
            RecognizedItemBinding.inflate(layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecognizedViewHolder, position: Int) {

        Log.d("ADAPTERLIST", "HI ONBIND")
        val candidate = getItem(position)
        holder.itemView.setOnClickListener{
            clickListener(candidate)
        }
        holder.bind(candidate)
    }
}