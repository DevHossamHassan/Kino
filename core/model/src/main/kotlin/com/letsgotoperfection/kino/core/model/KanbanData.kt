package com.letsgotoperfection.kino.core.model
data class KanbanData(
    val sections: List<TaskSectionData>
)

data class TaskSectionData(
    val id: String,
    val name: String,
    val totalTasks: Int,
    val isExpanded: Boolean = true,
    val columns: List<KanbanColumn>
)

data class KanbanColumn(
    val id: String,
    val title: String,
    val tasks: List<Task>
)

data class TaskCreationRequest(
    val title: String,
    val description: String,
    val section: TaskSection,
    val column: TaskColumn,
    val priority: Priority,
    val dueDate: java.time.LocalDateTime?
)

data class TaskFilters(
    val sections: Set<TaskSection> = emptySet(),
    val priorities: Set<Priority> = emptySet(),
    val columns: Set<TaskColumn> = emptySet(),
    val labels: Set<String> = emptySet(),
    val hasDueDate: Boolean? = null,
    val isOverdue: Boolean? = null
)
