package com.cricut.androidassessment.common.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.cricut.androidassessment.common.theme.AndroidAssessmentTheme

@Composable
fun ErrorScreen(modifier: Modifier = Modifier,
                errorMesssage: String = "Something unexpected has happened!") {
    Column(modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center){

        Text(text = errorMesssage,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineMedium)
    }
}


@Preview
@Composable
private fun ErrorScreenPreview(){
    AndroidAssessmentTheme{
        Surface{
            ErrorScreen(modifier = Modifier.fillMaxSize())
        }
    }
}