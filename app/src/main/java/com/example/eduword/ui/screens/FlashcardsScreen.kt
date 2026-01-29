package com.example.eduword.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.eduword.data.entity.WordEntity
import com.example.eduword.data.entity.WordProgressEntity
import com.example.eduword.data.repository.WordRepository
import com.example.eduword.ui.components.KnowledgeIndicator
import com.example.eduword.ui.settings.AppSettings
import com.example.eduword.ui.strings.strings
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardsScreen(repo: WordRepository) {
    val scope = rememberCoroutineScope()

    var progress by remember { mutableStateOf<WordProgressEntity?>(null) }
    var topics by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedTopic by remember { mutableStateOf<String?>(null) }
    var current by remember { mutableStateOf<WordEntity?>(null) }
    var flipped by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        topics = repo.topics()
        current = repo.randomWord()
    }

    LaunchedEffect(current?.id) {
        val id = current?.id ?: return@LaunchedEffect
        progress = repo.progress(id)
    }

    fun next() {
        flipped = false
        progress = null
        scope.launch {
            current = repo.randomWord(selectedTopic)
        }
    }

    Scaffold(topBar = { TopAppBar(title = { Text(strings.flashcards) }) }) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val w = current
            if (w == null) {
                Text(strings.noWordsInDb)
                return@Column
            }

            TopicPicker(
                topics = topics,
                selected = selectedTopic,
                onSelect = { t ->
                    selectedTopic = t
                    next()
                })

            val streak = progress?.correctStreak ?: 0
            KnowledgeIndicator(streak = streak)
            Text(strings.knowledge(streak), style = MaterialTheme.typography.bodySmall)

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clickable { flipped = !flipped }
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.Center) {
                        if (!flipped) {
                            Text(strings.question, style = MaterialTheme.typography.labelMedium)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "${w.article ?: ""} ${w.lemma}".trim(),
                                style = MaterialTheme.typography.headlineMedium
                            )
                        } else {
                            Text(strings.answer, style = MaterialTheme.typography.labelMedium)
                            Spacer(Modifier.height(8.dp))
                            val translation = if (AppSettings.currentLanguage == "EN") w.engTranslation else w.ukTranslation
                            Text(translation, style = MaterialTheme.typography.headlineMedium)
                            if (w.plural != null) {
                                Spacer(Modifier.height(8.dp))
                                Text(strings.plural(w.plural), style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            repo.markFail(w.id)
                            progress = repo.progress(w.id)
                            delay(300)
                            next()
                        }
                    },
                    modifier = Modifier.weight(1f).height(52.dp)
                ) { Text(strings.iDontKnow) }

                Button(
                    onClick = {
                        scope.launch {
                            repo.markSuccess(w.id)
                            progress = repo.progress(w.id)
                            delay(300)
                            next()
                        }
                    },
                    modifier = Modifier.weight(1f).height(52.dp)
                ) { Text(strings.iKnow) }
            }

            Text(strings.tapToFlip, style = MaterialTheme.typography.bodySmall)
        }
    }
}
