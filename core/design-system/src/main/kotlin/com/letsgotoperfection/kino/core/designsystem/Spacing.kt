package com.letsgotoperfection.kino.core.designsystem

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Spacing system following 8dp grid
 * XS: 4px, SM: 8px, MD: 16px, LG: 24px, XL: 32px
 */
object Spacing {
    // Base spacing values
    val XS: Dp = 4.dp
    val SM: Dp = 8.dp
    val MD: Dp = 16.dp
    val LG: Dp = 24.dp
    val XL: Dp = 32.dp
    val XXL: Dp = 48.dp
    
    // Extended spacing for specific use cases
    val XS2: Dp = 2.dp
    val SM2: Dp = 6.dp
    val MD2: Dp = 12.dp
    val LG2: Dp = 20.dp
    val XL2: Dp = 40.dp
    val XXL2: Dp = 64.dp
    
    // Component specific spacing
    object Card {
        val padding: Dp = MD
        val margin: Dp = SM
        val radius: Dp = 12.dp
    }
    
    object Button {
        val padding: Dp = MD
        val height: Dp = 48.dp
        val radius: Dp = 8.dp
    }
    
    object Input {
        val padding: Dp = MD
        val height: Dp = 56.dp
        val radius: Dp = 8.dp
    }
    
    object Chip {
        val padding: Dp = 8.dp
        val height: Dp = 24.dp
        val radius: Dp = 12.dp
    }
    
    object TaskCard {
        val padding: Dp = MD
        val margin: Dp = SM
        val radius: Dp = 12.dp
        val height: Dp = 160.dp
        val width: Dp = 280.dp
    }
    
    object Section {
        val padding: Dp = MD
        val headerHeight: Dp = 40.dp
    }
    
    object Navigation {
        val height: Dp = 56.dp
        val iconSize: Dp = 24.dp
    }
    
    object AppBar {
        val height: Dp = 56.dp
        val padding: Dp = MD
    }
}
