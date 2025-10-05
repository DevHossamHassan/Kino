package com.letsgotoperfection.kino.core.designsystem.preview

import androidx.compose.ui.text.buildAnnotatedString
import com.letsgotoperfection.kino.core.model.*
import java.time.LocalDateTime

object PreviewData {
    
    // Task Preview Data
    val sampleTaskHigh = Task(
        id = "task-1",
        title = "Design new landing page for product launch",
        description = "Create high-fidelity mockups and interactive prototypes for the upcoming product launch landing page. Include mobile and desktop versions.",
        section = TaskSection.WORK,
        column = TaskColumn.IN_PROGRESS,
        priority = Priority.HIGH,
        progress = 65,
        labels = listOf(
            Label("1", "Design", "#FF6B6B"),
            Label("2", "Urgent", "#FFD93D"),
            Label("3", "Marketing", "#4ECDC4")
        ),
        attachments = listOf(
            Attachment("1", "task-1", "task", "file://attachment1.jpg", "mockup1.jpg", "image/jpeg", 1024, LocalDateTime.now()),
            Attachment("2", "task-1", "task", "file://attachment2.pdf", "specs.pdf", "application/pdf", 2048, LocalDateTime.now()),
            Attachment("3", "task-1", "task", "file://attachment3.png", "icon.png", "image/png", 512, LocalDateTime.now())
        ),
        checklist = listOf(
            ChecklistItem("1", "task-1", "Create wireframes", true, 1, LocalDateTime.now().minusDays(3)),
            ChecklistItem("2", "task-1", "Design desktop version", true, 2, LocalDateTime.now().minusDays(2)),
            ChecklistItem("3", "task-1", "Design mobile version", true, 3, LocalDateTime.now().minusDays(1)),
            ChecklistItem("4", "task-1", "Create interactive prototype", true, 4, LocalDateTime.now().minusHours(12)),
            ChecklistItem("5", "task-1", "Review with stakeholders", false, 5, LocalDateTime.now().minusHours(6)),
            ChecklistItem("6", "task-1", "Make revisions", false, 6, LocalDateTime.now().minusHours(3)),
            ChecklistItem("7", "task-1", "Final approval", false, 7, LocalDateTime.now().minusHours(1))
        ),
        dueDate = LocalDateTime.now().plusDays(1),
        createdAt = LocalDateTime.now().minusDays(3),
        updatedAt = LocalDateTime.now().minusHours(2)
    )
    
    val sampleTaskMedium = Task(
        id = "task-2",
        title = "Review pull requests",
        description = "Check code quality and approve pending PRs",
        section = TaskSection.WORK,
        column = TaskColumn.TODO_THIS_WEEK,
        priority = Priority.MEDIUM,
        progress = 0,
        labels = listOf(Label("4", "Code Review", "#95E1D3")),
        attachments = emptyList(),
        checklist = emptyList(),
        dueDate = LocalDateTime.now().plusDays(5),
        createdAt = LocalDateTime.now().minusDays(1),
        updatedAt = LocalDateTime.now().minusDays(1)
    )
    
    val sampleTaskLow = Task(
        id = "task-3",
        title = "Buy groceries",
        description = "Milk, eggs, bread, vegetables, fruits",
        section = TaskSection.PERSONAL,
        column = TaskColumn.TODO_THIS_WEEK,
        priority = Priority.LOW,
        progress = 0,
        labels = emptyList(),
        attachments = emptyList(),
        checklist = emptyList(),
        dueDate = LocalDateTime.now().plusDays(2),
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )
    
