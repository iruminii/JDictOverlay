package com.example.jdictoverlay.data

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.jdictoverlay.model.*
import kotlinx.coroutines.*
import java.util.concurrent.Executors

@Database (entities = [DictEntry::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class JDictDatabase : RoomDatabase() {
    abstract fun jDictDao() : JDictDao

    val dbScope = CoroutineScope(Job() + Dispatchers.IO)

    companion object {
        @Volatile
        private var INSTANCE : JDictDatabase? = null

        fun getDatabase(context: Context) :JDictDatabase {
            Log.d("Hi", "JDICTDATABASE getDatabase")
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    JDictDatabase::class.java,
                    "entry_database"
                )
                    .addCallback(object: Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            Executors.newSingleThreadExecutor().execute() {
                                JDictDatabase.INSTANCE?.fillDatabase()
                            }
                        }
                    })
                    .fallbackToDestructiveMigration()
                    .build()
                //instance.fillDatabase()
                Log.d("Hi", "database file filleddatabase")
                INSTANCE = instance
                return instance
            }
        }

    }


    private fun fillDatabase() {
        dbScope.launch {
            val fileData = ImportJData().getDataFromFile()
            jDictDao().insertAllEntries(fileData)
        }
    }
}