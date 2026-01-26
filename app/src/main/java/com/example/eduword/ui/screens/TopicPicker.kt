package com.example.eduword.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicPicker(topics: List<String>, selected: String?, onSelect: (String?) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                value = selected ?: "All",
                onValueChange = {},
                readOnly = true,
                label = { Text("Topic") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )

            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                DropdownMenuItem(
                    text = { Text("All") },
                    onClick = {
                        onSelect(null)
                        expanded = false
                    }
                )
                topics.forEach { topic ->
                    DropdownMenuItem(
                        text = { Text(topic) },
                        onClick = {
                            onSelect(topic)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
