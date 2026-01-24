package com.example.eduword.ui.nav

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.eduword.data.repository.WordRepository
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
                onSpelling = { nav.navigate(Routes.SPELLING) }
            )
        }
        composable(Routes.FLASHCARDS) { FlashcardsScreen(repo = repo) }
        composable(Routes.SPELLING) { SpellingScreen(repo = repo) }
    }
}
