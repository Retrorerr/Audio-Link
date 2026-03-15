package com.serene.music.ui.screens.nowplaying

import android.net.Uri
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.serene.music.ui.theme.VinylCenter
import com.serene.music.ui.theme.VinylDark
import com.serene.music.ui.theme.VinylGroove
import com.serene.music.ui.theme.VinylShine

/**
 * Vinyl turntable widget — the signature UI feature of Serene.
 *
 * Renders a spinning vinyl disc with album art at center, concentric groove rings,
 * a tonearm/needle positioned at the top-right, and a drop shadow.
 *
 * The disc rotates continuously when [isPlaying] is true and pauses on false.
 */
@Composable
fun TurntableWidget(
    albumArtUri: Uri?,
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    discSize: Dp = 300.dp
) {
    // Track accumulated rotation so it doesn't snap back when paused
    var currentRotation by remember { mutableFloatStateOf(0f) }

    val infiniteTransition = rememberInfiniteTransition(label = "vinyl_spin")
    val animatedRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "vinyl_rotation"
    )

    // Accumulate rotation while playing, freeze when paused
    val rotation = if (isPlaying) {
        (currentRotation + animatedRotation) % 360f
    } else {
        currentRotation
    }

    // When playback stops, save the current visual rotation so resume is seamless
    LaunchedEffect(isPlaying) {
        if (!isPlaying) {
            currentRotation = rotation
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Vinyl disc with rotation
        Box(
            modifier = Modifier
                .size(discSize)
                .shadow(elevation = 32.dp, shape = CircleShape, ambientColor = Color(0xFF7B2FBE))
                .graphicsLayer { rotationZ = rotation },
            contentAlignment = Alignment.Center
        ) {
            // Draw the vinyl record body
            Canvas(modifier = Modifier.size(discSize)) {
                drawVinylDisc(size.width / 2f)
            }

            // Album art at center (40% of disc size)
            val artSize = discSize * 0.40f
            Box(
                modifier = Modifier
                    .size(artSize)
                    .clip(CircleShape)
            ) {
                if (albumArtUri != null) {
                    AsyncImage(
                        model = albumArtUri,
                        contentDescription = "Album art",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        // Default gradient for no album art
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0xFF2D1B69), Color(0xFF0A0A0F))
                            )
                        )
                    }
                }
            }

            // Center spindle hole overlay
            Canvas(modifier = Modifier.size(discSize)) {
                val center = Offset(size.width / 2f, size.height / 2f)
                drawCircle(
                    color = VinylCenter,
                    radius = size.width * 0.025f,
                    center = center
                )
                drawCircle(
                    color = Color(0xFF888888),
                    radius = size.width * 0.008f,
                    center = center
                )
            }
        }

        // Tonearm — drawn outside the rotating box so it stays static
        Canvas(
            modifier = Modifier
                .size(discSize)
                .offset(x = discSize * 0.18f, y = -(discSize * 0.18f))
        ) {
            drawToneArm(discRadius = size.width / 2f, isPlaying = isPlaying)
        }
    }
}

/**
 * Draws the vinyl disc with realistic grooves on the canvas.
 */
private fun DrawScope.drawVinylDisc(radius: Float) {
    val center = Offset(size.width / 2f, size.height / 2f)

    // Base disc
    drawCircle(
        color = VinylDark,
        radius = radius,
        center = center
    )

    // Subtle radial shine gradient
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(VinylShine, Color.Transparent),
            center = center,
            radius = radius
        ),
        radius = radius,
        center = center
    )

    // Concentric groove rings — spacing between 4–12px, varying opacity
    val albumArtRadius = radius * 0.22f
    val grooveAreaStart = albumArtRadius + radius * 0.07f
    val grooveAreaEnd = radius * 0.88f

    var currentR = grooveAreaStart
    var groupIndex = 0
    while (currentR < grooveAreaEnd) {
        val alpha = if (groupIndex % 3 == 0) 0.18f else if (groupIndex % 3 == 1) 0.10f else 0.06f
        drawCircle(
            color = VinylGroove.copy(alpha = alpha),
            radius = currentR,
            center = center,
            style = Stroke(width = 0.8f)
        )
        // Slightly varying groove spacing for realism
        currentR += if (groupIndex % 5 == 0) 6f else 4f
        groupIndex++
    }

    // Label area ring (inner label border)
    drawCircle(
        color = Color(0xFF2A1A4A).copy(alpha = 0.6f),
        radius = albumArtRadius + radius * 0.05f,
        center = center,
        style = Stroke(width = 2f)
    )

    // Outer rim
    drawCircle(
        color = Color(0xFF333344).copy(alpha = 0.5f),
        radius = radius - 2f,
        center = center,
        style = Stroke(width = 3f)
    )
}

/**
 * Draws the tonearm (needle) that pivots towards the vinyl.
 * When playing, the arm is angled inward ~30°; otherwise slightly lifted.
 */
private fun DrawScope.drawToneArm(discRadius: Float, isPlaying: Boolean) {
    val pivotX = size.width * 0.78f
    val pivotY = size.height * 0.12f

    val armAngle = if (isPlaying) 30f else 15f

    rotate(degrees = armAngle, pivot = Offset(pivotX, pivotY)) {
        val armLength = discRadius * 0.85f
        val armEndX = pivotX - armLength * 0.7f
        val armEndY = pivotY + armLength

        // Arm body
        drawLine(
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFFAAAAAA), Color(0xFF666666)),
                start = Offset(pivotX, pivotY),
                end = Offset(armEndX, armEndY)
            ),
            start = Offset(pivotX, pivotY),
            end = Offset(armEndX, armEndY),
            strokeWidth = 6f,
            cap = StrokeCap.Round
        )

        // Pivot circle
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFCCCCCC), Color(0xFF555555)),
                center = Offset(pivotX, pivotY),
                radius = 14f
            ),
            radius = 14f,
            center = Offset(pivotX, pivotY)
        )
        drawCircle(
            color = Color(0xFF111111),
            radius = 5f,
            center = Offset(pivotX, pivotY)
        )

        // Cartridge/headshell at needle end
        val headX = armEndX - 8f
        val headY = armEndY - 5f
        drawLine(
            color = Color(0xFF888888),
            start = Offset(armEndX, armEndY),
            end = Offset(headX, headY + 16f),
            strokeWidth = 4f,
            cap = StrokeCap.Round
        )
        // Needle tip diamond
        drawCircle(
            color = Color(0xFFE879F9),
            radius = 4f,
            center = Offset(headX, headY + 20f)
        )
    }
}
