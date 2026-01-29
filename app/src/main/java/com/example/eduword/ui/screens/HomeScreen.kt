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
import com.example.eduword.ui.strings.strings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        topBar = { TopAppBar(title = { Text(strings.appName) }) }
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
                Text(strings.changeLanguage)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = { navController.navigate(Routes.FLASHCARDS) }, modifier = Modifier.fillMaxWidth().height(52.dp)) {
                    Text(strings.flashcards)
                }
                Button(onClick = { navController.navigate(Routes.SPELLING) }, modifier = Modifier.fillMaxWidth().height(52.dp)) {
                    Text(strings.spelling)
                }
                Button(onClick = { navController.navigate(Routes.ARTICLE_QUIZ) }, modifier = Modifier.fillMaxWidth().height(52.dp)) {
                    Text(strings.articleQuiz)
                }
                Button(onClick = { navController.navigate(Routes.ADD_WORD) }, modifier = Modifier.fillMaxWidth().height(52.dp)) {
                    Text(strings.addWord)
                }
                Button(onClick = { navController.navigate(Routes.WORD_LIST) }, modifier = Modifier.fillMaxWidth().height(52.dp)) {
                    Text(strings.wordList)
                }
                Button(onClick = { navController.navigate(Routes.SCAN) }, modifier = Modifier.fillMaxWidth().height(52.dp)) {
                    Text("Scan")
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = strings.version,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            )
        }
    }
}
