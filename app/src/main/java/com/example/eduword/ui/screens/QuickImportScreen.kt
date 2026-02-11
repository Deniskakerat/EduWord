package com.example.eduword.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.eduword.data.entity.WordEntity
import com.example.eduword.data.ocr.DetectedWordRow
import com.example.eduword.data.repository.WordRepository
import com.example.eduword.ui.nav.Routes
import com.example.eduword.ui.screens.WordListScreen
import kotlinx.coroutines.launch
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickImportScreen(
    navController: NavController,
    onParsed: (List<String>) -> Unit
) {
    var text by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Quick import") }) }
    ) { padding ->
        Column(
            Modifier.padding(padding).fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.fillMaxWidth().weight(1f),
                label = { Text("Paste words (one per line)") },
                placeholder = {
                    Text(
                        "• anfassen\n• bemalen\n• berichten\n• bieten\n• einladen\n• entdecken"
                    )
                }
            )

            Button(
                onClick = {
                    val words = parseWordList(text)
                    onParsed(words)
                    navController.navigate(Routes.TABLE_EDITOR)
                },
                enabled = text.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) { Text("Next") }
        }
    }
}

private fun parseWordList(input: String): List<String> {
    return input
        .lines()
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .map { line ->
            // прибираємо маркери типу "•", "-", "*", "1."
            line.replace(Regex("""^[\u2022\-\*\d\.\)\(]+\s*"""), "")
                .trim()
        }
        .filter { it.length >= 2 }
        .distinct()
}

