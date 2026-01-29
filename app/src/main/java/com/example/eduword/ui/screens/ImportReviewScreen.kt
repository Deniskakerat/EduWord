package com.example.eduword.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.eduword.data.entity.WordEntity
import com.example.eduword.data.ocr.DetectedWordRow
import com.example.eduword.data.repository.WordRepository
import com.example.eduword.ui.screens.WordListScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportReviewScreen(
    repo: WordRepository,
    initial: List<DetectedWordRow>,
    onDone: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var rows by remember { mutableStateOf(initial.map { it.copy() }) }

    Scaffold(topBar = { TopAppBar(title = { Text("Import review") }) }) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {

            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                itemsIndexed(rows) { idx, r ->
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Checkbox(
                                    checked = r.isIncluded,
                                    onCheckedChange = { isChecked ->
                                        rows = rows.toMutableList().also { it[idx] = it[idx].copy(isIncluded = isChecked) }
                                    }
                                )
                                Text(r.raw)
                            }

                            OutlinedTextField(
                                value = r.article ?: "",
                                onValueChange = { v ->
                                    val a = v.trim().lowercase().ifBlank { null }
                                    rows = rows.toMutableList().also { it[idx] = it[idx].copy(article = a) }
                                },
                                label = { Text("Article (der/die/das або пусто)") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = r.lemma,
                                onValueChange = { v ->
                                    rows = rows.toMutableList().also { it[idx] = it[idx].copy(lemma = v) }
                                },
                                label = { Text("Lemma") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = r.topic,
                                onValueChange = { v ->
                                    rows = rows.toMutableList().also { it[idx] = it[idx].copy(topic = v) }
                                },
                                label = { Text("Topic") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = r.level,
                                onValueChange = { v ->
                                    rows = rows.toMutableList().also { it[idx] = it[idx].copy(level = v) }
                                },
                                label = { Text("Level") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = r.uk,
                                onValueChange = { v ->
                                    rows = rows.toMutableList().also { it[idx] = it[idx].copy(uk = v) }
                                },
                                label = { Text("UA translation") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = r.en,
                                onValueChange = { v ->
                                    rows = rows.toMutableList().also { it[idx] = it[idx].copy(en = v) }
                                },
                                label = { Text("EN translation") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            Button(
                onClick = {
                    scope.launch {
                        val entities = rows
                            .filter { it.isIncluded }
                            .map {
                                WordEntity(
                                    language = "DE",
                                    topic = it.topic.ifBlank { "General" },
                                    level = it.level.ifBlank { "A1" },
                                    article = it.article,
                                    lemma = it.lemma.trim(),
                                    plural = null,
                                    ukTranslation = it.uk.trim(),
                                    engTranslation = it.en.trim()
                                )
                            }
                        repo.insertAllIgnore(entities) // треба додати цей метод в repo/dao
                        onDone()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp).padding(16.dp)
            ) {
                Text("Import")
            }
        }
    }
}
