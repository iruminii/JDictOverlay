package com.example.jdictoverlay.data

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.jdictoverlay.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface JDictDao {

    @Query("SELECT * FROM jdict_database WHERE 1=0")
    fun getNothing(): Flow<List<DictEntry>>

    // get all entries
    @Query("SELECT * FROM jdict_database")
    fun getJDict(): Flow<List<DictEntry>>

    // get entry by id
    @Query("SELECT * FROM jdict_database WHERE id = :id")
    fun getEntryById(id: String) : Flow<DictEntry>

    // get kanji by id
    @Query("SELECT kanji FROM jdict_database WHERE id = :id")
    fun getKanjiById(id: String) : Flow<List<String>>

    // get reading by id
    @Query("SELECT reading FROM jdict_database WHERE id = :id")
    fun getReadingById(id: String) : Flow<List<String>>

    // get pos by id
    @Query("SELECT pos FROM jdict_database WHERE id = :id")
    fun getPosById(id: String) : Flow<List<String>>

    // get gloss by id
    @Query("SELECT gloss FROM jdict_database WHERE id = :id")
    fun getGlossById(id: String) : Flow<List<String>>

    // search kanji
    @Query("SELECT * FROM jdict_database WHERE kanji LIKE :input OR LOWER(kanji) LIKE LOWER(:input)")
    fun searchKanji(input: String) : LiveData<List<DictEntry>>

    // search reading
    @Query("SELECT * FROM jdict_database WHERE reading LIKE :input OR LOWER(reading) LIKE LOWER(:input)")
    fun searchReading(input: String) : LiveData<List<DictEntry>>

    // search gloss
    @Query("SELECT * FROM jdict_database WHERE gloss LIKE :input OR LOWER(gloss) LIKE LOWER(:input)")
    fun searchGloss(input: String) : LiveData<List<DictEntry>>

    // search all
    @Query("SELECT * FROM jdict_database WHERE kanji LIKE '%' || :input || '%' " +
            "OR LOWER(kanji) LIKE LOWER('%' || :input || '%') " +
            "OR reading LIKE '%' || :input || '%' " +
            "OR LOWER(reading) LIKE LOWER('%' || :input || '%') " +
            "OR gloss LIKE '%' || :input || '%' " +
            "OR LOWER(gloss) LIKE LOWER('%' || :input || '%') ")
    fun searchAll(input: String) : LiveData<List<DictEntry>>

    @Insert(onConflict = REPLACE)
    fun insertEntry(entry: DictEntry)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllEntries(entries: List<DictEntry>)

    // get count of database
    @Query("SELECT COUNT(id) FROM jdict_database")
    fun count() : LiveData<Int>
}