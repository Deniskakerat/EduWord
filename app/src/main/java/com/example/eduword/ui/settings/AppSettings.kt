package com.example.eduword.ui.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object AppSettings {
    var currentLanguage by mutableStateOf("UK") // or "EN"
}
