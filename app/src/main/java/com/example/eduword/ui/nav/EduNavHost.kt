package com.example.eduword.ui.nav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.eduword.data.entity.WordRowUi
import com.example.eduword.data.ocr.OcrResultHolder
import com.example.eduword.data.repository.WordRepository
import com.example.eduword.ui.screens.*
import java.util.UUID
import kotlin.collections.emptyList


@Composable
fun EduNavHost(repo: WordRepository) {
    val nav = rememberNavController()

    var pendingWords by remember { mutableStateOf<List<String>>(emptyList()) }
    var pendingRows by remember { mutableStateOf<List<WordRowUi>>(emptyList()) }

    NavHost(navController = nav, startDestination = Routes.HOME) {

        composable(Routes.HOME) {
            HomeScreen(navController = nav)
        }
        // ✅ ADD THESE
        composable(Routes.SPELLING) {
            SpellingScreen(repo = repo) // or a placeholder
        }
        composable(Routes.ARTICLE_QUIZ) {
            ArticleQuizScreen(repo = repo) // or a placeholder
        }

        composable(Routes.ADD_WORD) {
            AddWordScreen(repo = repo) // or a placeholder
        }
        composable(Routes.WORD_LIST) {
            WordListScreen(repo = repo) // or a placeholder
        }
        composable(Routes.SCAN) {
            ScanScreen(
                    onResult = { rows ->
                        // TODO: do something with rows
                        // Example: go back to home after scan:
                        nav.popBackStack()
                    }
            )
        }

        composable(Routes.FLASHCARDS) {
            FlashcardsScreen(repo = repo) // or a placeholder
        }

        composable(Routes.QUICK_IMPORT) {
            QuickImportScreen(
                navController = nav,
                onParsed = { words -> pendingWords = words }
            )
        }

        composable(Routes.TABLE_EDITOR) {
            LaunchedEffect(pendingWords) {
                pendingRows = pendingWords.map { w ->
                    WordRowUi(
                        id = UUID.randomUUID().toString(),
                        original = w,
                        selected = true,
                        article = null,
                        lemma = w.trim(),
                        plural = null,
                        uk = "",
                        en = "",
                        topic = null,
                        level = null
                    )
                }
            }

            // If you want to use pendingRows, pass them instead of words later.
            WordTableEditorScreen(
                repo = repo,
                words = pendingWords,
                onDone = { nav.popBackStack(Routes.HOME, false) }
            )
        }
    }
}
