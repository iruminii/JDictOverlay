package com.example.jdictoverlay.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.jdictoverlay.databinding.ListItemDictEntryBinding
import com.example.jdictoverlay.model.DictEntry

class JDictListAdapter(
    private val clickListener:(DictEntry) -> Unit
) : ListAdapter<DictEntry, JDictListAdapter.JDictViewHolder> (DiffCallback){

    class JDictViewHolder (
        private var binding: ListItemDictEntryBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(dictEntry: DictEntry) {

            Log.d("ADAPTERLIST", "HI BIND")
            var word = ""
            binding.dictEntry = dictEntry
            if(!dictEntry.reading.isNullOrEmpty()) {
                word = dictEntry.reading[0] }
            if(!dictEntry.kanji.isNullOrEmpty()) {
                word = dictEntry.kanji[0] + ", " + word}

            binding.word.text = word
            binding.definition.text = dictEntry.gloss.joinToString(", ")
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback: DiffUtil.ItemCallback<DictEntry>() {
        override fun areItemsTheSame(oldItem: DictEntry, newItem: DictEntry): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DictEntry, newItem: DictEntry): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JDictViewHolder {

        Log.d("ADAPTERLIST", "HI ONCREATE")
        val layoutInflater = LayoutInflater.from(parent.context)
        return JDictViewHolder(
            ListItemDictEntryBinding.inflate(layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: JDictViewHolder, position: Int) {

        Log.d("ADAPTERLIST", "HI ONBIND")
        val dictEntry = getItem(position)
        holder.itemView.setOnClickListener{
            clickListener(dictEntry)
        }
        holder.bind(dictEntry)
    }
}