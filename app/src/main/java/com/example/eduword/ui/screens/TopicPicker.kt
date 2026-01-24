package com.example.eduword.ui.screens

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable

@Composable
fun TopicPicker(
    topics: List<String>,
    selected: String?,
    onSelect: (String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    OutlinedButton(onClick = { expanded = true }) {
        Text(selected ?: "All topics")
    }

    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
        DropdownMenuItem(
            text = { Text("All topics") },
            onClick = { onSelect(null); expanded = false }
        )
        topics.forEach { t ->
            DropdownMenuItem(
                text = { Text(t) },
                onClick = { onSelect(t); expanded = false }
            )
        }
    }
}
