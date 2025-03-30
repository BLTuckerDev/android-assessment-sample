package com.cricut.androidassessment.assessment.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun TrueFalseQuestionItem(
    modifier: Modifier = Modifier,
    questionText: String,
    selectedAnswer: Boolean?,
    onAnswerSelected: (Boolean) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = questionText,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = { onAnswerSelected(true) },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedAnswer == true)
                        MaterialTheme.colorScheme.primary
                    else
                        Color.LightGray
                )
            ) {
                Text(
                    text = "True",
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (selectedAnswer == true)
                        Color.White
                    else
                        Color.DarkGray
                )
            }

            Button(
                onClick = { onAnswerSelected(false) },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedAnswer == false)
                        MaterialTheme.colorScheme.primary
                    else
                        Color.LightGray
                )
            ) {
                Text(
                    text = "False",
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (selectedAnswer == false)
                        Color.White
                    else
                        Color.DarkGray
                )
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun TrueFalseQuestionItemPreview() {
    MaterialTheme {
        TrueFalseQuestionItem(
            questionText = "Kotlin is the official language for Android development.",
            selectedAnswer = true,
            onAnswerSelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TrueFalseQuestionItemPreview_NoSelection() {
    MaterialTheme {
        TrueFalseQuestionItem(
            questionText = "An Activity is destroyed during recomposition?",
            selectedAnswer = null,
            onAnswerSelected = {}
        )
    }
}
