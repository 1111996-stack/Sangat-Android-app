package com.sangat.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sangat.app.R

// welcome_pic.png replaces the canvas-drawn logo
// Same function name so NO other file needs changing
@Composable
fun SangatLogo(size: Dp = 160.dp) {
    Box(Modifier.size(size), contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(id = R.drawable.welcome_pic),
            contentDescription = "Sangat Logo",
            modifier = Modifier.size(size),
            contentScale = ContentScale.Fit
        )
    }
}
