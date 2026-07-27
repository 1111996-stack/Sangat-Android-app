package com.sangat.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.sangat.app.R

val InderFamily = FontFamily(
    Font(R.font.inder_regular, FontWeight.Normal)
)

val InterFamily = InderFamily
val DisplayFamily = InderFamily

val SangatTypography = Typography(
    displayLarge  = TextStyle(fontFamily = DisplayFamily, fontWeight = FontWeight.Bold,   fontSize = 48.sp, letterSpacing = (-1).sp),
    headlineLarge = TextStyle(fontFamily = DisplayFamily, fontWeight = FontWeight.Bold,   fontSize = 30.sp, letterSpacing = (-0.5).sp),
    headlineMedium= TextStyle(fontFamily = DisplayFamily, fontWeight = FontWeight.Bold,   fontSize = 24.sp),
    titleLarge    = TextStyle(fontFamily = DisplayFamily, fontWeight = FontWeight.Bold,   fontSize = 20.sp),
    bodyLarge     = TextStyle(fontFamily = InterFamily,   fontWeight = FontWeight.Normal, fontSize = 14.sp),
    bodyMedium    = TextStyle(fontFamily = InterFamily,   fontWeight = FontWeight.Normal, fontSize = 13.sp),
    bodySmall     = TextStyle(fontFamily = InterFamily,   fontWeight = FontWeight.Normal, fontSize = 11.sp),
    labelSmall    = TextStyle(fontFamily = InterFamily,   fontWeight = FontWeight.SemiBold,fontSize = 10.sp, letterSpacing = 2.sp),
)
