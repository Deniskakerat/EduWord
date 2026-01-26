package com.example.eduword.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.eduword.ui.nav.Routes
import com.example.eduword.ui.settings.AppSettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("EduWord") }) }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.clickable { AppSettings.currentLanguage = if (AppSettings.currentLanguage == "UK") "EN" else "UK" },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (AppSettings.currentLanguage == "UK") "🇺🇦" else "🇬🇧",
                    fontSize = 48.sp,
                    modifier = Modifier.padding(8.dp)
                )
                Text("Change Language")
            }

            Spacer(modifier = Modifier.height(32.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = { navController.navigate(Routes.FLASHCARDS) }, modifier = Modifier.fillMaxWidth().height(52.dp)) {
                    Text("Flashcards")
                }
                Button(onClick = { navController.navigate(Routes.SPELLING) }, modifier = Modifier.fillMaxWidth().height(52.dp)) {
                    Text("Spelling")
                }
                Button(onClick = { navController.navigate(Routes.ARTICLE_QUIZ) }, modifier = Modifier.fillMaxWidth().height(52.dp)) {
                    Text("Article Quiz")
                }
                Button(onClick = { navController.navigate(Routes.ADD_WORD) }, modifier = Modifier.fillMaxWidth().height(52.dp)) {
                    Text("Add Word")
                }
                Button(onClick = { navController.navigate(Routes.WORD_LIST) }, modifier = Modifier.fillMaxWidth().height(52.dp)) {
                    Text("Word List")
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Version 1.0",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            )
        }
    }
}
