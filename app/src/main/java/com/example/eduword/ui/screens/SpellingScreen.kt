package com.example.eduword.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.eduword.data.entity.WordEntity
import com.example.eduword.data.repository.WordRepository
import com.example.eduword.ui.settings.AppSettings
import com.example.eduword.ui.strings.strings
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpellingScreen(repo: WordRepository) {
    val scope = rememberCoroutineScope()
    var selectedTopic by remember { mutableStateOf<String?>(null) }
    var current by remember { mutableStateOf<WordEntity?>(null) }
    var article by remember { mutableStateOf("der") }
    var input by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<String?>(null) }
    var topics by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(Unit) {
        topics = repo.topics()
    }

    fun next() {
        result = null
        input = ""
        article = "der"
        scope.launch { current = repo.randomWord(selectedTopic) }
    }

    fun check(w: WordEntity) {
        val correctLemma = w.lemma.trim()
        val correctArticle = (w.article ?: "").trim()
        val correctArticleForDisplay = correctArticle.ifBlank { strings.keine }

        val lemmaOk = input.trim().equals(correctLemma, ignoreCase = true)
        val articleOk = if (correctArticle.isBlank()) article == strings.keine else article == correctArticle

        result = when {
            lemmaOk && articleOk -> strings.correct
            !articleOk && lemmaOk -> strings.correctWordWrongArticle(correctArticleForDisplay)
            articleOk && !lemmaOk -> strings.correctArticleWrongWord(w.lemma)
            else -> strings.incorrect(correctArticleForDisplay, w.lemma)
        }
    }

    Scaffold(topBar = { TopAppBar(title = { Text(strings.spelling) }) }) { padding ->
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
                Text(strings.noWordsInDb)
                return@Column
            }

            Text("${strings.translation}:", style = MaterialTheme.typography.labelMedium)
            val translation = if (AppSettings.currentLanguage == "EN") w.engTranslation else w.ukTranslation
            Text(translation, style = MaterialTheme.typography.headlineSmall)

            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("der", "die", "das").forEach { a ->
                        FilterChip(
                            modifier = Modifier.weight(1f),
                            selected = article == a,
                            onClick = { article = a },
                            label = {
                                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                    Text(a.replaceFirstChar(Char::titlecase))
                                }
                            }
                        )
                    }
                }
                FilterChip(
                    selected = article == strings.keine,
                    onClick = { article = strings.keine },
                    label = {
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text(strings.keine)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                label = { Text(strings.enterWordInGerman) },
                modifier = Modifier.fillMaxWidth()
            )

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = { check(w) },
                    modifier = Modifier.weight(1f).height(52.dp)
                ) { Text(strings.check) }

                Button(
                    onClick = { next() },
                    modifier = Modifier.weight(1f).height(52.dp)
                ) { Text(strings.next) }
            }

            if (result != null) {
                Card(Modifier.fillMaxWidth()) {
                    Text(result!!, Modifier.padding(16.dp))
                }
            }

            if (!w.plural.isNullOrBlank()) {
                key(w.id) { // Reset spoiler state for each new word
                    Spoiler(text = strings.hint(w.plural!!))
                }
            }
        }
    }
}

@Composable
private fun Spoiler(modifier: Modifier = Modifier, text: String) {
    var revealed by remember { mutableStateOf(false) }

    val contentColor = if (revealed) {
        MaterialTheme.colorScheme.onSurface
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .clickable { revealed = !revealed }
            .padding(8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (revealed) Icons.Default.VisibilityOff else Icons.Default.Visibility,
            contentDescription = "Toggle hint",
            tint = contentColor
        )
        Spacer(modifier = Modifier.width(8.dp))

        Crossfade(targetState = revealed, label = "SpoilerCrossfade") { isRevealed ->
            if (isRevealed) {
                Text(text = text, color = contentColor)
            } else {
                Text(text = strings.showHint, color = contentColor)
            }
        }
    }
}