    val sampleTaskOverdue = Task(
        id = "task-4",
        title = "Submit expense report",
        description = "Complete and submit Q4 expense report",
        section = TaskSection.WORK,
        column = TaskColumn.PENDING,
        priority = Priority.HIGH,
        progress = 80,
        labels = listOf(Label("5", "Finance", "#F38181")),
        attachments = listOf(
            Attachment("4", "task-4", "task", "file://receipt1.pdf", "receipt1.pdf", "application/pdf", 1024, LocalDateTime.now()),
            Attachment("5", "task-4", "task", "file://receipt2.pdf", "receipt2.pdf", "application/pdf", 1024, LocalDateTime.now()),
            Attachment("6", "task-4", "task", "file://receipt3.pdf", "receipt3.pdf", "application/pdf", 1024, LocalDateTime.now()),
            Attachment("7", "task-4", "task", "file://receipt4.pdf", "receipt4.pdf", "application/pdf", 1024, LocalDateTime.now()),
            Attachment("8", "task-4", "task", "file://receipt5.pdf", "receipt5.pdf", "application/pdf", 1024, LocalDateTime.now())
        ),
        checklist = listOf(
            ChecklistItem("8", "task-4", "Collect receipts", true, 1, LocalDateTime.now().minusDays(2)),
            ChecklistItem("9", "task-4", "Categorize expenses", true, 2, LocalDateTime.now().minusDays(1)),
            ChecklistItem("10", "task-4", "Calculate totals", true, 3, LocalDateTime.now().minusHours(12)),
            ChecklistItem("11", "task-4", "Review with manager", true, 4, LocalDateTime.now().minusHours(6)),
            ChecklistItem("12", "task-4", "Submit to finance", false, 5, LocalDateTime.now().minusHours(2))
        ),
        dueDate = LocalDateTime.now().minusDays(2),
        createdAt = LocalDateTime.now().minusDays(10),
        updatedAt = LocalDateTime.now().minusHours(5)
    )
    
    val sampleTaskCompleted = Task(
        id = "task-5",
        title = "Update documentation",
        description = "Add API documentation for new endpoints",
        section = TaskSection.WORK,
        column = TaskColumn.DONE,
        priority = Priority.MEDIUM,
        progress = 100,
        labels = listOf(
            Label("6", "Documentation", "#A8E6CF"),
            Label("7", "Backend", "#FFD3B6")
        ),
        attachments = listOf(
            Attachment("9", "task-5", "task", "file://api-docs.pdf", "api-docs.pdf", "application/pdf", 2048, LocalDateTime.now()),
            Attachment("10", "task-5", "task", "file://examples.md", "examples.md", "text/markdown", 512, LocalDateTime.now())
        ),
        checklist = listOf(
            ChecklistItem("13", "task-5", "Write endpoint descriptions", true, 1, LocalDateTime.now().minusDays(1)),
            ChecklistItem("14", "task-5", "Add request/response examples", true, 2, LocalDateTime.now().minusHours(12)),
            ChecklistItem("15", "task-5", "Review and publish", true, 3, LocalDateTime.now().minusHours(6))
        ),
        dueDate = LocalDateTime.now().minusDays(1),
        createdAt = LocalDateTime.now().minusDays(7),
        updatedAt = LocalDateTime.now().minusDays(1)
    )
    
    // Kanban Section Data
    val sampleSectionWork = TaskSectionData(
        id = "work",
        name = "Work",
        isExpanded = true,
        columns = TaskColumn.values().map { column ->
            KanbanColumn(
                id = column.name,
                title = column.displayName,
                tasks = when (column) {
                    TaskColumn.TODO_THIS_WEEK -> listOf(sampleTaskMedium)
                    TaskColumn.IN_PROGRESS -> listOf(sampleTaskHigh)
                    TaskColumn.PENDING -> listOf(sampleTaskOverdue)
                    TaskColumn.DONE -> listOf(sampleTaskCompleted)
                    else -> emptyList()
                }
            )
        },
        totalTasks = 12
    )
    
    val sampleSectionPersonal = TaskSectionData(
        id = "personal",
        name = "Personal",
        isExpanded = false,
        columns = TaskColumn.values().map { column ->
            KanbanColumn(
                id = column.name,
                title = column.displayName,
                tasks = if (column == TaskColumn.TODO_THIS_WEEK) {
                    listOf(sampleTaskLow)
                } else emptyList()
            )
        },
        totalTasks = 5
    )
    
    val sampleKanbanData = KanbanData(
        sections = listOf(sampleSectionWork, sampleSectionPersonal)
    )
    
