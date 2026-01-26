package com.example.eduword.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.eduword.data.entity.WordEntity
import com.example.eduword.data.repository.WordRepository
import com.example.eduword.ui.settings.AppSettings
import kotlinx.coroutines.launch
import com.example.eduword.data.entity.WordProgressEntity
import com.example.eduword.ui.components.KnowledgeIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleQuizScreen(repo: WordRepository) {
    val scope = rememberCoroutineScope()
    var current by remember { mutableStateOf<WordEntity?>(null) }
    var flipped by remember { mutableStateOf(false) }
    var feedback by remember { mutableStateOf<String?>(null) }
    var topics by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedTopic by remember { mutableStateOf<String?>(null) }
    var progress by remember { mutableStateOf<WordProgressEntity?>(null) }

    val streak = progress?.correctStreak ?: 0

    LaunchedEffect(Unit) {
        topics = repo.topics()
    }

    LaunchedEffect(current?.id) {
        val id = current?.id ?: return@LaunchedEffect
        progress = repo.progress(id)
    }

    fun next() {
        flipped = false
        feedback = null
        scope.launch {
            current = repo.randomWord(selectedTopic)
        }
    }

    fun answer(chosen: String) {
        val w = current ?: return
        val correctArticle = w.article ?: ""
        val isCorrect = if (chosen == "Keine") correctArticle.isBlank() else chosen == correctArticle

        scope.launch {
            repo.recordAttempt(w.id, isCorrect)
        }
        feedback = if (isCorrect) "✅ Правильно" else "❌ Ні, правильно: ${correctArticle.ifBlank { "Keine" }}"
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Артиклі (тест)") }) }) { padding ->
        Column(
            Modifier.padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TopicPicker(
                topics = topics,
                selected = selectedTopic,
                onSelect = { t ->
                    selectedTopic = t
                    next()
                })

            val w = current
            if (w == null) {
                Text("Немає слів у базі.")
                return@Column
            }
            KnowledgeIndicator(streak)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clickable { flipped = !flipped }
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.Center) {
                    if (!flipped) {
                        Text("Який артикль?", style = MaterialTheme.typography.labelMedium)
                        Spacer(Modifier.height(8.dp))
                        Text(w.lemma, style = MaterialTheme.typography.headlineMedium)
                    } else {
                        Text("Переклад", style = MaterialTheme.typography.labelMedium)
                        Spacer(Modifier.height(8.dp))
                        val translation = if (AppSettings.currentLanguage == "EN") w.engTranslation else w.ukTranslation
                        Text(translation, style = MaterialTheme.typography.headlineMedium)
                        if (w.plural != null) {
                            Spacer(Modifier.height(8.dp))
                            Text("Plural: ${w.plural}", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("der", "die", "das").forEach { a ->
                        Button(
                            onClick = { answer(a) },
                            modifier = Modifier.weight(1f).height(52.dp)
                        ) { Text(a.replaceFirstChar(Char::titlecase)) }
                    }
                }
                Button(
                    onClick = { answer("Keine") },
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) { Text("Keine") }
            }

            if (feedback != null) {
                Card(Modifier.fillMaxWidth()) {
                    Text(feedback!!, Modifier.padding(16.dp))
                }
                Button(onClick = { next() }, modifier = Modifier.fillMaxWidth().height(52.dp)) {
                    Text("Далі")
                }
            }

            Text("Тап по картці → показати переклад", style = MaterialTheme.typography.bodySmall)
        }
    }
}
