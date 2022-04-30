package com.example.jdictoverlay.model

import androidx.room.*

@Entity(tableName = "jdict_database")
data class DictEntry (
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "kanji")
    val kanji: List<String>,
    @ColumnInfo(name = "reading")
    val reading: List<String>,
    @ColumnInfo(name = "pos")
    val pos: List<String>,
    @ColumnInfo(name = "dial")
    val dial: String,
    @ColumnInfo(name = "gloss")
    val gloss: List<String>,
    @ColumnInfo(name = "ex_text")
    val ex_text: String,
    @ColumnInfo(name = "ex_sent_j")
    val ex_sent_j: String,
    @ColumnInfo(name = "ex_sent_e")
    val ex_sent_e: String
)

@Entity(tableName = "jdict_database_fts")
@Fts4(contentEntity = DictEntry::class)
data class DictEntryFTS(
    @ColumnInfo(name = "kanji")
    val kanji: List<String>,
    @ColumnInfo(name= "reading")
    val reading: List<String>,
    @ColumnInfo(name = "gloss")
    val gloss: List<String>
)

data class DictEntryWithMatchInfo(
    @Embedded
    val dictEntry: DictEntry,
    @ColumnInfo(name = "matchInfo")
    val matchInfo: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if(this == other) return true
        if(javaClass != other?.javaClass) return false

        other as DictEntryWithMatchInfo

        if(dictEntry != other.dictEntry) return false
        if(!matchInfo.contentEquals(other.matchInfo)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = dictEntry.hashCode()
        result = 31 * result + matchInfo.contentHashCode()
        return result
    }
}