package com.example.ai

import android.widget.Toast
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.type.asTextOrNull

@Composable
internal fun SummarizeRoute(
    summarizeViewModel: SummarizeViewModel = viewModel()
) {
    val summarizeUiState by summarizeViewModel.uiState.collectAsState()
    val respond = remember<(String) -> Unit>{
        {
            summarizeViewModel.summarizeChat(it)
        }
    }
    SummarizeScreen(summarizeUiState, summarizeViewModel.chat,onSummarizeClicked = respond)
}

@Composable
fun SummarizeScreen(
    uiState: SummarizeUiState = SummarizeUiState.Initial,
    chat : Chat,
    onSummarizeClicked: (String) -> Unit = {}
) {
    val imeState = rememberImeState()
    val scrollState = rememberScrollState()

    LaunchedEffect(key1 = imeState.value) {
        if (imeState.value) {
            scrollState.scrollTo(scrollState.maxValue)
        }
    }
    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier
            ) {
                var prompt by remember { mutableStateOf("") }
                OutlinedTextField(
                    value = prompt,
                    placeholder = { Text(stringResource(R.string.summarize_hint)) },
                    onValueChange = { prompt = it },
                    modifier = Modifier
                        .weight(8f),
                    maxLines = 3 ,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (prompt.isNotBlank()) {
                                onSummarizeClicked(prompt)
                            }
                        }
                    ),
                )
                IconButton(
                    onClick = {
                        if (prompt.isNotBlank()) {
                            onSummarizeClicked(prompt)
                        }
                    },

                    modifier = Modifier
                        .weight(2f)
                        .padding(all = 4.dp)
                        .align(Alignment.CenterVertically)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Send,
                        contentDescription ="" ,
                        tint = MaterialTheme.colorScheme.primary ,
                        modifier = Modifier
                            .size(80.dp)
                    )
                }
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(it)
                .padding(top = 5.dp)
        ) {
            items(chat.history) {
                Card (
                    colors = CardDefaults.cardColors(
                        containerColor = if (it.role == "user") Color.White else MaterialTheme.colorScheme.primary ,
                        contentColor = if (it.role == "user") Color.Black else Color.Black
                    ) ,
                    modifier = Modifier
                        .padding(vertical = 8.dp , horizontal = 10.dp)
                ){
                    Text(
                        text = it.parts[0].asTextOrNull() ?: "",
                        modifier = Modifier.padding(all = 8.dp)
                    )
                }
            }
        }

        when(uiState){
            is SummarizeUiState.Error ->{
                Toast.makeText(LocalContext.current , uiState.errorMessage , Toast.LENGTH_LONG).show()
            }
            is SummarizeUiState.Success ->{
                Toast.makeText(LocalContext.current , "Succfully", Toast.LENGTH_LONG).show()
            }
            else ->{

            }
        }
    }
}