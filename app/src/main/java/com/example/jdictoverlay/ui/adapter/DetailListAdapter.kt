package com.example.jdictoverlay.ui.adapter


import android.annotation.SuppressLint
import android.content.Context
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.bold
import androidx.core.text.underline
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.jdictoverlay.databinding.*
import com.example.jdictoverlay.model.DetailEntry

const val VIEW_TYPE_KANJI = 1
const val VIEW_TYPE_READING = 2
const val VIEW_TYPE_POS = 3
const val VIEW_TYPE_DIAL = 4
const val VIEW_TYPE_GLOSS = 5
const val VIEW_TYPE_EXAMPLE = 6

class DetailListAdapter(
    private val details : List<DetailEntry>

) : ListAdapter<DetailEntry, RecyclerView.ViewHolder> (DiffCallback){

    open class DetailKanjiViewHolder (
        private var binding: DetailItemKanjiBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(kanji: String) {
            Log.d("ADAPTER", "HI KANJI")
            binding.detailKanji.text = kanji
            binding.executePendingBindings()
        }
    }
    open class DetailReadingViewHolder (
        private var binding: DetailItemReadingBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(reading: String) {

            Log.d("ADAPTER", "HI READING")
            binding.detailReading.text = reading
            binding.executePendingBindings()
        }
    }
    open class DetailPosViewHolder (
        private var binding: DetailItemPosBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(pos: String) {
            binding.detailPos.text = pos
            binding.executePendingBindings()
        }
    }

    open class DetailDialViewHolder (
        private var binding: DetailItemDialBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(dial: String) {
            binding.detailDial.text = dial
            binding.executePendingBindings()
        }
    }
    open class DetailGlossViewHolder (
        private var binding: DetailItemGlossBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(gloss: String) {
            binding.detailGloss.text = gloss
            binding.executePendingBindings()
        }
    }

    open class DetailExampleViewHolder (
        private var binding: DetailItemExampleBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(examplet: String, examplej: String, examplee: String) {
            val pattern = Regex(examplet)
            val matchedSent : List<String> = pattern.split(examplej)
            var newSent = SpannableStringBuilder().append(matchedSent[0])
            if(matchedSent.size > 1) {
                matchedSent.drop(1).forEach { word ->
                    newSent
                        .underline { bold { append(examplet) } }
                        .append(word)} }

            binding.detailExampleJ.text = newSent
            binding.detailExampleE.text = examplee
            binding.executePendingBindings()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return details[position].viewType
    }

    override fun getItemCount(): Int {
        return details.size
    }

    companion object DiffCallback: DiffUtil.ItemCallback<DetailEntry>() {
        override fun areItemsTheSame(oldItem: DetailEntry, newItem: DetailEntry): Boolean {
            return oldItem.detailString == newItem.detailString
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: DetailEntry, newItem: DetailEntry): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        when(viewType) {
            VIEW_TYPE_KANJI -> {
                Log.d("ADAPTER", "HI KANJI ONCREATE")
                return DetailKanjiViewHolder(
                    DetailItemKanjiBinding.inflate(layoutInflater, parent, false)
                ) }
            VIEW_TYPE_POS -> { return DetailPosViewHolder(
                DetailItemPosBinding.inflate(layoutInflater, parent, false)
            ) }
            VIEW_TYPE_DIAL -> { return DetailDialViewHolder(
                DetailItemDialBinding.inflate(layoutInflater, parent, false)
            ) }
            VIEW_TYPE_GLOSS -> { return DetailGlossViewHolder(
                DetailItemGlossBinding.inflate(layoutInflater, parent, false)
            ) }
            VIEW_TYPE_EXAMPLE -> {

                Log.d("DETAILADAPTER", "HI KANJI ONCREATE")
                return DetailExampleViewHolder(
                    DetailItemExampleBinding.inflate(layoutInflater, parent, false)
                ) }
        }

        Log.d("ADAPTER", "HI READING ONCREATE")
        return DetailReadingViewHolder(
            DetailItemReadingBinding.inflate(layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        Log.d("ADAPTER", "HI ONBIND")
        when(holder.itemViewType) {
            VIEW_TYPE_KANJI -> (holder as DetailKanjiViewHolder).bind(details[position].detailString)
            VIEW_TYPE_READING -> (holder as DetailReadingViewHolder).bind(details[position].detailString)
            VIEW_TYPE_POS -> (holder as DetailPosViewHolder).bind(details[position].detailString)
            VIEW_TYPE_DIAL -> (holder as DetailDialViewHolder).bind(details[position].detailString)
            VIEW_TYPE_GLOSS -> (holder as DetailGlossViewHolder).bind(details[position].detailString)
            VIEW_TYPE_EXAMPLE -> (holder as DetailExampleViewHolder).bind(details[position].detailString, details[position].exStringJ, details[position].exStringE)
        }
    }
}