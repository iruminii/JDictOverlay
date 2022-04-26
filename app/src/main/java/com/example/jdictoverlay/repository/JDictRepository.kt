package com.example.jdictoverlay.repository

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.asLiveData
import com.example.jdictoverlay.data.JDictDao
import com.example.jdictoverlay.model.DictEntry

class JDictRepository (
    private val jDictDao: JDictDao
) {
    val allEntries : LiveData<List<DictEntry>> = jDictDao.getJDict().asLiveData()

    fun getEntryKanji(id: String) : LiveData<List<String>> {
        return jDictDao.getKanjiById(id).asLiveData()
    }
    fun getEntryReading(id: String) : LiveData<List<String>> {
        return jDictDao.getReadingById(id).asLiveData()
    }

    fun getEntryPos(id: String) : LiveData<List<String>> {
        return jDictDao.getPosById(id).asLiveData()
    }

    fun getEntryGloss(id: String) : LiveData<List<String>> {
        return jDictDao.getGlossById(id).asLiveData()
    }

    fun getEntryById(id: String) : LiveData<DictEntry> {
        return jDictDao.getEntryById(id).asLiveData()
    }

    private val searchStringLiveData = MutableLiveData<String>("") //we can add initial value directly in the constructor
    val mappedEntries: LiveData<List<DictEntry>> = Transformations.switchMap(searchStringLiveData) {
            string ->
        if(TextUtils.isEmpty(string)) {
            Log.d("VIEWMODEL", "ISEMPTY")
            jDictDao.getNothing().asLiveData()
            //allEntries
        }
        else {
            Log.d("VIEWMODEL", "MAPPING + " + string)
            //  jDictDao.searchAll(SimpleSQLiteQuery("SELECT * FROM jdict_database WHERE kanji LIKE ? OR LOWER(kanji) LIKE LOWER(?) OR reading LIKE ? OR LOWER(reading) LIKE LOWER(?) OR gloss LIKE ? OR LOWER(gloss) LIKE LOWER(?)", arrayOf<Any>(string)))
            jDictDao.searchAll(string)

        }
    }
    fun searchChanged(string: String) {
        searchStringLiveData.value = string
        Log.d("VIEWMODEL", "SEARCH CHANGED")
    }
}