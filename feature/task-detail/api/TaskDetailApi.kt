package com.letsgotoperfection.kino.feature.taskdetail.api

import androidx.navigation.NavController
import com.letsgotoperfection.kino.core.common.Result
import com.letsgotoperfection.kino.feature.taskdetail.internal.domain.model.TaskDetail

/**
 * Public API for Task Detail feature.
 * This allows other feature modules to interact with task detail functionality.
 */
interface TaskDetailApi {
    
    /**
     * Get task detail by ID.
     * 
     * @param taskId The unique task identifier
     * @return Result containing the TaskDetail or an error
     */
    suspend fun getTaskDetail(taskId: String): Result<TaskDetail>
    
    /**
     * Navigate to task detail screen.
     * 
     * @param navController Navigation controller
     * @param taskId The unique task identifier
     */
    fun navigateToTaskDetail(navController: NavController, taskId: String)
    
    /**
     * Navigate back from task detail screen.
     * 
     * @param navController Navigation controller
     */
    fun navigateBack(navController: NavController)
}

/**
 * Navigation destinations for Task Detail feature.
 */
object TaskDetailDestinations {
    const val TASK_DETAIL = "task_detail/{taskId}"
    
    fun taskDetailRoute(taskId: String) = "task_detail/$taskId"
}
