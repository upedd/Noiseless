package dev.uped.noiseless.screen.onboarding

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OnBoardingItem(
    @DrawableRes image: Int,
    text: String,
    scrollState: ScrollState = rememberScrollState()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier
                .size(400.dp)
                .padding(bottom = 48.dp),
            painter = painterResource(id = image),
            contentDescription = "On-boarding image"
        )

        Text(
            modifier = Modifier.verticalScroll(scrollState),
            text = text,
            style = MaterialTheme.typography.body1.copy(lineHeight = 32.sp, fontSize = 18.sp),
            color = MaterialTheme.colors.onBackground
        )
    }
}