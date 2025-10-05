# Compose Previews - Modern Best Practices Guide

## Overview

This directory contains a comprehensive preview system for the Kino task management app, following the latest Android best practices for Compose previews. Every screen and component has multiple preview configurations to ensure quality across all devices and conditions.

## Structure

```
preview/
├── Previews.kt                    # Multi-preview annotations
├── PreviewData.kt                 # Sample data for previews
├── PreviewParameterProviders.kt   # Data providers for parameterized previews
└── README.md                     # This documentation
```

## Multi-Preview Annotations

### Core Annotations

- **`@ThemePreviews`** - Light and dark theme variations
- **`@PhonePreviews`** - Different phone screen sizes
- **`@TabletPreviews`** - Tablet screen sizes
- **`@FontScalePreviews`** - Accessibility font scale variations
- **`@LocalePreviews`** - RTL and localization support
- **`@ScreenPreviews`** - Full screen previews with system UI
- **`@ComponentPreviews`** - Component-level previews
- **`@InteractivePreviews`** - Interactive previews for testing

### Combined Annotations

- **`@CompletePreviews`** - Combines theme and font scale previews
- **`@InteractivePreviews`** - For testing user interactions

## Preview Data

### Sample Data Objects

The `PreviewData` object provides comprehensive sample data for all app features:

- **Tasks**: Various states (high priority, medium, low, overdue, completed)
- **Notes**: Pinned and regular notes with rich content
- **Media**: Different file types (images, PDFs, videos)
- **Kanban Data**: Complete board with sections and columns
- **Checklists**: Sample checklist items with completion states

### UI State Classes

- **`UiState<T>`** - Sealed class for loading, success, and error states
- **Empty States** - Predefined empty state data for all features

## Preview Parameter Providers

### Data Providers

- **`TaskPreviewProvider`** - Different task states and priorities
- **`NotePreviewProvider`** - Various note configurations
- **`MediaPreviewProvider`** - Different media file types
- **`KanbanDataPreviewProvider`** - Various kanban board configurations
- **`PriorityPreviewProvider`** - All priority levels
- **`LabelsPreviewProvider`** - Different label configurations

### Usage Example

```kotlin
@ThemePreviews
@Composable
private fun TaskCardPreview(
    @PreviewParameter(TaskPreviewProvider::class) task: Task
) {
    KinoTheme {
        TaskCard(task = task, onTaskClick = {})
    }
}
```

## Component Previews

### TaskCard Component

- **Parameterized Preview** - Shows all task states
- **High Priority Preview** - Specific priority focus
- **Long Text Preview** - Edge case with text truncation
- **Minimal Preview** - Task with no metadata
- **Maximal Preview** - Task with all features
- **Font Scale Preview** - Accessibility testing
- **Interactive Preview** - User interaction testing

### PriorityBadge Component

- **Parameterized Preview** - All priority levels
- **All Priorities Preview** - Side-by-side comparison

### LabelChip Component

- **Basic Preview** - Different chip states
- **Multiple Labels Preview** - Various label configurations

### SectionHeader Component

- **Expanded State** - Open section header
- **Collapsed State** - Closed section header
- **All States** - Multiple headers together

### EmptyState Component

- **With Action** - Empty state with call-to-action button
- **Without Action** - Simple empty state
- **All Types** - Different empty state variations

## Screen Previews

### Kanban Board Screen

- **Full Data** - Complete board with all sections
- **Empty State** - No tasks available
- **Tablet View** - Larger screen layout
- **Collapsed Sections** - All sections closed
- **Expanded Sections** - All sections open
- **Font Scale** - Accessibility testing
- **RTL Support** - Arabic layout testing

### Notes Screen

- **Full Data** - List of notes with different states
- **Empty State** - No notes available
- **Different States** - Various note configurations
- **Tablet View** - Larger screen layout
- **Font Scale** - Accessibility testing
- **RTL Support** - Arabic layout testing

### Media Manager Screen

- **Grid View** - Media files in grid layout
- **List View** - Media files in list layout
- **Empty State** - No media files
- **Different Types** - Various media file types
- **Tablet View** - Larger screen layout
- **Font Scale** - Accessibility testing
- **RTL Support** - Arabic layout testing

### Task Detail Screen

