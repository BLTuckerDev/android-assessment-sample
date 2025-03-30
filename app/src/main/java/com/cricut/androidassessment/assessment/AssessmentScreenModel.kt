package com.cricut.androidassessment.assessment

import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

data class AssessmentScreenModel(
    val questions: List<Question> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val answers: Map<String, Any> = emptyMap(),
    val isLoading: Boolean,
    val isError: Boolean,
    val isCompleted: Boolean = false,
) {
    val currentQuestion: Question?
        get() = questions.getOrNull(currentQuestionIndex)

    val canGoNext: Boolean
        get() = currentQuestionIndex < questions.size - 1 && answers.containsKey(currentQuestion?.id)

    val canGoPrevious: Boolean
        get() = currentQuestionIndex > 0

    val canFinish: Boolean
        get() = questions.isNotEmpty() && questions.all { questionId ->
            answers.containsKey(
                questionId.id
            )
        }

    fun getCurrentAnswer(): Any? = currentQuestion?.let { answers[it.id] }

    val correctlyAnsweredCount: Int
        get() {
            return questions.count { question ->
                val userAnswer = answers[question.id]
                when {
                    userAnswer == null -> false
                    question is Question.TrueFalse -> userAnswer as Boolean == question.correctAnswer
                    question is Question.MultipleChoice -> userAnswer as Int == question.correctAnswerIndex
                    else -> false
                }
            }
        }

}


@ViewModelScoped
class AssessmentScreenModelReducer @Inject constructor() {

    fun createInitialState() = AssessmentScreenModel(isLoading = true, isError = false)

    fun updateModelWithError(previousModel: AssessmentScreenModel) =
        previousModel.copy(isLoading = false, isError = true)

    fun updateModelWithQuestions(previousModel: AssessmentScreenModel, questions: List<Question>) =
        previousModel.copy(isLoading = false, isError = false, questions = questions)


    fun navigateToNextQuestion(previousModel: AssessmentScreenModel) =
        if (previousModel.canGoNext) {
            previousModel.copy(currentQuestionIndex = previousModel.currentQuestionIndex + 1)
        } else {
            previousModel
        }

    fun navigateToPreviousQuestion(previousModel: AssessmentScreenModel) =
        if (previousModel.canGoPrevious) {
            previousModel.copy(currentQuestionIndex = previousModel.currentQuestionIndex - 1)
        } else {
            previousModel
        }

    fun saveAnswer(
        previousModel: AssessmentScreenModel,
        questionId: String,
        answer: Any
    ): AssessmentScreenModel {
        val updatedAnswers = previousModel.answers.toMutableMap().apply {
            put(questionId, answer)
        }
        return previousModel.copy(answers = updatedAnswers)
    }

    fun updateModelWithCompletedAssessment(previousModel: AssessmentScreenModel): AssessmentScreenModel {
        return previousModel.copy(isCompleted = true)
    }
}