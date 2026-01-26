package com.example.eduword.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun KnowledgeIndicator(
    streak: Int,
    max: Int = 5
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        repeat(max) { index ->
            AssistChip(
                onClick = {},
                label = { Text(if (index < streak) "✓" else "•") },
                enabled = false
            )
        }

        Spacer(Modifier.weight(1f))
        Text(
            text = "$streak/$max",
            style = MaterialTheme.typography.bodySmall
        )
    }
}
