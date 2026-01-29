package com.example.eduword.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.eduword.data.entity.WordEntity
import com.example.eduword.data.repository.WordRepository
import com.example.eduword.ui.settings.AppSettings
import com.example.eduword.ui.strings.strings
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordListScreen(repo: WordRepository) {
    var words by remember { mutableStateOf<List<WordEntity>>(emptyList()) }
    var topics by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedTopic by remember { mutableStateOf<String?>(null) }
    var sortBy by remember { mutableStateOf(strings.topic) }

    LaunchedEffect(Unit) {
        topics = repo.topics()
        repo.observeAll().collectLatest { words = it }
    }

    val filteredWords = if (selectedTopic == null) {
        words
    } else {
        words.filter { it.topic == selectedTopic }
    }

    val sortedWords = if (sortBy == strings.fromAtoZ) {
        filteredWords.sortedBy { it.lemma }
    } else {
        filteredWords.sortedWith(compareBy({ it.topic }, { it.lemma }))
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(strings.wordList) }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TopicPicker(
                    topics = topics,
                    selected = selectedTopic,
                    onSelect = { selectedTopic = it })
                SortByPicker(
                    selected = sortBy,
                    onSelect = { sortBy = it })
            }

            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                Text(strings.article, modifier = Modifier.weight(0.5f), fontWeight = FontWeight.Bold)
                Text(strings.german, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                Text(strings.plural(""), modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                Text(strings.translation, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(sortedWords) { word ->
                    WordCard(word = word)
                }
            }
        }
    }
}

@Composable
private fun WordCard(word: WordEntity) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(word.article ?: "", modifier = Modifier.weight(0.5f))
            Text(word.lemma, modifier = Modifier.weight(1f))
            Text(word.plural ?: "", modifier = Modifier.weight(1f))
            val translation = if (AppSettings.currentLanguage == "EN") word.engTranslation else word.ukTranslation
            Text(translation, modifier = Modifier.weight(1f))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SortByPicker(selected: String, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(strings.sortBy) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor()
        )

        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text(strings.topic) },
                onClick = {
                    onSelect(strings.topic)
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text(strings.fromAtoZ) },
                onClick = {
                    onSelect(strings.fromAtoZ)
                    expanded = false
                }
            )
        }
    }
}
