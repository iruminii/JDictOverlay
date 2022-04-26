package com.example.jdictoverlay.data

import android.util.Log
import com.example.jdictoverlay.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

class ImportJData {
    //val fileName : String = "src/main/assets/JMDict_first_eleven.xml"
    //val inputStream : InputStream = FileInputStream(fileName!!)
    val inputStream: InputStream = javaClass.getResourceAsStream("/assets/JMDict_test.xml")

    fun getDataFromFile() : List<DictEntry> = runBlocking {
        Log.d("Hi", "fillDatabase")
        val test = ParseJData().parse(inputStream)
        // get all values from XML and save in list
        inputStream.close()
        return@runBlocking test ?: emptyList()
        //return@runBlocking ParseJData().parse(inputStream) ?: emptyList()
    }

}