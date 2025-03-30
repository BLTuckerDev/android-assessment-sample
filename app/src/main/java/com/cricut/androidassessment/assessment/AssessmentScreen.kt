package com.cricut.androidassessment.assessment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.cricut.androidassessment.assessment.composables.MultipleChoiceQuestionItem
import com.cricut.androidassessment.assessment.composables.TrueFalseQuestionItem
import com.cricut.androidassessment.common.composables.ErrorScreen
import com.cricut.androidassessment.common.composables.LoadingScreen
import com.cricut.androidassessment.common.theme.AndroidAssessmentTheme


const val ASSESSMENT_ROUTE = "assessment"


data class AssessmentScreenActions(
    val onNextQuestion: () -> Unit,
    val onPreviousQuestion: () -> Unit,
    val onTrueFalseAnswerSelected: (Boolean) -> Unit,
    val onMultipleChoiceAnswerSelected: (Int) -> Unit,
    val onFinishAssessment: () -> Unit,
)

fun NavGraphBuilder.assessmentScreen() {
    composable(ASSESSMENT_ROUTE) {
        val viewModel = hiltViewModel<AssessmentViewModel>()

        LifecycleStartEffect(Unit) {
            viewModel.onStart()

            onStopOrDispose { }
        }

        val screenActions = AssessmentScreenActions(
            onNextQuestion = viewModel::onNextQuestion,
            onPreviousQuestion = viewModel::onPreviousQuestion,
            onTrueFalseAnswerSelected = viewModel::onTrueFalseAnswerSelected,
            onMultipleChoiceAnswerSelected = viewModel::onMultipleChoiceAnswerSelected,
            onFinishAssessment = {
                //TODO
            }
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
            model.isLoading -> LoadingScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            )

            model.isError -> ErrorScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            )

            else -> AssessmentScreenContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                model = model,
                actions = actions,
            )
        }
    }
}

@Composable
private fun AssessmentScreenContent(
    modifier: Modifier = Modifier,
    model: AssessmentScreenModel,
    actions: AssessmentScreenActions
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LinearProgressIndicator(
            progress = { (model.currentQuestionIndex + 1).toFloat() / model.questions.size },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            model.currentQuestion?.let { question ->
                when (question) {
                    is Question.TrueFalse -> {
                        val selectedAnswer = model.getCurrentAnswer() as? Boolean

                        TrueFalseQuestionItem(
                            questionText = question.text,
                            selectedAnswer = selectedAnswer,
                            onAnswerSelected = actions.onTrueFalseAnswerSelected
                        )
                    }

                    is Question.MultipleChoice -> {
                        val selectedOptionIndex = model.getCurrentAnswer() as? Int

                        MultipleChoiceQuestionItem(
                            questionText = question.text,
                            options = question.options,
                            selectedOptionIndex = selectedOptionIndex,
                            onOptionSelected = actions.onMultipleChoiceAnswerSelected
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = actions.onPreviousQuestion,
                enabled = model.canGoPrevious,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text("Previous")
            }

            Button(
                onClick = if (model.currentQuestionIndex == model.questions.size - 1)
                    actions.onFinishAssessment
                else
                    actions.onNextQuestion,
                enabled = model.canGoNext,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                if (model.currentQuestionIndex == model.questions.size - 1) {
                    Text("Finish")
                } else {
                    Text("Next Question")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewAssessmentScreen() {
    AndroidAssessmentTheme {
        AssessmentScreenScaffold(
            modifier = Modifier.fillMaxSize(),
            model = AssessmentScreenModel(isLoading = false, isError = false, questions = AssessmentQuestionsRepository.Companion.ASSESSMENT_QUESTIONS),
            actions = AssessmentScreenActions(
                onNextQuestion = { },
                onPreviousQuestion = { },
                onFinishAssessment = { },
                onTrueFalseAnswerSelected = { },
                onMultipleChoiceAnswerSelected = { },
            )
        )
    }
}
