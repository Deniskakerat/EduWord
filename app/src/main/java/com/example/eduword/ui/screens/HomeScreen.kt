package com.example.eduword.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onFlashcards: () -> Unit,
    onSpelling: () -> Unit,
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("EduWord") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Німецька • v0.1", style = MaterialTheme.typography.titleMedium)

            ElevatedButton(
                onClick = onFlashcards,
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) { Text("Повторення (картки)") }

            ElevatedButton(
                onClick = onSpelling,
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) { Text("Правопис (артикль + слово)") }

            Spacer(Modifier.weight(1f))
            AssistChip(onClick = {}, label = { Text("Prototype") })
        }
    }
}
