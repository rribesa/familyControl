package br.com.rribesa.familycontrol.core.ui.screen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
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
import androidx.compose.foundation.layout.BoxScope
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
import br.com.rribesa.familycontrol.core.ui.R
import br.com.rribesa.familycontrol.core.ui.theme.familyControlTheme
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val ENTRANCE_ANIMATION_DURATION = 800
private const val LOGO_FLOAT_ANIMATION_DURATION = 2000
private const val PROGRESS_SIMULATION_DURATION = 2500
private const val DELAY_TEXT_MS = 200
private const val DELAY_LOADER_MS = 400
private const val LOGO_FLOAT_TARGET_Y = -12f
private const val MAX_SUBTITLE_WIDTH_DP = 280

private const val INITIAL_LOGO_OFFSET = 40f
private const val INITIAL_TEXT_OFFSET = 30f
private const val INITIAL_LOADER_OFFSET = 20f
private const val ORNAMENT_ALPHA = 0.15f
private const val ORNAMENT_SIZE = 350
private const val ORNAMENT_OFFSET = 100
private const val ORNAMENT_OFFSET_NEG = -100
private const val ORNAMENT_BLUR = 90
private const val ORNAMENT_RADIUS = 175

private class SplashAnimations(
    val logoAlpha: Animatable<Float, AnimationVector1D>,
    val logoOffsetY: Animatable<Float, AnimationVector1D>,
    val textAlpha: Animatable<Float, AnimationVector1D>,
    val textOffsetY: Animatable<Float, AnimationVector1D>,
    val loaderAlpha: Animatable<Float, AnimationVector1D>,
    val loaderOffsetY: Animatable<Float, AnimationVector1D>
)

@Composable
fun splashScreen(
    modifier: Modifier = Modifier,
    onSplashFinished: () -> Unit = {}
) {
    val logoAlpha = remember { Animatable(0f) }
    val logoOffsetY = remember { Animatable(INITIAL_LOGO_OFFSET) }
    val textAlpha = remember { Animatable(0f) }
    val textOffsetY = remember { Animatable(INITIAL_TEXT_OFFSET) }
    val loaderAlpha = remember { Animatable(0f) }
    val loaderOffsetY = remember { Animatable(INITIAL_LOADER_OFFSET) }
    val progress = remember { Animatable(0f) }

    val animations = remember {
        SplashAnimations(
            logoAlpha = logoAlpha,
            logoOffsetY = logoOffsetY,
            textAlpha = textAlpha,
            textOffsetY = textOffsetY,
            loaderAlpha = loaderAlpha,
            loaderOffsetY = loaderOffsetY
        )
    }

    LaunchedEffect(Unit) {
        startEntranceAnimations(
            scope = this,
            animations = animations,
            progress = progress,
            onSplashFinished = onSplashFinished
        )
    }

    val infiniteTransition = rememberInfiniteTransition(label = "logo_float")
    val logoFloatY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = LOGO_FLOAT_TARGET_Y,
        animationSpec = infiniteRepeatable(
            animation = tween(LOGO_FLOAT_ANIMATION_DURATION, easing = EaseInOutSine),
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
        splashBackground()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            splashLogo(logoAlpha.value, logoOffsetY.value + logoFloatY)
            Spacer(modifier = Modifier.height(32.dp))
            splashText(textAlpha.value, textOffsetY.value)
            Spacer(modifier = Modifier.height(48.dp))
            splashLoader(loaderAlpha.value, loaderOffsetY.value, progress.value)
        }
    }
}

