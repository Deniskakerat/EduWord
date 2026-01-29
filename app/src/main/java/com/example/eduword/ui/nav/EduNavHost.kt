package com.example.eduword.ui.nav

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.eduword.data.ocr.OcrResultHolder
import com.example.eduword.data.repository.WordRepository
import com.example.eduword.ui.screens.*

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
        composable(Routes.SCAN) {
            ScanScreen(onResult = {
                OcrResultHolder.ocrResult = it
                nav.navigate(Routes.IMPORT_REVIEW)
            })
        }
        composable(Routes.IMPORT_REVIEW) {
            val rows = OcrResultHolder.ocrResult ?: emptyList()
            ImportReviewScreen(repo = repo, initial = rows, onDone = { nav.popBackStack() })
        }
    }
}
