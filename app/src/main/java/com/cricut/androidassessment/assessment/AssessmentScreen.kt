package com.cricut.androidassessment.assessment

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.cricut.androidassessment.common.composables.ErrorScreen
import com.cricut.androidassessment.common.composables.LoadingScreen
import com.cricut.androidassessment.common.theme.AndroidAssessmentTheme


const val ASSESSMENT_ROUTE = "assessment"


data class AssessmentScreenActions(
    val onNextQuestion: () -> Unit,
    val onPreviousQuestion: () -> Unit,
    val onFinishAssessment: () -> Unit,
)

fun NavGraphBuilder.assessmentScreen() {
    composable(ASSESSMENT_ROUTE) {
        val viewModel = hiltViewModel<AssessmentViewModel>()

        LifecycleStartEffect(Unit) {
            viewModel.onStart()

            onStopOrDispose {  }
        }

        //TODO
        val screenActions = AssessmentScreenActions(
            onNextQuestion = { },
            onPreviousQuestion = { },
            onFinishAssessment = { }
        )

        val model by viewModel.observableModel.collectAsStateWithLifecycle()



        AssessmentScreenScaffold(
            modifier = Modifier.fillMaxSize(),
            model = model,
            actions = screenActions
        )
    }
}

@Composable
private fun AssessmentScreenScaffold(
    modifier: Modifier = Modifier,
    model: AssessmentScreenModel,
    actions: AssessmentScreenActions,
) {
    Scaffold(modifier = modifier) { padding ->
        when {
            model.isLoading -> LoadingScreen(modifier = Modifier
                .fillMaxSize()
                .padding(padding))
            model.isError -> ErrorScreen(modifier = Modifier
                .fillMaxSize()
                .padding(padding))
            else -> AssessmentScreenContent(modifier = Modifier
                .fillMaxSize()
                .padding(padding))
        }
    }
}

@Composable
private fun AssessmentScreenContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Assessment Screen", textAlign = TextAlign.Center)
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewAssessmentScreen() {
    AndroidAssessmentTheme {
        AssessmentScreenScaffold(
            modifier = Modifier.fillMaxSize(),
            model = AssessmentScreenModel(isLoading = false, isError = false),
            actions = AssessmentScreenActions(
                onNextQuestion = { },
                onPreviousQuestion = { },
                onFinishAssessment = { },
            )
        )
    }
}
