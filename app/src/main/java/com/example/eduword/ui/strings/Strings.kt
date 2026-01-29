package com.example.eduword.ui.strings

import androidx.compose.runtime.Composable
import com.example.eduword.ui.settings.AppSettings

interface AppStrings {
    val appName: String
    val changeLanguage: String
    val flashcards: String
    val spelling: String
    val articleQuiz: String
    val addWord: String
    val wordList: String
    val version: String
    fun knowledge(streak: Int): String
    val noWordsInDb: String
    val question: String
    val answer: String
    fun plural(plural: String): String
    val iDontKnow: String
    val iKnow: String
    val tapToFlip: String
    val tapToSeeTranslation: String
    val translation: String
    val keine: String
    val enterWordInGerman: String
    val check: String
    val next: String
    val correct: String
    fun correctWordWrongArticle(correctArticle: String): String
    fun correctArticleWrongWord(correctWord: String): String
    fun incorrect(correctArticle: String, correctWord: String): String
    fun hint(plural: String): String
    val showHint: String
    val whichArticle: String
    fun incorrectArticle(correctArticle: String): String
    val sortBy: String
    val topic: String
    val all: String
    val fromAtoZ: String
    val article: String
    val german: String
    val importReview: String

}

private object UkStrings : AppStrings {
    override val appName: String get() = "EduWord"
    override val changeLanguage: String get() = "Змінити мову"
    override val flashcards: String get() = "Картки"
    override val spelling: String get() = "Правопис"
    override val articleQuiz: String get() = "Артиклі (тест)"
    override val addWord: String get() = "Додати слово"
    override val wordList: String get() = "Список слів"
    override val version: String get() = "Версія 1.0"
    override fun knowledge(streak: Int): String = "Знання: $streak/5"
    override val noWordsInDb: String get() = "Немає слів у базі."
    override val question: String get() = "Питання"
    override val answer: String get() = "Відповідь"
    override fun plural(plural: String): String = "Множина: $plural"
    override val iDontKnow: String get() = "Не знаю"
    override val iKnow: String get() = "Знаю"
    override val tapToFlip: String get() = "Тап по картці → переворот"
    override val tapToSeeTranslation: String get() = "Тап по картці → показати переклад"
    override val translation: String get() = "Переклад"
    override val keine: String get() = "Keine"
    override val enterWordInGerman: String get() = "Введи слово німецькою"
    override val check: String get() = "Перевірити"
    override val next: String get() = "Далі"
    override val correct: String get() = "✅ Правильно!"
    override fun correctWordWrongArticle(correctArticle: String): String = "⚠️ Слово правильно, артикль ні. Правильно: $correctArticle"
    override fun correctArticleWrongWord(correctWord: String): String = "⚠️ Артикль правильно, слово ні. Правильно: $correctWord"
    override fun incorrect(correctArticle: String, correctWord: String): String = "❌ Неправильно. Правильно: $correctArticle $correctWord".trim()
    override fun hint(plural: String): String = "Підказка (Множина): $plural"
    override val showHint: String get() = "Показати підказку"
    override val whichArticle: String get() = "Який артикль?"
    override fun incorrectArticle(correctArticle: String): String = "❌ Ні, правильно: $correctArticle"
    override val sortBy: String get() = "Сортувати за"
    override val topic: String get() = "Тема"
    override val all: String get() = "Всі"
    override val fromAtoZ: String get() = "A-Z"
    override val article: String get() = "Артикль"
    override val german: String get() = "Німецька"
    override val importReview: String get() = "Імпорт відгуків"

}

private object EnStrings : AppStrings {
    override val appName: String get() = "EduWord"
    override val changeLanguage: String get() = "Change Language"
    override val flashcards: String get() = "Flashcards"
    override val spelling: String get() = "Spelling"
    override val articleQuiz: String get() = "Article Quiz"
    override val addWord: String get() = "Add Word"
    override val wordList: String get() = "Word List"
    override val version: String get() = "Version 1.0"
    override fun knowledge(streak: Int): String = "Knowledge: $streak/5"
    override val noWordsInDb: String get() = "No words in the database."
    override val question: String get() = "Question"
    override val answer: String get() = "Answer"
    override fun plural(plural: String): String = "Plural: $plural"
    override val iDontKnow: String get() = "I don't know"
    override val iKnow: String get() = "I know"
    override val tapToFlip: String get() = "Tap the card to flip"
    override val tapToSeeTranslation: String get() = "Tap the card to see the translation"
    override val translation: String get() = "Translation"
    override val keine: String get() = "Keine"
    override val enterWordInGerman: String get() = "Enter the word in German"
    override val check: String get() = "Check"
    override val next: String get() = "Next"
    override val correct: String get() = "✅ Correct!"
    override fun correctWordWrongArticle(correctArticle: String): String = "⚠️ Word is correct, article is not. Correct: $correctArticle"
    override fun correctArticleWrongWord(correctWord: String): String = "⚠️ Article is correct, word is not. Correct: $correctWord"
    override fun incorrect(correctArticle: String, correctWord: String): String = "❌ Incorrect. Correct: $correctArticle $correctWord".trim()
    override fun hint(plural: String): String = "Hint (Plural): $plural"
    override val showHint: String get() = "Show hint"
    override val whichArticle: String get() = "Which article?"
    override fun incorrectArticle(correctArticle: String): String = "❌ No, correct is: $correctArticle"
    override val sortBy: String get() = "Sort by"
    override val topic: String get() = "Topic"
    override val all: String get() = "All"
    override val fromAtoZ: String get() = "A-Z"
    override val article: String get() = "Article"
    override val german: String get() = "German"
    override val importReview: String get() = "Import review"

}

val strings: AppStrings

    get() = if (AppSettings.currentLanguage == "EN") EnStrings else UkStrings