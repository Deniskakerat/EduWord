package com.example.eduword.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.eduword.data.entity.WordEntity
import com.example.eduword.data.repository.WordRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardsScreen(repo: WordRepository) {
    val scope = rememberCoroutineScope()

    var current by remember { mutableStateOf<WordEntity?>(null) }
    var flipped by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { current = repo.randomWord() }

    fun next() {
        flipped = false
        scope.launch { current = repo.randomWord() }
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Картки") }) }) { padding ->
        Column(
            Modifier.padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val w = current
            if (w == null) {
                Text("Немає слів у базі.")
                return@Column
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clickable { flipped = !flipped }
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.Center) {
                    if (!flipped) {
                        Text("Питання", style = MaterialTheme.typography.labelMedium)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "${w.article ?: ""} ${w.lemma}".trim(),
                            style = MaterialTheme.typography.headlineMedium
                        )
                    } else {
                        Text("Відповідь", style = MaterialTheme.typography.labelMedium)
                        Spacer(Modifier.height(8.dp))
                        Text(w.ukTranslation, style = MaterialTheme.typography.headlineMedium)
                        if (w.plural != null) {
                            Spacer(Modifier.height(8.dp))
                            Text("Plural: ${w.plural}", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = { /* TODO later: mark fail */ next() },
                    modifier = Modifier.weight(1f).height(52.dp)
                ) { Text("Не знаю") }

                Button(
                    onClick = { /* TODO later: mark success */ next() },
                    modifier = Modifier.weight(1f).height(52.dp)
                ) { Text("Знаю") }
            }

            Text("Тап по картці → переворот", style = MaterialTheme.typography.bodySmall)
        }
    }
}
