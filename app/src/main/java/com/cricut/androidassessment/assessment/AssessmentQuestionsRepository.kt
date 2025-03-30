package com.cricut.androidassessment.assessment

import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AssessmentQuestionsRepository @Inject constructor() {

    suspend fun loadAssessmentQuestions(){
        delay(3_000)
    }
}