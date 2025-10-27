package com.alaershov.mars_colony.demo_dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alaershov.mars_colony.demo_dialog.component.DemoDialogComponent
import com.alaershov.mars_colony.demo_dialog.component.PreviewDemoDialogComponent
import com.alaershov.mars_colony.ui.theme.MarsColonyTheme

@Composable
fun DemoDialog(
    component: DemoDialogComponent,
    modifier: Modifier = Modifier
) {
    val state by component.state.collectAsState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
    ) {
        Text(
            text = state.message,
            style = MaterialTheme.typography.headlineLarge
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    component.onCloseClick()
                },
                modifier = Modifier
                    .wrapContentWidth()
            ) {
                Text(text = "Close")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = {
                    component.onCloseAllClick()
                },
                modifier = Modifier
                    .wrapContentWidth()
            ) {
                Text(text = "Close All")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = {
                    component.onCloseRandomClick()
                },
                modifier = Modifier
                    .wrapContentWidth()
            ) {
                Text(text = "Close Random")
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

@Preview(device = "id:pixel_9")
@Composable
private fun DemoDialogPreview() {
    MarsColonyTheme {
        DemoDialog(PreviewDemoDialogComponent())
    }
}
