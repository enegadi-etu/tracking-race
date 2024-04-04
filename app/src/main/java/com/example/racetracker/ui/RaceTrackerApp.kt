/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.racetracker.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.racetracker.R
import com.example.racetracker.ui.theme.RaceTrackerTheme
import com.example.racetracker.ui.theme.md_theme_light_primaryContainer
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RaceTrackerApp() {
    /**
     * Note: To survive the configuration changes such as screen rotation, [rememberSaveable] should
     * be used with custom Saver object. But to keep the example simple, and keep focus on
     * Coroutines that implementation detail is stripped out.
     */
    val playerOne = remember {
        RaceParticipant(name = "Player Blue", progressIncrement = 1, color = Color.Blue)
    }
    val playerTwo = remember {
        RaceParticipant(name = "Player Red", progressIncrement = 2 , color =  Color.Red)
    }
    var raceInProgress by remember { mutableStateOf(false) }

    if (raceInProgress) {
        LaunchedEffect(playerOne, playerTwo) {
            coroutineScope {
                launch { playerOne.run() }
                launch { playerTwo.run() }
            }
            raceInProgress = false
        }
    }
    Column {
        // bar containing the title
        TopAppBar( colors = TopAppBarDefaults.topAppBarColors(
            containerColor = md_theme_light_primaryContainer,
            titleContentColor = Color.White,
        ),
            title = {
                Text("Run a race")
            }
        )

        RaceTrackerScreen(
            playerOne = playerOne,
            playerTwo = playerTwo,
            isRunning = raceInProgress,
            onRunStateChange = { raceInProgress = it },
            modifier = Modifier
                .statusBarsPadding()
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .safeDrawingPadding()
                .padding(horizontal = dimensionResource(R.dimen.padding_medium)),
        )
    }

}

@Composable
private fun RaceTrackerScreen(
    playerOne: RaceParticipant,
    playerTwo: RaceParticipant,
    isRunning: Boolean,
    onRunStateChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(dimensionResource(R.dimen.padding_medium)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_walk),
                contentDescription = null,
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium)),
                tint = Color.DarkGray
            )
            StatusIndicator(
                participantName = playerOne.name,
                currentProgress = playerOne.currentProgress,
                color = playerOne.color,
                maxProgress = stringResource(
                    R.string.progress_percentage,
                    playerOne.maxProgress
                ),
                progressFactor = playerOne.progressFactor,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.size(dimensionResource(R.dimen.padding_large)))
            StatusIndicator(
                participantName = playerTwo.name,
                color = playerTwo.color,
                currentProgress = playerTwo.currentProgress,
                maxProgress = stringResource(
                    R.string.progress_percentage,
                    playerTwo.maxProgress
                ),
                progressFactor = playerTwo.progressFactor,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.size(dimensionResource(R.dimen.padding_large)))
            RaceControls(
                isRunning = isRunning,
                onRunStateChange = onRunStateChange,
                onReset = {
                    playerOne.reset()
                    playerTwo.reset()
                    onRunStateChange(false)
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun StatusIndicator(
    participantName: String,
    currentProgress: Int,
    maxProgress: String,
    progressFactor: Float,
    color : Color,
    modifier: Modifier = Modifier
) {
    Column {
        Icon(
            painter = painterResource(R.drawable.ic_walk),
            contentDescription = null,
            modifier = Modifier
                .size(100.dp)
                .padding(dimensionResource(R.dimen.padding_medium)),
            tint = color
        )
        Row(
            modifier = modifier
        ) {
            Text(
                text = participantName,
                modifier = Modifier.padding(end = dimensionResource(R.dimen.padding_small))
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
            ) {
                LinearProgressIndicator(
                    progress = progressFactor,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dimensionResource(R.dimen.progress_indicator_height))
                        .clip(RoundedCornerShape(dimensionResource(R.dimen.progress_indicator_corner_radius)))
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.progress_percentage, currentProgress),
                        textAlign = TextAlign.Start,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = maxProgress,
                        textAlign = TextAlign.End,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }

}

@Composable
private fun RaceControls(
    onRunStateChange: (Boolean) -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier,
    isRunning: Boolean = true,
) {
    Column(
        modifier = modifier.padding(top = dimensionResource(R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))
    ) {
        Button(
            onClick = { onRunStateChange(!isRunning) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(if (isRunning) stringResource(R.string.pause) else stringResource(R.string.start))
        }
        OutlinedButton(
            onClick = onReset,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.reset))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RaceTrackerAppPreview() {
    RaceTrackerTheme {
        RaceTrackerApp()
    }
}
