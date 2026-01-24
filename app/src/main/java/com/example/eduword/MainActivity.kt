package com.example.eduword

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.room.Room
import com.example.eduword.data.db.AppDatabase
import com.example.eduword.data.repository.WordRepository
import com.example.eduword.data.seed.SeedWords
import com.example.eduword.ui.nav.EduNavHost
import com.example.eduword.ui.theme.EduWordTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val db by lazy {
        Room.databaseBuilder(applicationContext, AppDatabase::class.java, "eduword.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    private val repo by lazy { WordRepository(db.wordDao()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        CoroutineScope(Dispatchers.IO).launch {
            repo.seedMerge(SeedWords.germanA1)
        }

        setContent {
            EduWordTheme {
                EduNavHost(repo = repo)
            }
        }
    }
}
