package com.cricut.androidassessment.assessment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssessmentViewModel @Inject constructor(
    private val assessmentQuestionsRepository: AssessmentQuestionsRepository,
    private val assessmentScreenModelReducer: AssessmentScreenModelReducer,
) : ViewModel() {

    private val mutableModel = MutableStateFlow(assessmentScreenModelReducer.createInitialState())
    val observableModel: StateFlow<AssessmentScreenModel> = mutableModel

    val latestModel: AssessmentScreenModel
        get() = mutableModel.value

    private var hasStarted = false

    fun onStart(){
        if(hasStarted){
            return
        }
        hasStarted = true

        loadQuestions()
    }

    private fun loadQuestions() {
        viewModelScope.launch {
            try{
                val questions = assessmentQuestionsRepository.loadAssessmentQuestions()

                mutableModel.update {
                    assessmentScreenModelReducer.updateModelWithQuestions(it, questions)
                }

            }catch (e: Exception){
                mutableModel.update {
                    assessmentScreenModelReducer.updateModelWithError(it)
                }
            }
        }
    }

    fun onNextQuestion() {
        mutableModel.update {
            assessmentScreenModelReducer.navigateToNextQuestion(it)
        }
    }

    fun onPreviousQuestion() {
        mutableModel.update {
            assessmentScreenModelReducer.navigateToPreviousQuestion(it)
        }
    }

    fun onTrueFalseAnswerSelected(answer: Boolean) {
        latestModel.currentQuestion?.id?.let { questionId ->
            mutableModel.update {
                assessmentScreenModelReducer.saveAnswer(it, questionId, answer)
            }
        }
    }

    fun onMultipleChoiceAnswerSelected(optionIndex: Int) {
        latestModel.currentQuestion?.id?.let { questionId ->
            mutableModel.update {
                assessmentScreenModelReducer.saveAnswer(it, questionId, optionIndex)
            }
        }
    }

    fun onFinishAssessment() {
        if(!latestModel.canFinish){
            return
        }

        mutableModel.update {
            assessmentScreenModelReducer.updateModelWithCompletedAssessment(it)
        }
    }
}
