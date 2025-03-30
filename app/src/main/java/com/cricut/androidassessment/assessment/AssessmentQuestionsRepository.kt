package com.cricut.androidassessment.assessment

import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton


sealed class Question(
    open val id: String,
    open val text: String
) {
    data class TrueFalse(
        override val id: String,
        override val text: String,
        val correctAnswer: Boolean
    ) : Question(id, text)

    data class MultipleChoice(
        override val id: String,
        override val text: String,
        val options: List<String>,
        val correctAnswerIndex: Int
    ) : Question(id, text)
}


@Singleton
class AssessmentQuestionsRepository @Inject constructor() {

    suspend fun loadAssessmentQuestions(): List<Question> {
        delay(3_000)
        val trueFalseQuestions = ASSESSMENT_QUESTIONS
            .filterIsInstance<Question.TrueFalse>()
            .shuffled()

        val multipleChoiceQuestions = ASSESSMENT_QUESTIONS
            .filterIsInstance<Question.MultipleChoice>()
            .shuffled()

        return listOf(
            trueFalseQuestions.first(),
            multipleChoiceQuestions.first()
        )
    }

    companion object {
        val ASSESSMENT_QUESTIONS = listOf(
            Question.TrueFalse(
                id = "tf1",
                text = "Kotlin is the official language for Android development.",
                correctAnswer = true
            ),
            Question.TrueFalse(
                id = "tf2",
                text = "An Activity is destroyed during recomposition in Jetpack Compose.",
                correctAnswer = false
            ),
            Question.TrueFalse(
                id = "tf3",
                text = "ViewModels are automatically cleared when their associated fragment or activity is destroyed.",
                correctAnswer = true
            ),
            Question.TrueFalse(
                id = "tf4",
                text = "Room database operations can be performed on the main thread by default.",
                correctAnswer = false
            ),
            Question.TrueFalse(
                id = "tf5",
                text = "Material3 is the latest design system for Android applications.",
                correctAnswer = true
            ),

            Question.MultipleChoice(
                id = "mc1",
                text = "Which of the following is the preferred programming language for Android app development?",
                options = listOf("Swift", "Kotlin", "Objective-C", "Java"),
                correctAnswerIndex = 1
            ),
            Question.MultipleChoice(
                id = "mc2",
                text = "Which architectural pattern is recommended by Google for Android apps?",
                options = listOf("MVC", "MVVM", "MVP", "VIPER"),
                correctAnswerIndex = 1
            ),
            Question.MultipleChoice(
                id = "mc3",
                text = "Which component is NOT part of Android Jetpack?",
                options = listOf("Room", "LiveData", "Volley", "Navigation"),
                correctAnswerIndex = 2
            ),
            Question.MultipleChoice(
                id = "mc4",
                text = "Which of the following is NOT a Compose UI layout composable?",
                options = listOf("Column", "Row", "GridView", "Box"),
                correctAnswerIndex = 2
            ),
            Question.MultipleChoice(
                id = "mc5",
                text = "What is the build system used in modern Android projects?",
                options = listOf("Maven", "Ant", "Make", "Gradle"),
                correctAnswerIndex = 3
            )
        )
    }
}