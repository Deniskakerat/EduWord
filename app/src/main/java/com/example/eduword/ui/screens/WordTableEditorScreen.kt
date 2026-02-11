package com.example.eduword.ui.screens

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.eduword.data.entity.RowError
import com.example.eduword.data.entity.WordEntity
import com.example.eduword.data.entity.WordRowUi
import com.example.eduword.data.repository.WordRepository
import kotlinx.coroutines.launch
import com.example.eduword.data.wiktionary.TranslationEnricher
import java.util.UUID


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun WordTableEditorScreen(
    repo: WordRepository,
    words: List<String>,
    onDone: () -> Unit
) {

    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(true) }
    var rows by remember { mutableStateOf<List<WordRowUi>>(emptyList()) }

    LaunchedEffect(words) {
        isLoading = true
        rows = buildRowsWithAutoFill(repo, words)
        isLoading = false
    }

    // Hoisted scroll state for synchronized scrolling
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Words table") }) }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            if (isLoading) {
                LinearProgressIndicator(Modifier.fillMaxWidth())
                Spacer(Modifier.height(12.dp))
                Text(
                    "Generating articles, plurals and translations…",
                    modifier = Modifier.padding(12.dp)
                )
            }

            TableToolbar(
                rows = rows,
                onRowsChange = { rows = it },
                onEnrichSelected = {
                    scope.launch {
                        isLoading = true
                        val updated = rows.toMutableList()
                        for (i in updated.indices) {
                            val r = updated[i]
                            if (!r.selected) continue
                            if (r.article.isNullOrBlank() || r.plural.isNullOrBlank()) {
                                val wiki = WiktionaryEnricher.fetchGermanInfo(r.lemma)
                                updated[i] = r.copy(
                                    article = r.article ?: wiki?.article,
                                    plural = r.plural ?: wiki?.plural
                                )
                            }
                        }
                        rows = updated
                        isLoading = false
                    }
                },
                onTranslateSelected = {
                    scope.launch {
                        isLoading = true
                        val updated = rows.toMutableList()

                        for (i in updated.indices) {
                            val r = updated[i]
                            if (!r.selected) continue

                            // якщо поле порожнє — заповнюємо
                            val needUk = r.uk.isBlank()
                            val needEn = r.en.isBlank()

                            if (needUk || needEn) {
                                val uk = if (needUk) TranslationEnricher.translateDeToUk(r.lemma) else r.uk
                                val en = if (needEn) TranslationEnricher.translateDeToEn(r.lemma) else r.en

                                updated[i] = r.copy(
                                    uk = uk ?: r.uk,
                                    en = en ?: r.en
                                )
                            }
                        }

                        rows = updated
                        isLoading = false
                    }
                }

            )
            TableHeader(scrollState = scrollState)

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(rows.size) { idx ->
                    WordTableRow(
                        row = rows[idx],
                        scrollState = scrollState,
                        onChange = { updated ->
                            rows = rows.toMutableList().also { it[idx] = updated }
                        }
                    )
                }
            }

            Button(
                onClick = {
                    scope.launch {
                        isLoading = true
                        try {
                            val validated = validateRows(rows)
                            rows = validated

                            val toInsert = validated
                                .filter { it.selected }
                                .filter { it.errors.isEmpty() }
                                .map { it.toWordEntity() }

                            if (toInsert.isEmpty()) return@launch

                            val resultIds = repo.insertAllIgnore(toInsert)

                            val inserted = resultIds.count { it != -1L }
                            val ignored = resultIds.count { it == -1L }

                            Log.d("DB", "Inserted=$inserted Ignored=$ignored")

                            onDone()
                        } catch (t: Throwable) {
                            Log.e("DB", "Insert error", t)
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .padding(12.dp),
                enabled = !isLoading
            ) { Text("ADD to database") }
        }
    }
}
private fun WordRowUi.toWordEntity(): WordEntity {
    val safeTopic = (topic ?: "").trim().ifBlank { "General" }
    val safeLevel = (level ?: "").trim().ifBlank { "A1" }

    // якщо переклад null/blank — ставимо "" (бо у тебе non-null поля)
    val safeUk = uk.trim()
    val safeEn = en.trim()

    return WordEntity(
        language = "DE",
        topic = safeTopic,
        level = safeLevel,
        article = article?.trim()?.ifBlank { null },
        lemma = lemma.trim(),
        plural = plural?.trim()?.ifBlank { null },
        ukTranslation = safeUk,
        engTranslation = safeEn
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TableToolbar(
    rows: List<WordRowUi>,
    onRowsChange: (List<WordRowUi>) -> Unit,
    onEnrichSelected: () -> Unit,
    onTranslateSelected: () -> Unit
) {
    var showBulkEdit by remember { mutableStateOf(false) }
    var showSetTopicLevel by remember { mutableStateOf(false) }
    val selectedCount = rows.count { it.selected }

    fun deleteSelected() = onRowsChange(rows.filterNot { it.selected })
    fun selectAll(value: Boolean) = onRowsChange(rows.map { it.copy(selected = value) })

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 1) Перший ряд кнопок (буде переноситись без “дір”)
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(onClick = { selectAll(true) }) { Text("All") }
            OutlinedButton(onClick = { selectAll(false) }) { Text("None") }
            OutlinedButton(onClick = { deleteSelected() }, enabled = selectedCount > 0) { Text("Delete") }
            OutlinedButton(onClick = { showBulkEdit = true }, enabled = selectedCount > 0) { Text("Bulk edit") }
            OutlinedButton(onClick = { showSetTopicLevel = true }, enabled = selectedCount > 0) { Text("Set topic/level") }
        }

        // 2) Другий ряд кнопок
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(onClick = onEnrichSelected, enabled = selectedCount > 0) { Text("Enrich (Wiki)") }
            OutlinedButton(onClick = onTranslateSelected, enabled = selectedCount > 0) { Text("Translate") }
        }
    }

    if (showBulkEdit) {
        BulkEditDialog(
            onDismiss = { showBulkEdit = false },
            onApply = { field, value ->
                onRowsChange(rows.map { if (!it.selected) it else applyField(it, field, value) })
                showBulkEdit = false
            }
        )
    }

    if (showSetTopicLevel) {
        SetTopicLevelDialog(
            onDismiss = { showSetTopicLevel = false },
            onApply = { topic, level ->
                onRowsChange(rows.map { if (!it.selected) it else it.copy(topic = topic, level = level) })
                showSetTopicLevel = false
            }
        )
    }
}

