package com.letsgotoperfection.kino.core.designsystem.accessibility

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.isEnabled
import androidx.compose.ui.test.isFocused
import androidx.compose.ui.test.isSelected

/**
 * Simple accessibility testing utilities for the Kino app.
 * Provides basic semantic matchers for accessibility validation.
 */
object AccessibilityTestUtils {

    /**
     * Creates a semantic matcher for elements with specific content description
     */
    fun hasContentDescription(text: String): SemanticsMatcher {
        return hasContentDescription(text)
    }

    /**
     * Creates a semantic matcher for elements with specific test tag
     */
    fun hasTestTag(testTag: String): SemanticsMatcher {
        return hasTestTag(testTag)
    }

    /**
     * Creates a semantic matcher for enabled elements
     */
    fun isEnabled(): SemanticsMatcher {
        return isEnabled()
    }

    /**
     * Creates a semantic matcher for focused elements
     */
    fun isFocused(): SemanticsMatcher {
        return isFocused()
    }

    /**
     * Creates a semantic matcher for selected elements
     */
    fun isSelected(): SemanticsMatcher {
        return isSelected()
    }
}
