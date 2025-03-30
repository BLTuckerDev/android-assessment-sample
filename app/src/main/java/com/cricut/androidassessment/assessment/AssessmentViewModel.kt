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

        viewModelScope.launch {


            try{
                assessmentQuestionsRepository.loadAssessmentQuestions()
                //TODO implement questions and load them in

                mutableModel.update {
                    assessmentScreenModelReducer.updateModelWithQuestions(it)
                }

            }catch (e: Exception){
                mutableModel.update {
                    assessmentScreenModelReducer.updateModelWithError(it)
                }
            }

        }
    }
}
