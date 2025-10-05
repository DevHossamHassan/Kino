package com.letsgotoperfection.kino.feature.kanban

/**
 * Navigation destinations for Kanban feature
 */
object KanbanDestinations {
    const val KANBAN_BOARD = "kanban_board"
    const val TASK_DETAIL = "task_detail/{taskId}"
    
    fun taskDetailRoute(taskId: String) = "task_detail/$taskId"
}
