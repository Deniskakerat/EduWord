package com.example.eduword.ui.nav

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.eduword.data.repository.WordRepository
import com.example.eduword.ui.screens.AddWordScreen
import com.example.eduword.ui.screens.ArticleQuizScreen
import com.example.eduword.ui.screens.FlashcardsScreen
import com.example.eduword.ui.screens.HomeScreen
import com.example.eduword.ui.screens.SpellingScreen
import com.example.eduword.ui.screens.WordListScreen

@Composable
fun EduNavHost(repo: WordRepository) {
    val nav = rememberNavController()

    NavHost(navController = nav, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(navController = nav)
        }
        composable(Routes.ADD_WORD) { AddWordScreen(repo) }
        composable(Routes.ARTICLE_QUIZ) { ArticleQuizScreen(repo) }
        composable(Routes.FLASHCARDS) { FlashcardsScreen(repo = repo) }
        composable(Routes.SPELLING) { SpellingScreen(repo = repo) }
        composable(Routes.WORD_LIST) { WordListScreen(repo) }
    }
}
