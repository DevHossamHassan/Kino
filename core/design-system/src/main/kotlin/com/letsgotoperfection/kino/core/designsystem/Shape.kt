package com.letsgotoperfection.kino.core.designsystem

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Shape system for consistent corner radius across the app
 */
val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

// Custom shapes for specific components
object CustomShapes {
    val TaskCard = RoundedCornerShape(12.dp)
    val Button = RoundedCornerShape(8.dp)
    val Input = RoundedCornerShape(8.dp)
    val Chip = RoundedCornerShape(12.dp)
    val PriorityBadge = RoundedCornerShape(10.dp)
    val BottomSheet = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    val Modal = RoundedCornerShape(16.dp)
    val Section = RoundedCornerShape(8.dp)
    val ProgressBar = RoundedCornerShape(3.dp)
    val FloatingActionButton = RoundedCornerShape(16.dp)
}