    // Note Preview Data
    val sampleNotePinned = Note(
        id = "note-1",
        title = "Meeting Notes - Q1 Planning",
        content = buildAnnotatedString {
            append("# Q1 Planning Meeting\n\n")
            append("## Attendees\n")
            append("- John Doe\n")
            append("- Jane Smith\n\n")
            append("## Action Items\n")
            append("☐ Review budget proposal\n")
            append("☑ Schedule follow-up\n")
        }.toString(),
        isPinned = true,
        labels = listOf(
            Label("8", "Meetings", "#FFB6C1"),
            Label("9", "Important", "#FF6B6B")
        ),
        attachments = listOf(
            Attachment("11", "note-1", "note", "file://meeting-notes.pdf", "meeting-notes.pdf", "application/pdf", 1024, LocalDateTime.now()),
            Attachment("12", "note-1", "note", "file://agenda.docx", "agenda.docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", 2048, LocalDateTime.now())
        ),
        createdAt = LocalDateTime.now().minusDays(2),
        updatedAt = LocalDateTime.now().minusHours(3)
    )
    
    val sampleNoteRegular = Note(
        id = "note-2",
        title = "Project Ideas",
        content = buildAnnotatedString {
            append("Random ideas for new projects:\n\n")
            append("1. Mobile app for task management\n")
            append("2. Web dashboard for analytics\n")
            append("3. Chrome extension for productivity\n")
        }.toString(),
        isPinned = false,
        labels = listOf(Label("10", "Ideas", "#DDA15E")),
        attachments = emptyList(),
        createdAt = LocalDateTime.now().minusDays(5),
        updatedAt = LocalDateTime.now().minusDays(5)
    )
    
    // Media Preview Data
    val sampleMediaImage = MediaFile(
        id = "media-1",
        filename = "design_mockup.png",
        uri = "content://media/image/123",
        mimeType = "image/png",
        size = 2_457_600, // 2.4 MB
        targetId = "task-1",
        targetType = "task",
        addedAt = LocalDateTime.now().minusDays(1)
    )
    
    val sampleMediaPdf = MediaFile(
        id = "media-2",
        filename = "expense_report_Q4.pdf",
        uri = "content://media/document/456",
        mimeType = "application/pdf",
        size = 1_048_576, // 1 MB
        targetId = "task-4",
        targetType = "task",
        addedAt = LocalDateTime.now().minusDays(3)
    )
    
    val sampleMediaVideo = MediaFile(
        id = "media-3",
        filename = "demo_recording.mp4",
        uri = "content://media/video/789",
        mimeType = "video/mp4",
        size = 15_728_640, // 15 MB
        targetId = "note-1",
        targetType = "note",
        addedAt = LocalDateTime.now().minusHours(6)
    )
    
    // Checklist Preview Data
    val sampleChecklist = listOf(
        ChecklistItem("1", "task-1", "Research competitors", true, 0, LocalDateTime.now().minusDays(5)),
        ChecklistItem("2", "task-1", "Create wireframes", true, 1, LocalDateTime.now().minusDays(4)),
        ChecklistItem("3", "task-1", "Design high-fidelity mockups", true, 2, LocalDateTime.now().minusDays(3)),
        ChecklistItem("4", "task-1", "Get feedback from team", true, 3, LocalDateTime.now().minusDays(2)),
        ChecklistItem("5", "task-1", "Create interactive prototype", false, 4, LocalDateTime.now().minusDays(1)),
        ChecklistItem("6", "task-1", "Prepare presentation", false, 5, LocalDateTime.now().minusHours(12)),
        ChecklistItem("7", "task-1", "Present to stakeholders", false, 6, LocalDateTime.now().minusHours(6))
    )
    
    // UI State Preview Data
    sealed class UiState<out T> {
        data object Loading : UiState<Nothing>()
        data class Success<T>(val data: T) : UiState<T>()
        data class Error(val message: String) : UiState<Nothing>()
    }
    
    // Empty states
    val emptyKanbanData = KanbanData(sections = emptyList())
    val emptyNotesList = emptyList<Note>()
    val emptyMediaList = emptyList<MediaFile>()
    val emptyTaskList = emptyList<Task>()
}
