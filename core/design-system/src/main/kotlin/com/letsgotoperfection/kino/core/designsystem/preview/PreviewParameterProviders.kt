package com.letsgotoperfection.kino.core.designsystem.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.letsgotoperfection.kino.core.model.*

/**
 * Provides different task states for previews
 */
class TaskPreviewProvider : PreviewParameterProvider<Task> {
    override val values: Sequence<Task> = sequenceOf(
        PreviewData.sampleTaskHigh,
        PreviewData.sampleTaskMedium,
        PreviewData.sampleTaskLow,
        PreviewData.sampleTaskOverdue,
        PreviewData.sampleTaskCompleted
    )
}

/**
 * Provides different UI states for previews
 */
class UiStatePreviewProvider<T> : PreviewParameterProvider<PreviewData.UiState<T>> {
    override val values: Sequence<PreviewData.UiState<T>> = sequenceOf(
        PreviewData.UiState.Loading,
        PreviewData.UiState.Error("Network connection failed. Please try again.")
        // Success state needs to be provided with actual data
    )
}

/**
 * Provides different kanban data configurations
 */
class KanbanDataPreviewProvider : PreviewParameterProvider<KanbanData> {
    override val values: Sequence<KanbanData> = sequenceOf(
        PreviewData.sampleKanbanData,
        KanbanData(sections = listOf(PreviewData.sampleSectionWork)),
        PreviewData.emptyKanbanData // Empty state
    )
}

/**
 * Provides different note states
 */
class NotePreviewProvider : PreviewParameterProvider<Note> {
    override val values: Sequence<Note> = sequenceOf(
        PreviewData.sampleNotePinned,
        PreviewData.sampleNoteRegular
    )
}

/**
 * Provides different media types
 */
class MediaPreviewProvider : PreviewParameterProvider<MediaFile> {
    override val values: Sequence<MediaFile> = sequenceOf(
        PreviewData.sampleMediaImage,
        PreviewData.sampleMediaPdf,
        PreviewData.sampleMediaVideo
    )
}

/**
 * Provides different priority levels
 */
class PriorityPreviewProvider : PreviewParameterProvider<Priority> {
    override val values: Sequence<Priority> = Priority.values().asSequence()
}

/**
 * Provides different label configurations
 */
class LabelsPreviewProvider : PreviewParameterProvider<List<Label>> {
    override val values: Sequence<List<Label>> = sequenceOf(
        emptyList(),
        listOf(Label("1", "Design", "#FF6B6B")),
        listOf(
            Label("1", "Design", "#FF6B6B"),
            Label("2", "Urgent", "#FFD93D")
        ),
        listOf(
            Label("1", "Design", "#FF6B6B"),
            Label("2", "Urgent", "#FFD93D"),
            Label("3", "Marketing", "#4ECDC4"),
            Label("4", "Review", "#95E1D3")
        )
    )
}

/**
 * Provides different task sections
 */
class TaskSectionPreviewProvider : PreviewParameterProvider<TaskSection> {
    override val values: Sequence<TaskSection> = TaskSection.values().asSequence()
}

/**
 * Provides different task columns
 */
class TaskColumnPreviewProvider : PreviewParameterProvider<TaskColumn> {
    override val values: Sequence<TaskColumn> = TaskColumn.values().asSequence()
}

/**
 * Provides different checklist states
 */
class ChecklistPreviewProvider : PreviewParameterProvider<List<ChecklistItem>> {
    override val values: Sequence<List<ChecklistItem>> = sequenceOf(
        emptyList(),
        PreviewData.sampleChecklist.take(3),
        PreviewData.sampleChecklist
    )
}

/**
 * Provides different attachment counts
 */
class AttachmentCountPreviewProvider : PreviewParameterProvider<Int> {
    override val values: Sequence<Int> = sequenceOf(0, 1, 3, 5, 10)
}

/**
 * Provides different progress values
 */
class ProgressPreviewProvider : PreviewParameterProvider<Int> {
    override val values: Sequence<Int> = sequenceOf(0, 25, 50, 75, 100)
}