- **Full Data** - Complete task with all sections
- **Minimal Data** - Task with basic information only
- **Completed Task** - Finished task state
- **Overdue Task** - Past due date task
- **Tablet View** - Larger screen layout
- **Font Scale** - Accessibility testing
- **RTL Support** - Arabic layout testing

## Best Practices

### 1. Use Multi-Preview Annotations

Always use the predefined multi-preview annotations for consistency:

```kotlin
@ThemePreviews
@Composable
private fun MyComponentPreview() {
    // Preview implementation
}
```

### 2. Parameterized Previews

Use `@PreviewParameter` for data variations:

```kotlin
@ThemePreviews
@Composable
private fun MyComponentPreview(
    @PreviewParameter(MyDataProvider::class) data: MyData
) {
    // Preview implementation
}
```

### 3. Edge Case Testing

Always include previews for edge cases:

- Long text that might overflow
- Empty states
- Maximum data scenarios
- Minimum data scenarios

### 4. Accessibility Testing

Include font scale and RTL previews:

```kotlin
@FontScalePreviews
@Composable
private fun MyComponentFontScalePreview() {
    // Preview implementation
}
```

### 5. Interactive Previews

Use interactive previews for complex interactions:

```kotlin
@Preview(name = "Interactive", showBackground = true)
@Composable
private fun MyInteractivePreview() {
    var state by remember { mutableStateOf(initialState) }
    // Interactive implementation
}
```

## Preview Organization

### Grouping

Use preview groups to organize related previews:

```kotlin
@Preview(name = "High Priority", group = "Priority")
@Preview(name = "Medium Priority", group = "Priority")
@Composable
private fun PriorityPreviews() {
    // Preview implementation
}
```

### Naming Convention

- Use descriptive names: "Component - State"
- Include theme information: "Light", "Dark"
- Include device information: "Phone", "Tablet"
- Include accessibility info: "Font Scale Large", "RTL"

## Testing Strategy

### Visual Regression Testing

Use screenshot testing with Roborazzi for visual regression:

```kotlin
@Test
fun component_screenshot() {
    composeTestRule.setContent {
        MyComponent()
    }
    
    composeTestRule.onRoot()
        .captureToImage()
        .assertAgainstGolden("component_golden")
}
```

### Accessibility Testing

Test with different font scales and RTL layouts:

```kotlin
@FontScalePreviews
@Composable
private fun AccessibilityPreviews() {
    // Test with different font scales
}
```

### Device Testing

Test on different screen sizes:

```kotlin
@PhonePreviews
@TabletPreviews
@Composable
private fun DevicePreviews() {
    // Test on different devices
}
```

## Maintenance

### Adding New Previews

1. Use existing multi-preview annotations
2. Add to appropriate preview data providers
3. Include edge cases and accessibility variations
4. Test on different devices and themes

### Updating Preview Data

1. Keep sample data realistic and diverse
2. Update when adding new features
3. Maintain consistency across all previews
4. Test with actual app data when possible

### Performance Considerations

1. Use `@Preview` sparingly for complex layouts
2. Consider using `@PreviewParameter` for data variations
3. Avoid heavy computations in preview functions
4. Use `remember` for expensive calculations

## Troubleshooting

### Common Issues

1. **Preview not showing**: Check imports and annotations
2. **Data not loading**: Verify preview data providers
3. **Theme issues**: Ensure `KinoTheme` wrapper
4. **Size issues**: Use appropriate device specifications

### Debug Tips

1. Use `@Preview(showBackground = true)` for better visibility
2. Add `@Preview(showSystemUi = true)` for full screen previews
3. Use descriptive names for easier identification
4. Group related previews together

## Future Enhancements

### Planned Features

1. **Animation Previews** - Preview animations and transitions
2. **State Machine Previews** - Complex state transitions
3. **Performance Previews** - Memory and CPU usage testing
4. **Accessibility Previews** - Screen reader and navigation testing

### Integration Ideas

1. **CI/CD Integration** - Automated preview testing
2. **Design System Integration** - Component library previews
3. **Documentation Generation** - Auto-generated preview docs
4. **Visual Regression Testing** - Automated screenshot comparison

---

This preview system ensures that all components and screens are thoroughly tested across different devices, themes, and accessibility settings, providing confidence in the app's quality and user experience.
