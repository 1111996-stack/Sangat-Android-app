package com.sangat.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sangat.app.ui.components.AmbientBackground
import com.sangat.app.ui.components.GlassSurface
import com.sangat.app.ui.components.SangatLogo
import com.sangat.app.ui.theme.SangatColors
import com.sangat.app.ui.theme.SangatGradient

@Composable
fun SplashScreen(onStart: () -> Unit) {
    AmbientBackground {
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .padding(top = 96.dp, bottom = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Floating animation — up and down
            val infinite = rememberInfiniteTransition(label = "float")
            val translate by infinite.animateFloat(
                initialValue = 0f,
                targetValue  = -10f,
                animationSpec = infiniteRepeatable(
                    tween(2000, easing = FastOutSlowInEasing),
                    RepeatMode.Reverse
                ),
                label = "y"
            )

            Spacer(Modifier.weight(1f))

            // welcome_pic.png — floating up/down via SangatLogo
            Box(Modifier.offset(y = translate.dp)) {
                SangatLogo(280.dp)
            }

            Text(
                "SANGAT",
                color      = SangatColors.NeonCyan,
                fontSize   = 48.sp,
                fontWeight = FontWeight.Bold,
                modifier   = Modifier.padding(top = 16.dp)
            )
            Text(
                "Your battery now speaks.\nFunny voice alerts for charging, unplug & full.",
                color       = SangatColors.MutedForeground,
                fontSize    = 13.sp,
                textAlign   = TextAlign.Center,
                modifier    = Modifier.padding(top = 12.dp)
            )

            Row(
                Modifier.padding(top = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Voice Alerts", "Record Your Own", "Background").forEach {
                    GlassSurface(corner = 999.dp) {
                        Text(
                            it,
                            color    = SangatColors.Foreground.copy(alpha = 0.8f),
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            // Container Box with padding for shadow
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp) // Extra room for shadow
                    .shadow(16.dp, RoundedCornerShape(24.dp), ambientColor = Color.Black, spotColor = Color.Black)
                    .clip(RoundedCornerShape(24.dp))
                    .background(SangatGradient)
                    .clickable { onStart() }
                    .padding(vertical = 18.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Get Started  →",
                    color      = SangatColors.PrimaryForeground,
                    fontSize   = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                "Made with Arman Baloch",
                color         = SangatColors.MutedForeground,
                fontSize      = 11.sp,
                letterSpacing = 2.sp,
                modifier      = Modifier.padding(top = 24.dp)
            )
        }
    }
}
