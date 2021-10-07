package dev.uped.noiseless.ui.screen

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import dev.uped.noiseless.R
import dev.uped.noiseless.SEEN_ON_BOARDING_KEY
import dev.uped.noiseless.SHARED_PREFERENCES_KEY
import dev.uped.noiseless.ui.theme.NoiselessTheme

// Source: https://github.com/google/accompanist/blob/main/sample/src/main/java/com/google/accompanist/sample/pager/HorizontalPagerWithIndicatorSample.kt
// Under Apache 2.0 license: https://github.com/google/accompanist/blob/main/LICENSE
@OptIn(ExperimentalPagerApi::class)
@Composable
fun OnBoardingScreen(onEnd: () -> Unit) {

    val context = LocalContext.current
    val sharedPreferences = remember {
        context.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
    }

    fun onEndClicked() {
        sharedPreferences.edit().putBoolean(SEEN_ON_BOARDING_KEY, true).apply()
        onEnd()
    }

    Column(Modifier.fillMaxSize()) {
        val pagerState = rememberPagerState()

        HorizontalPager(
            count = 3,
            state = pagerState,
            // Add 32.dp horizontal padding to 'center' the pages
            contentPadding = PaddingValues(horizontal = 32.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) { page ->
            when (page) {
                0 -> OnBoardingItem(
                    image = R.drawable.city,
                    text = stringResource(id = R.string.on_boarding1)
                )
                1 -> OnBoardingItem(
                    image = R.drawable.health,
                    text = stringResource(id = R.string.on_boarding2)
                )
                else -> OnBoardingItem(
                    image = R.drawable.walk,
                    text = stringResource(id = R.string.on_boarding3)
                )
            }
        }

        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp),
            inactiveColor = MaterialTheme.colors.onBackground.copy(alpha = 0.85f),
            activeColor = MaterialTheme.colors.primary
        )

        if (pagerState.currentPage < 2) {
            OutlinedButton(
                modifier = Modifier
                    .padding(bottom = 24.dp, end = 24.dp)
                    .align(Alignment.End),
                onClick = { onEndClicked() }
            ) {
                Text(text = "Pomiń")
            }
        } else {
            Button(
                modifier = Modifier
                    .padding(bottom = 24.dp, end = 24.dp)
                    .align(Alignment.End),
                onClick = { onEndClicked() }
            ) {
                Text(text = "Kontynuuj")
            }
        }
    }
}

@Composable
fun OnBoardingItem(@DrawableRes image: Int, text: String) {
    val scrollState = rememberScrollState()
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewOnBoardingScreen() {
    NoiselessTheme {
        OnBoardingScreen {}
    }
}