@Composable
fun TableHeader(scrollState: ScrollState) {
    Row(
        Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Spacer(Modifier.width(44.dp)) // For checkbox
        Text("Art", Modifier.width(90.dp), fontWeight = FontWeight.Bold)
        Text("Word", Modifier.width(150.dp), fontWeight = FontWeight.Bold)
        Text("Plural", Modifier.width(160.dp), fontWeight = FontWeight.Bold)
        Text("UA", Modifier.width(180.dp), fontWeight = FontWeight.Bold)
        Text("EN", Modifier.width(180.dp), fontWeight = FontWeight.Bold)
        Text("Topic", Modifier.width(160.dp), fontWeight = FontWeight.Bold)
        Text("Level", Modifier.width(90.dp), fontWeight = FontWeight.Bold)
    }
}

@Composable
fun WordTableRow(
    row: WordRowUi,
    scrollState: ScrollState, // Accept hoisted state
    onChange: (WordRowUi) -> Unit
) {
    val hasError = row.errors.isNotEmpty()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (hasError) MaterialTheme.colorScheme.errorContainer
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(Modifier.padding(8.dp)) {
            Row(
                Modifier
                    .horizontalScroll(scrollState) // Use shared state
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Checkbox(
                    checked = row.selected,
                    onCheckedChange = { onChange(row.copy(selected = it)) },
                    modifier = Modifier.width(44.dp)
                )
                OutlinedTextField(value = row.article ?: "", onValueChange = { v -> onChange(row.copy(article = v.trim().lowercase().ifBlank { null })) }, label = { Text("Art") }, singleLine = true, modifier = Modifier.width(90.dp))
                OutlinedTextField(value = row.lemma, onValueChange = { onChange(row.copy(lemma = it)) }, label = { Text("Word") }, singleLine = true, modifier = Modifier.width(150.dp))
                OutlinedTextField(value = row.plural ?: "", onValueChange = { onChange(row.copy(plural = it.ifBlank { null })) }, label = { Text("Plural") }, singleLine = true, modifier = Modifier.width(160.dp))
                OutlinedTextField(value = row.uk, onValueChange = { onChange(row.copy(uk = it)) }, label = { Text("UA") }, singleLine = true, modifier = Modifier.width(180.dp))
                OutlinedTextField(value = row.en, onValueChange = { onChange(row.copy(en = it)) }, label = { Text("EN") }, singleLine = true, modifier = Modifier.width(180.dp))
                OutlinedTextField(value = row.topic ?: "", onValueChange = { onChange(row.copy(topic = it)) }, label = { Text("Topic") }, singleLine = true, modifier = Modifier.width(160.dp))
                OutlinedTextField(value = row.level ?: "A1", onValueChange = { onChange(row.copy(level = it)) }, label = { Text("Level") }, singleLine = true, modifier = Modifier.width(90.dp))
            }
            if (hasError) {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = buildString {
                        if (row.errors.contains(RowError.MissingTopic)) append("Topic is required. ")
                        if (row.errors.contains(RowError.MissingLevel)) append("Level is required. ")
                        if (row.errors.contains(RowError.MissingLemma)) append("Word is empty. ")
                        if (row.errors.contains(RowError.MissingTranslation)) append("Add UA or EN translation. ")
                    },
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

enum class BulkField { ARTICLE, TOPIC, LEVEL, UK, EN, PLURAL }

@Composable
fun BulkEditDialog(
    onDismiss: () -> Unit,
    onApply: (BulkField, String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var field by remember { mutableStateOf(BulkField.ARTICLE) }
    var value by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = { onApply(field, value) }, enabled = value.isNotBlank()) { Text("Apply") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
        title = { Text("Bulk edit") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(onClick = { expanded = true }) { Text("Field: $field") }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    BulkField.values().forEach { f ->
                        DropdownMenuItem(text = { Text(f.name) }, onClick = { field = f; expanded = false })
                    }
                }
                OutlinedTextField(value = value, onValueChange = { value = it }, label = { Text("New value") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            }
        }
    )
}

fun applyField(row: WordRowUi, field: BulkField, value: String): WordRowUi {
    return when (field) {
        BulkField.ARTICLE -> row.copy(article = value.trim().lowercase().ifBlank { null })
        BulkField.TOPIC -> row.copy(topic = value.trim())
        BulkField.LEVEL -> row.copy(level = value.trim())
        BulkField.UK -> row.copy(uk = value.trim())
        BulkField.EN -> row.copy(en = value.trim())
        BulkField.PLURAL -> row.copy(plural = value.trim().ifBlank { null })
    }
}

@Composable
fun SetTopicLevelDialog(
    onDismiss: () -> Unit,
    onApply: (String, String) -> Unit
) {
    var topic by remember { mutableStateOf("") }
    var level by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = { onApply(topic.trim(), level.trim()) }, enabled = topic.isNotBlank() && level.isNotBlank()) { Text("Apply") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
        title = { Text("Set topic & level") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(topic, { topic = it }, label = { Text("Topic") }, singleLine = true)
                OutlinedTextField(level, { level = it }, label = { Text("Level (A1..C2)") }, singleLine = true)
            }
        }
    )
}

suspend fun buildRowsWithAutoFill(repo: WordRepository, words: List<String>): List<WordRowUi> {
    val cleaned = words
        .map { it.trim() }
        .filter { it.length >= 2 }
        .distinct()

    return cleaned.map { w ->
        val lemma = w.replaceFirstChar(Char::titlecase)
        val fromDb = repo.findGermanByLemma(lemma)

        WordRowUi(
            id = UUID.randomUUID().toString(),
            original = w,
            selected = true,
            article = fromDb?.article,
            lemma = fromDb?.lemma ?: lemma,
            plural = fromDb?.plural,
            uk = fromDb?.ukTranslation ?: "",
            en = fromDb?.engTranslation ?: "",
            topic = fromDb?.topic ?: "",
            level = fromDb?.level ?: "A1"
        )
    }
}

fun validateRows(list: List<WordRowUi>): List<WordRowUi> {
    return list.map { r ->
        val errs = buildSet {
            if (r.lemma.trim().isEmpty()) add(RowError.MissingLemma)
            if ((r.topic ?: "").trim().isEmpty()) add(RowError.MissingTopic)
            if ((r.level ?: "").trim().isEmpty()) add(RowError.MissingLevel)
            if (r.uk.trim().isEmpty() && r.en.trim().isEmpty()) add(RowError.MissingTranslation)
        }
        r.copy(errors = errs)
    }
}
