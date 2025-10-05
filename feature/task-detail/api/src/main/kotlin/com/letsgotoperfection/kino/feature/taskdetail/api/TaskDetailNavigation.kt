package com.letsgotoperfection.kino.feature.taskdetail.api

/**
 * Navigation destinations for Task Detail feature
 */
object TaskDetailDestinations {
    const val TASK_DETAIL = "task_detail/{taskId}"
    
    fun taskDetailRoute(taskId: String) = "task_detail/$taskId"
}