private fun startEntranceAnimations(
    scope: CoroutineScope,
    animations: SplashAnimations,
    progress: Animatable<Float, AnimationVector1D>,
    onSplashFinished: () -> Unit
) {
    scope.launch {
        animations.logoAlpha.animateTo(1f, animationSpec = tween(ENTRANCE_ANIMATION_DURATION, easing = EaseOutQuad))
    }
    scope.launch {
        animations.logoOffsetY.animateTo(0f, animationSpec = tween(ENTRANCE_ANIMATION_DURATION, easing = EaseOutQuad))
    }
    scope.launch {
        delay(DELAY_TEXT_MS.milliseconds)
        animations.textAlpha.animateTo(1f, animationSpec = tween(ENTRANCE_ANIMATION_DURATION, easing = EaseOutQuad))
    }
    scope.launch {
        delay(DELAY_TEXT_MS.milliseconds)
        animations.textOffsetY.animateTo(0f, animationSpec = tween(ENTRANCE_ANIMATION_DURATION, easing = EaseOutQuad))
    }
    scope.launch {
        delay(DELAY_LOADER_MS.milliseconds)
        animations.loaderAlpha.animateTo(1f, animationSpec = tween(ENTRANCE_ANIMATION_DURATION, easing = EaseOutQuad))
    }
    scope.launch {
        delay(DELAY_LOADER_MS.milliseconds)
        animations.loaderOffsetY.animateTo(0f, animationSpec = tween(ENTRANCE_ANIMATION_DURATION, easing = EaseOutQuad))
    }
    scope.launch {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = PROGRESS_SIMULATION_DURATION, easing = FastOutSlowInEasing)
        )
        onSplashFinished()
    }
}

@Composable
private fun BoxScope.splashBackground() {
    Box(
        modifier = Modifier
            .size(ORNAMENT_SIZE.dp)
            .offset(x = ORNAMENT_OFFSET_NEG.dp, y = ORNAMENT_OFFSET_NEG.dp)
            .alpha(ORNAMENT_ALPHA)
            .blur(ORNAMENT_BLUR.dp)
            .background(
                Brush.radialGradient(
                    colors = listOf(MaterialTheme.colorScheme.primaryContainer, Color.Transparent)
                ),
                shape = RoundedCornerShape(ORNAMENT_RADIUS.dp)
            )
    )
    Box(
        modifier = Modifier
            .size(ORNAMENT_SIZE.dp)
            .align(Alignment.BottomEnd)
            .offset(x = ORNAMENT_OFFSET.dp, y = ORNAMENT_OFFSET.dp)
            .alpha(ORNAMENT_ALPHA)
            .blur(ORNAMENT_BLUR.dp)
            .background(
                Brush.radialGradient(
                    colors = listOf(MaterialTheme.colorScheme.secondaryContainer, Color.Transparent)
                ),
                shape = RoundedCornerShape(ORNAMENT_RADIUS.dp)
            )
    )
}

@Composable
private fun splashLogo(alphaVal: Float, translationYVal: Float) {
    Box(
        modifier = Modifier
            .graphicsLayer {
                alpha = alphaVal
                translationY = translationYVal
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
}

@Composable
private fun splashText(alphaVal: Float, translationYVal: Float) {
    Column(
        modifier = Modifier
            .graphicsLayer {
                alpha = alphaVal
                translationY = translationYVal
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
            modifier = Modifier.widthIn(max = MAX_SUBTITLE_WIDTH_DP.dp)
        )
    }
}

@Composable
private fun splashLoader(alphaVal: Float, translationYVal: Float, progressVal: Float) {
    Column(
        modifier = Modifier
            .graphicsLayer {
                alpha = alphaVal
                translationY = translationYVal
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LinearProgressIndicator(
            progress = { progressVal },
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

@Preview(name = "Splash - Normal Device", widthDp = 360, heightDp = 640, showBackground = true)
@Composable
internal fun splashPreviewNormal() {
    familyControlTheme {
        splashScreen()
    }
}

@Preview(name = "Splash - Large Device (Tablet Portrait)", widthDp = 768, heightDp = 1024, showBackground = true)
@Composable
internal fun splashPreviewLarge() {
    familyControlTheme {
        splashScreen()
    }
}

@Preview(name = "Splash - Expanded Device (Landscape)", widthDp = 1280, heightDp = 800, showBackground = true)
@Composable
internal fun splashPreviewExpanded() {
    familyControlTheme {
        splashScreen()
    }
}
