package dev.uped.noiseless.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.uped.noiseless.R

@Composable
fun PermissionRationale(
    modifier: Modifier,
    onRationaleClose: () -> Unit,
    onRationaleSuccess: () -> Unit,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .padding(50.dp)
    ) {
        Column(
            Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            content()

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                OutlinedButton(onClick = onRationaleClose) {
                    Text(stringResource(id = R.string.decline))
                }
                Button(onClick = onRationaleSuccess) {
                    Text(stringResource(id = R.string.allow))
                }
            }
        }
    }
}