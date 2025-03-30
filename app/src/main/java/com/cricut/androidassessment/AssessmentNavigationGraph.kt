package com.cricut.androidassessment

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.cricut.androidassessment.assessment.ASSESSMENT_ROUTE
import com.cricut.androidassessment.assessment.assessmentScreen


@Composable
fun AssessmentNavigationGraph(navigationController: NavHostController,
                              startDestination: String = ASSESSMENT_ROUTE){
    NavHost(navController = navigationController,
        startDestination = startDestination){


        assessmentScreen(onStartNewAssessment = {
            navigationController.navigate(ASSESSMENT_ROUTE){
                popUpTo(ASSESSMENT_ROUTE){
                    inclusive = true
                }
            }
        })
    }
}