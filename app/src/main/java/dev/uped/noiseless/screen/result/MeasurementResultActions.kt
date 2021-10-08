package dev.uped.noiseless.screen.result

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.uped.noiseless.R

@Composable
fun MeasurementResultActions(
    onSaveAndShare: () -> Unit,
    onSave: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Button(onClick = onSaveAndShare) {
            Text(text = stringResource(id = R.string.save_and_share))
        }
        OutlinedButton(onClick = onSave) {
            Text(text = stringResource(id = R.string.save))
        }
    }
    Text(
        text = stringResource(id = R.string.location_warning),
        style = MaterialTheme.typography.body2,
        color = MaterialTheme.colors.onBackground.copy(0.7f),
        modifier = Modifier.padding(top = 32.dp)
    )
}