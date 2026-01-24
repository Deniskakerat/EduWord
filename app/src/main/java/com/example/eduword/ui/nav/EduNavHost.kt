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

@Composable
fun EduNavHost(repo: WordRepository) {
    val nav = rememberNavController()

    NavHost(navController = nav, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(
                onFlashcards = { nav.navigate(Routes.FLASHCARDS) },
                onSpelling = { nav.navigate(Routes.SPELLING) },
                onAddWord = { nav.navigate(Routes.ADD_WORD) },
                onArticle = { nav.navigate(Routes.ARTICLE_QUIZ) }
            )
        }
        composable(Routes.ADD_WORD) { AddWordScreen(repo) }
        composable(Routes.ARTICLE_QUIZ) { ArticleQuizScreen(repo) }
        composable(Routes.FLASHCARDS) { FlashcardsScreen(repo = repo) }
        composable(Routes.SPELLING) { SpellingScreen(repo = repo) }
    }
}

