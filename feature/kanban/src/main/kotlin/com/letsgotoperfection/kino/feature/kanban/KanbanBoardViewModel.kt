package com.letsgotoperfection.kino.feature.kanban

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letsgotoperfection.kino.core.database.dao.TaskDao
import com.letsgotoperfection.kino.core.database.mapper.toDomain
import com.letsgotoperfection.kino.core.model.Task
import com.letsgotoperfection.kino.core.model.TaskColumn
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class KanbanBoardViewModel @Inject constructor(
    private val taskDao: TaskDao
) : ViewModel() {

    val boardState: StateFlow<Map<TaskColumn, List<Task>>> = taskDao.getAllTasks()
        .map { entities ->
            val tasks = entities.map { it.toDomain() }
            TaskColumn.values().associateWith { column ->
                tasks.filter { it.column == column }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TaskColumn.values().associateWith { emptyList<Task>() }
        )

    fun moveTask(taskId: String, targetColumn: TaskColumn) {
        viewModelScope.launch {
            taskDao.updateColumn(taskId, targetColumn.name.lowercase())
        }
    }
}
