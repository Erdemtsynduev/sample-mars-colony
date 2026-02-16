package com.alaershov.mars_colony.habitat.list_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alaershov.mars_colony.habitat.build_dialog.HabitatBuildDialog
import com.alaershov.mars_colony.habitat.dismantle_dialog.HabitatDismantleDialog
import com.alaershov.mars_colony.habitat.list_screen.component.HabitatListScreenComponent
import com.alaershov.mars_colony.habitat.list_screen.component.PreviewHabitatListScreenComponent
import com.alaershov.mars_colony.message_dialog.MessageDialog
import com.alaershov.mars_colony.ui.R
import com.alaershov.mars_colony.ui.theme.MarsColonyTheme
import com.arkivanov.decompose.extensions.compose.subscribeAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitatListScreen(component: HabitatListScreenComponent) {
    Box {
        ScreenContent(component)

        val dialogPages by component.dialogPages.subscribeAsState()
        dialogPages.items.forEach { entry ->
            when (val dialogChild = entry.instance) {
                is DialogChild.HabitatBuild -> HabitatBuildDialog(dialogChild.component)
                is DialogChild.HabitatDismantle -> HabitatDismantleDialog(dialogChild.component)
                is DialogChild.ConfirmDismantle -> MessageDialog(dialogChild.component)
                else -> { }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScreenContent(
    component: HabitatListScreenComponent,
) {
    val state by component.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Habitat List",
                    style = MaterialTheme.typography.headlineMedium
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = {
                        component.onBackClick()
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_back),
                        contentDescription = "Back"
                    )
                }
            },
            actions = {
                IconButton(
                    onClick = component::onBuildClick,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_add),
                        contentDescription = "Build a Habitat"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
            )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Total Habitat Capacity",
                style = MaterialTheme.typography.titleLarge,
            )

            Text(
                text = state.totalCapacity.toString(),
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            state.list.forEach { habitat ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            component.onHabitatClick(habitat.id)
                        }
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "Habitat ID: ${habitat.id}",
                        style = MaterialTheme.typography.titleMedium,
                    )

                    Text(
                        text = "Capacity: ${habitat.capacity}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Preview(device = "id:pixel_9")
@Composable
private fun HabitatListScreenPreview() {
    MarsColonyTheme {
        HabitatListScreen(PreviewHabitatListScreenComponent())
    }
}
