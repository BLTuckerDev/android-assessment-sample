package com.cricut.androidassessment.assessment

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.cricut.androidassessment.assessment.composables.ConfettiAnimation
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
    val onStartNewAssessment: () -> Unit,
)

fun NavGraphBuilder.assessmentScreen(onStartNewAssessment: () -> Unit) {
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
            onFinishAssessment = viewModel::onFinishAssessment,
            onStartNewAssessment = onStartNewAssessment,
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

            model.isCompleted -> ScoreScreenContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                model = model,
                actions = actions,
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
        val animatedProgress = animateFloatAsState(
            targetValue = (model.currentQuestionIndex + 1).toFloat() / model.questions.size,
            animationSpec = tween(durationMillis = 500),
            label = "progress_animation"
        )

        LinearProgressIndicator(
            progress = { animatedProgress.value },
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

            if (model.currentQuestionIndex == model.questions.size - 1) {
                Button(
                    onClick = actions.onFinishAssessment,
                    enabled = model.canFinish,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Finish")
                }
            } else {
                Button(
                    onClick = actions.onNextQuestion,
                    enabled = model.canGoNext,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Next Question")
                }
            }
        }
    }
}

@Composable
private fun ScoreScreenContent(
    modifier: Modifier = Modifier,
    model: AssessmentScreenModel,
    actions: AssessmentScreenActions,
) {
    Box(modifier = modifier.fillMaxSize()) {
        ConfettiAnimation(modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Assessment Completed!",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "Your Score: ${model.correctlyAnsweredCount} out of ${model.questions.size}",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            Button(
                onClick = actions.onStartNewAssessment,
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "Start New Assessment",
                    style = MaterialTheme.typography.bodyLarge
                )
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
                onStartNewAssessment = { }
            )
        )
    }
}
