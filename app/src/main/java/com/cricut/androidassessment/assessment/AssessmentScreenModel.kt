package com.cricut.androidassessment.assessment

import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

data class AssessmentScreenModel(
    val isLoading: Boolean,
    val isError: Boolean,
)


@ViewModelScoped
class AssessmentScreenModelReducer @Inject constructor(){

    fun createInitialState() = AssessmentScreenModel(isLoading = true, isError = false)

    fun updateModelWithError(previousModel: AssessmentScreenModel) = previousModel.copy(isLoading = false, isError = true)

    //TODO load questions into model
    fun updateModelWithQuestions(previousModel: AssessmentScreenModel) = previousModel.copy(isLoading = false, isError = false)


}