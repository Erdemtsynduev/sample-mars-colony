package com.alaershov.mars_colony.demo_dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alaershov.mars_colony.demo_dialog.component.DemoDialogComponent
import com.alaershov.mars_colony.demo_dialog.component.PreviewDemoDialogComponent
import com.alaershov.mars_colony.ui.theme.MarsColonyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DemoDialog(
    component: DemoDialogComponent,
    modifier: Modifier = Modifier
) {
    val state by component.state.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = component::onCloseClick,
        sheetState = sheetState,
    ) {
        Column(
            modifier = modifier
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .imePadding()
                .navigationBarsPadding()
                .padding(16.dp),
        ) {
            Text(
                text = state.message,
                style = MaterialTheme.typography.headlineMedium
            )

            Column {
                for (i in 0 until state.size) {
                    Text("Item $i")
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        component.onAddFirstClick()
                    },
                    modifier = Modifier
                        .wrapContentWidth()
                ) {
                    Text(text = "<- Add")
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = {
                        component.onAddMiddleClick()
                    },
                    modifier = Modifier
                        .wrapContentWidth()
                ) {
                    Text(text = "Add")
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = {
                        component.onAddClick()
                    },
                    modifier = Modifier
                        .wrapContentWidth()
                ) {
                    Text(text = "Add ->")
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        component.onShiftBackwardClick()
                    },
                    modifier = Modifier
                        .wrapContentWidth()
                ) {
                    Text(text = "<- Shift")
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = {
                        component.onShuffleClick()
                    },
                    modifier = Modifier
                        .wrapContentWidth()
                ) {
                    Text(text = "Shuffle")
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = {
                        component.onShiftForwardClick()
                    },
                    modifier = Modifier
                        .wrapContentWidth()
                ) {
                    Text(text = "Shift ->")
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        component.onCloseAllClick()
                    },
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(text = "Close All")
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = {
                        component.onCloseHalfClick()
                    },
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(text = "Close Half")
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = {
                        component.onReplaceClick()
                    },
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(text = "Replace")
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        component.onCloseFirstClick()
                    },
                    modifier = Modifier
                        .wrapContentWidth()
                ) {
                    Text(text = "<- Close")
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = {
                        component.onCloseSecondClick()
                    },
                    modifier = Modifier
                        .wrapContentWidth()
                ) {
                    Text(text = "Close Second")
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = {
                        component.onCloseClick()
                    },
                    modifier = Modifier
                        .wrapContentWidth()
                ) {
                    Text(text = "Close ->")
                }
            }

            Button(
                onClick = {
                    component.onButtonClick()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Text(state.button)
            }
        }
    }
}

@Preview(device = "id:pixel_9", showBackground = true)
@Composable
private fun DemoDialogPreview() {
    MarsColonyTheme {
        DemoDialog(PreviewDemoDialogComponent())
    }
}
