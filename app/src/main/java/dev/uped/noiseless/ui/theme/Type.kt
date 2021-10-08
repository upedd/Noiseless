package dev.uped.noiseless.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import dev.uped.noiseless.R

val QuickSand = FontFamily(
    Font(R.font.quicksand),
    Font(R.font.quicksand_bold, weight = FontWeight.Bold)
)

val Typography = Typography(
    defaultFontFamily = QuickSand
)