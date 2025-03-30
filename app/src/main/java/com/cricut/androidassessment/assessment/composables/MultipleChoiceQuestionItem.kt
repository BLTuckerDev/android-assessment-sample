package com.cricut.androidassessment.assessment.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun MultipleChoiceQuestionItem(
    questionText: String,
    options: List<String>,
    selectedOptionIndex: Int?,
    onOptionSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = questionText,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        options.forEachIndexed { index, option ->
            Button(
                onClick = { onOptionSelected(index) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedOptionIndex == index)
                        MaterialTheme.colorScheme.primary
                    else
                        Color(0xFF5C6BC0)
                )
            ) {
                Text(
                    text = option,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun MultipleChoiceQuestionItemPreview() {
    MaterialTheme {
        MultipleChoiceQuestionItem(
            questionText = "Which of the following is the preferred programming language for Android app development?",
            options = listOf("Swift", "Kotlin", "Objective-C", "Java"),
            selectedOptionIndex = 1,
            onOptionSelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MultipleChoiceQuestionItemPreview_NoSelection() {
    MaterialTheme {
        MultipleChoiceQuestionItem(
            questionText = "Which of the following is a Jetpack Compose UI toolkit?",
            options = listOf("RecyclerView", "LazyColumn", "ListView", "ScrollView"),
            selectedOptionIndex = null,
            onOptionSelected = {}
        )
    }
}