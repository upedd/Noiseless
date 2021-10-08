package dev.uped.noiseless.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import dev.uped.noiseless.R

@Composable
fun BottomNav(
    route: String?,
    onHomeClicked: () -> Unit,
    onMeasurementClicked: () -> Unit,
    onMeasurementListClicked: () -> Unit
) {
    if (route != null && route == "home" || route == "measurements") {
        BottomNavigation(
            modifier = Modifier.padding(
                rememberInsetsPaddingValues(
                    LocalWindowInsets.current.navigationBars
                )
            ),
        ) {
            BottomNavigationItem(
                icon = { Icon(painterResource(id = R.drawable.map), contentDescription = null) },
                label = { Text(stringResource(id = R.string.map)) },
                selected = route == "home",
                onClick = onHomeClicked
            )
            BottomNavigationItem(
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                label = { Text(stringResource(id = R.string.new_measurement)) },
                selected = false,
                onClick = onMeasurementClicked
            )
            BottomNavigationItem(
                icon = { Icon(Icons.Filled.List, contentDescription = null) },
                label = { Text(stringResource(id = R.string.measurements)) },
                selected = route == "measurements",
                onClick = onMeasurementListClicked
            )
        }
    }
}