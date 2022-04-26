package com.example.jdictoverlay.model

import androidx.room.*

@Entity(tableName = "jdict_database")
data class DictEntry (
    @PrimaryKey
    val id: String,
    val kanji: List<String>,
    val reading: List<String>,
    val pos: List<String>,
    val dial: String,
    val gloss: List<String>,
    val ex_text: String,
    val ex_sent_j: String,
    val ex_sent_e: String
)