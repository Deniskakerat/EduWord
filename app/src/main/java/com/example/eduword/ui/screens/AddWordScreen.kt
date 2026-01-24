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
fun AddWordScreen(repo: WordRepository) {
    val scope = rememberCoroutineScope()

    var article by remember { mutableStateOf("der") }
    var lemma by remember { mutableStateOf("") }
    var plural by remember { mutableStateOf("") }
    var uk by remember { mutableStateOf("") }
    var topic by remember { mutableStateOf("General") }
    var level by remember { mutableStateOf("A1") }
    var msg by remember { mutableStateOf<String?>(null) }

    Scaffold(topBar = { TopAppBar(title = { Text("Додати слово") }) }) { padding ->
        Column(
            Modifier.padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(value = lemma, onValueChange = { lemma = it }, label = { Text("Німецьке слово (lemma)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = uk, onValueChange = { uk = it }, label = { Text("Переклад українською") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = plural, onValueChange = { plural = it }, label = { Text("Множина (optional)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = topic, onValueChange = { topic = it }, label = { Text("Тема") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = level, onValueChange = { level = it }, label = { Text("Рівень") }, modifier = Modifier.fillMaxWidth())

            // простий вибір артикля (без experimental chips)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("der","die","das").forEach { a ->
                    Button(
                        onClick = { article = a },
                        colors = if (article == a) ButtonDefaults.buttonColors() else ButtonDefaults.outlinedButtonColors()
                    ) { Text(a) }
                }
            }

            Button(
                onClick = {
                    msg = null
                    val l = lemma.trim()
                    val t = uk.trim()
                    if (l.isBlank() || t.isBlank()) {
                        msg = "Заповни lemma і переклад."
                        return@Button
                    }
                    scope.launch {
                        repo.insertWord(
                            WordEntity(
                                language = "DE",
                                topic = topic.trim().ifBlank { "General" },
                                level = level.trim().ifBlank { "A1" },
                                article = article,
                                lemma = l,
                                plural = plural.trim().ifBlank { null },
                                ukTranslation = t
                            )
                        )
                        lemma = ""; uk = ""; plural = ""
                        msg = "✅ Додано!"
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) { Text("Зберегти") }

            if (msg != null) {
                Text(msg!!, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

