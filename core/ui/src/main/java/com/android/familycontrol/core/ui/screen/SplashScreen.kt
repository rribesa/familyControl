@file:Suppress("MagicNumber", "LongMethod", "FunctionNaming")

package com.android.familycontrol.core.ui.screen


import androidx.compose.animation.core.Animatable

import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.EaseOutQuad
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.familycontrol.core.ui.R
import com.android.familycontrol.core.ui.theme.FamilyControlTheme
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    onSplashFinished: () -> Unit = {}
) {
    // Entrance animations state
    val logoAlpha = remember { Animatable(0f) }
    val logoOffsetY = remember { Animatable(40f) }

    val textAlpha = remember { Animatable(0f) }
    val textOffsetY = remember { Animatable(30f) }

    val loaderAlpha = remember { Animatable(0f) }
    val loaderOffsetY = remember { Animatable(20f) }

    // Loading progress animation
    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Trigger entrance animations
        launch {
            logoAlpha.animateTo(1f, animationSpec = tween(800, easing = EaseOutQuad))
        }
        launch {
            logoOffsetY.animateTo(0f, animationSpec = tween(800, easing = EaseOutQuad))
        }

        launch {
            delay(200.milliseconds)
            textAlpha.animateTo(1f, animationSpec = tween(800, easing = EaseOutQuad))
        }
        launch {
            delay(200.milliseconds)
            textOffsetY.animateTo(0f, animationSpec = tween(800, easing = EaseOutQuad))
        }

        launch {
            delay(400.milliseconds)
            loaderAlpha.animateTo(1f, animationSpec = tween(800, easing = EaseOutQuad))
        }
        launch {
            delay(400.milliseconds)
            loaderOffsetY.animateTo(0f, animationSpec = tween(800, easing = EaseOutQuad))
        }

        // Progress bar simulation (2.5 seconds)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2500, easing = FastOutSlowInEasing)
        )
        onSplashFinished()
    }

    // Soft floating animation for the logo
    val infiniteTransition = rememberInfiniteTransition(label = "logo_float")
    val logoFloatY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -12f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logo_float_y"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        // Background Ornaments (Subtle Layer Tonal)
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .size(350.dp)
                    .offset(x = (-100).dp, y = (-100).dp)
                    .alpha(0.15f)
                    .blur(90.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(MaterialTheme.colorScheme.primaryContainer, Color.Transparent)
                        ),
                        shape = RoundedCornerShape(175.dp)
                    )
            )
            Box(
                modifier = Modifier
                    .size(350.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = 100.dp, y = 100.dp)
                    .alpha(0.15f)
                    .blur(90.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(MaterialTheme.colorScheme.secondaryContainer, Color.Transparent)
                        ),
                        shape = RoundedCornerShape(175.dp)
                    )
            )
        }

        // Main content Column
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Floating Logo
            Box(
                modifier = Modifier
                    .graphicsLayer {
                        alpha = logoAlpha.value
                        translationY = logoOffsetY.value + logoFloatY
                    }
                    .size(200.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.splash_logo),
                    contentDescription = stringResource(id = R.string.splash_title),
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Text content
            Column(
                modifier = Modifier
                    .graphicsLayer {
                        alpha = textAlpha.value
                        translationY = textOffsetY.value
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.splash_title),
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = stringResource(id = R.string.splash_subtitle),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp,
                    modifier = Modifier.widthIn(max = 280.dp)
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Loading indicator
            Column(
                modifier = Modifier
                    .graphicsLayer {
                        alpha = loaderAlpha.value
                        translationY = loaderOffsetY.value
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LinearProgressIndicator(
                    progress = { progress.value },
                    modifier = Modifier
                        .width(120.dp)
                        .height(4.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(id = R.string.splash_loading),
                    color = MaterialTheme.colorScheme.outline,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// Previews
@Preview(name = "Splash - Normal Device", widthDp = 360, heightDp = 640, showBackground = true)
@Composable
fun SplashPreviewNormal() {
    FamilyControlTheme {
        SplashScreen()
    }
}

@Preview(name = "Splash - Large Device (Tablet Portrait)", widthDp = 768, heightDp = 1024, showBackground = true)
@Composable
fun SplashPreviewLarge() {
    FamilyControlTheme {
        SplashScreen()
    }
}

@Preview(name = "Splash - Expanded Device (Landscape)", widthDp = 1280, heightDp = 800, showBackground = true)
@Composable
fun SplashPreviewExpanded() {
    FamilyControlTheme {
        SplashScreen()
    }
}
