package com.cricut.androidassessment.assessment

import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

data class AssessmentScreenModel(
    val questions: List<Question> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val answers: Map<String, Any> = emptyMap(),
    val isLoading: Boolean,
    val isError: Boolean,
){
    val currentQuestion: Question?
        get() = questions.getOrNull(currentQuestionIndex)

    val canGoNext: Boolean
        get() = currentQuestionIndex < questions.size - 1 && answers.containsKey(currentQuestion?.id)

    val canGoPrevious: Boolean
        get() = currentQuestionIndex > 0

    fun getCurrentAnswer(): Any? = currentQuestion?.let { answers[it.id] }
}


@ViewModelScoped
class AssessmentScreenModelReducer @Inject constructor(){

    fun createInitialState() = AssessmentScreenModel(isLoading = true, isError = false)

    fun updateModelWithError(previousModel: AssessmentScreenModel) = previousModel.copy(isLoading = false, isError = true)

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

    fun saveAnswer(previousModel: AssessmentScreenModel, questionId: String, answer: Any): AssessmentScreenModel {
        val updatedAnswers = previousModel.answers.toMutableMap().apply {
            put(questionId, answer)
        }
        return previousModel.copy(answers = updatedAnswers)
    }
}