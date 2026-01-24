package com.example.eduword.ui.screens

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
fun SpellingScreen(repo: WordRepository) {
    val scope = rememberCoroutineScope()

    var current by remember { mutableStateOf<WordEntity?>(null) }
    var article by remember { mutableStateOf("der") }
    var input by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) { current = repo.randomWord() }

    fun next() {
        result = null
        input = ""
        article = "der"
        scope.launch { current = repo.randomWord() }
    }

    fun check(w: WordEntity) {
        val correctLemma = w.lemma.trim()
        val correctArticle = (w.article ?: "").trim()

        val lemmaOk = input.trim().equals(correctLemma, ignoreCase = true)
        val articleOk = if (correctArticle.isBlank()) true else article == correctArticle

        result = when {
            lemmaOk && articleOk -> "✅ Правильно!"
            !articleOk && lemmaOk -> "⚠️ Слово правильно, артикль ні. Правильно: $correctArticle"
            articleOk && !lemmaOk -> "⚠️ Артикль правильно, слово ні. Правильно: ${w.lemma}"
            else -> "❌ Неправильно. Правильно: ${w.article ?: ""} ${w.lemma}".trim()
        }
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Правопис") }) }) { padding ->
        Column(
            Modifier.padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val w = current
            if (w == null) {
                Text("Немає слів у базі.")
                return@Column
            }

            Text("Переклад:", style = MaterialTheme.typography.labelMedium)
            Text(w.ukTranslation, style = MaterialTheme.typography.headlineSmall)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("der", "die", "das").forEach { a ->
                    FilterChip(
                        selected = article == a,
                        onClick = { article = a },
                        label = { Text(a) }
                    )
                }
            }

            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                label = { Text("Введи слово німецькою") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = { check(w) },
                    modifier = Modifier.weight(1f).height(52.dp)
                ) { Text("Перевірити") }

                Button(
                    onClick = { next() },
                    modifier = Modifier.weight(1f).height(52.dp)
                ) { Text("Далі") }
            }

            if (result != null) {
                Card(Modifier.fillMaxWidth()) {
                    Text(result!!, Modifier.padding(16.dp))
                }
            }

            if (w.plural != null) {
                Text("Підказка (Plural): ${w.plural}", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
