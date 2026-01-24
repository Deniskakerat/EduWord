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
fun ArticleQuizScreen(repo: WordRepository) {
    val scope = rememberCoroutineScope()
    var current by remember { mutableStateOf<WordEntity?>(null) }
    var flipped by remember { mutableStateOf(false) }
    var feedback by remember { mutableStateOf<String?>(null) }
    var topics by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedTopic by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        topics = repo.topics()
    }

    LaunchedEffect(Unit) { current = repo.randomWord() }

    fun next() {

        flipped = false
        feedback = null
        scope.launch {
            current = repo.randomWord(selectedTopic)
        }
    }

    fun answer(chosen: String) {

        val w = current ?: return
        val correct = (w.article ?: "").trim() == chosen
        scope.launch {
            repo.recordAttempt(w.id, correct)
        }
        feedback = if (correct) "✅ Правильно" else "❌ Ні, правильно: ${w.article}"
        // можна автоматично йти далі через 0.7с, але поки залишимо кнопку "Далі"
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
                    next() // перезавантажити слово під новий topic
                    })

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
                        Text("Який артикль?", style = MaterialTheme.typography.labelMedium)
                        Spacer(Modifier.height(8.dp))
                        Text(w.lemma, style = MaterialTheme.typography.headlineMedium)
                    } else {
                        Text("Переклад", style = MaterialTheme.typography.labelMedium)
                        Spacer(Modifier.height(8.dp))
                        Text(w.ukTranslation, style = MaterialTheme.typography.headlineMedium)
                        if (w.plural != null) {
                            Spacer(Modifier.height(8.dp))
                            Text("Plural: ${w.plural}", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("der","die","das").forEach { a ->
                    Button(
                        onClick = { answer(a) },
                        modifier = Modifier.weight(1f).height(52.dp)
                    ) { Text(a) }
                }
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
