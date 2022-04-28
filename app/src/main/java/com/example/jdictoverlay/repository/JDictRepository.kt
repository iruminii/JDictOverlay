package com.example.jdictoverlay.repository

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.*
import com.example.jdictoverlay.data.JDictDao
import com.example.jdictoverlay.model.DictEntry
import com.example.jdictoverlay.ui.CharacterConverter

class JDictRepository (
    private val jDictDao: JDictDao
) {
    private val searchStringLiveData = MutableLiveData<String>("") //we can add initial value directly in the constructor

    val mediatorLiveData = MediatorLiveData<List<DictEntry>>()
    val combinedLiveData = MediatorLiveData<List<DictEntry>>()

    var combinedSearchData: LiveData<List<DictEntry>> = MutableLiveData()
    var searchLiveData: LiveData<List<DictEntry>> = MutableLiveData()
    var searchHiraganaLiveData: LiveData<List<DictEntry>> = MutableLiveData()
    var searchFullKataLiveData: LiveData<List<DictEntry>> = MutableLiveData()
    var searchHalfKataLiveData: LiveData<List<DictEntry>> = MutableLiveData()

    var searchHiraganaFullLiveData: LiveData<List<DictEntry>> = MutableLiveData()
    var searchFullKataFullLiveData: LiveData<List<DictEntry>> = MutableLiveData()
    var searchHalfKataFullLiveData: LiveData<List<DictEntry>> = MutableLiveData()

    init {
        searchLiveData = Transformations.switchMap(searchStringLiveData) {
                string ->
            if(TextUtils.isEmpty(string)) {
                jDictDao.getNothing().asLiveData()
            }
            else {
                jDictDao.searchAll(string)
            }
        }
        mediatorLiveData.addSource(searchLiveData) {
            mediatorLiveData.value = it
        }

        searchHiraganaLiveData = Transformations.switchMap(searchStringLiveData) {
                string ->
            if(TextUtils.isEmpty(string)) {
                jDictDao.getNothing().asLiveData()
            }
            else {
                jDictDao.searchAll(CharacterConverter().convertToHiragana(string))
            }
        }
        mediatorLiveData.addSource(searchHiraganaLiveData) {
            mediatorLiveData.value = it
        }

        searchFullKataLiveData = Transformations.switchMap(searchStringLiveData) {
                string ->
            if(TextUtils.isEmpty(string)) {
                jDictDao.getNothing().asLiveData()
            }
            else {
                jDictDao.searchAll(CharacterConverter().convertToFullKatakana(string))
            }
        }
        mediatorLiveData.addSource(searchFullKataLiveData) {
            mediatorLiveData.value = it
        }

        searchHalfKataLiveData = Transformations.switchMap(searchStringLiveData) {
                string ->
            if(TextUtils.isEmpty(string)) {
                jDictDao.getNothing().asLiveData()
            }
            else {
                jDictDao.searchAll(CharacterConverter().convertToHalfKatakana(string))
            }
        }
        mediatorLiveData.addSource(searchHalfKataLiveData) {
            mediatorLiveData.value = it
        }
        searchHiraganaFullLiveData = Transformations.switchMap(searchStringLiveData) {
                string ->
            if(TextUtils.isEmpty(string)) {
                jDictDao.getNothing().asLiveData()
            }
            else {
                jDictDao.searchAll(CharacterConverter().convertToHiraganaFull(string))
            }
        }
        mediatorLiveData.addSource(searchHiraganaFullLiveData) {
            mediatorLiveData.value = it
        }

        searchFullKataFullLiveData = Transformations.switchMap(searchStringLiveData) {
                string ->
            if(TextUtils.isEmpty(string)) {
                jDictDao.getNothing().asLiveData()
            }
            else {
                jDictDao.searchAll(CharacterConverter().convertToFullKatakanaFull(string))
            }
        }
        mediatorLiveData.addSource(searchFullKataFullLiveData) {
            mediatorLiveData.value = it
        }

        searchHalfKataFullLiveData = Transformations.switchMap(searchStringLiveData) {
                string ->
            if(TextUtils.isEmpty(string)) {
                jDictDao.getNothing().asLiveData()
            }
            else {
                jDictDao.searchAll(CharacterConverter().convertToHalfKatakanaFull(string))
            }
        }
        mediatorLiveData.addSource(searchHalfKataFullLiveData) {
            mediatorLiveData.value = it
        }
        combinedSearchData = Transformations.switchMap(searchStringLiveData) {
                string ->
            if(TextUtils.isEmpty(string)) {
                jDictDao.getNothing().asLiveData()
            }
            else {
                mergeSearch(string)
            }
        }
        combinedLiveData.addSource(combinedSearchData) {
            combinedLiveData.value = it
        }
    }

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

    val mappedEntries: LiveData<List<DictEntry>> = Transformations.switchMap(searchStringLiveData) {
            string ->
        if(TextUtils.isEmpty(string)) {
            jDictDao.getNothing().asLiveData()
        }
        else {
            Log.d("HI", "jDictDao.searchAll ${jDictDao.searchAll(string).value}")
            jDictDao.searchAll(string)
        }
    }


    private fun mergeSearch(string: String) : LiveData<List<DictEntry>> {
       val searchResults : List<DictEntry> = mediatorLiveData.value ?: listOf<DictEntry>()
       val searchInput = jDictDao.searchAll(string).value
       val searchHira = jDictDao.searchAll(CharacterConverter().convertToHiragana(string)).value
       val searchFullKata = jDictDao.searchAll(CharacterConverter().convertToFullKatakana(string)).value
       val searchHalfKata = jDictDao.searchAll(CharacterConverter().convertToHalfKatakana(string)).value
       val list: MutableList<DictEntry> = ArrayList()
        if (searchInput != null) {
            list.addAll(searchInput)
        }
        if (searchHira != null) {
            list.addAll(searchHira)
        }
        Log.d("HI", "mediatorLiveData.value = ${mediatorLiveData.value}")
       Log.d("HI", "searchResults = $searchResults")
       val newLiveData: MutableLiveData<List<DictEntry>> = MutableLiveData()
       //newLiveData.value = searchResults.distinct().toList()
       newLiveData.value = list.distinct().toList()
       Log.d("HI", "newLiveData = ${newLiveData.value}")
       return newLiveData
    }

    fun searchChanged(string: String) {
        searchStringLiveData.value = string
        Log.d("VIEWMODEL", "SEARCH CHANGED")
    }
}