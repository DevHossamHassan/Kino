package com.letsgotoperfection.kino.feature.kanban

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letsgotoperfection.kino.core.database.dao.TaskDao
import com.letsgotoperfection.kino.core.database.mapper.toDomain
import com.letsgotoperfection.kino.core.model.Task
import com.letsgotoperfection.kino.core.model.TaskColumn
import com.letsgotoperfection.kino.core.model.TaskSection
import com.letsgotoperfection.kino.core.model.Priority
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime

/**
 * Filter criteria for tasks with enterprise-grade filtering capabilities.
 */
@Immutable
data class TaskFilterCriteria(
    val searchQuery: String = "",
    val selectedPriorities: Set<Priority> = emptySet(),
    val selectedSections: Set<TaskSection> = emptySet(),
    val showOverdueOnly: Boolean = false,
    val showCompletedTasks: Boolean = true,
    val dueDateRange: DateRange? = null,
    val hasAttachments: Boolean? = null,
    val hasLabels: Boolean? = null
) {
    val isActive: Boolean
        get() = searchQuery.isNotBlank() ||
                selectedPriorities.isNotEmpty() ||
                selectedSections.isNotEmpty() ||
                showOverdueOnly ||
                !showCompletedTasks ||
                dueDateRange != null ||
                hasAttachments != null ||
                hasLabels != null
    
    val activeFilterCount: Int
        get() = listOfNotNull(
            if (searchQuery.isNotBlank()) 1 else null,
            if (selectedPriorities.isNotEmpty()) selectedPriorities.size else null,
            if (selectedSections.isNotEmpty()) selectedSections.size else null,
            if (showOverdueOnly) 1 else null,
            if (!showCompletedTasks) 1 else null,
            if (dueDateRange != null) 1 else null,
            if (hasAttachments != null) 1 else null,
            if (hasLabels != null) 1 else null
        ).sum()
}

@Immutable
data class DateRange(
    val start: LocalDateTime,
    val end: LocalDateTime
)

/**
 * UI state for Kanban board with loading, error, and data states.
 */
sealed interface KanbanUiState {
    data object Loading : KanbanUiState
    data class Success(
        val board: Map<TaskColumn, List<Task>>,
        val totalTasks: Int,
        val filteredTasks: Int,
        val filters: TaskFilterCriteria
    ) : KanbanUiState
    data class Error(val message: String) : KanbanUiState
}

@HiltViewModel
class KanbanBoardViewModel @Inject constructor(
    private val taskDao: TaskDao
) : ViewModel() {

    /**
     * PERFORMANCE: Lazy loading with WhileSubscribed + proper error handling.
     * 
     * UX IMPROVEMENTS:
     * - Loading state shown during initial load
     * - Error state with actionable message
     * - Empty state guidance for users
     */
    // Search and Filter State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val debouncedSearchQuery = _searchQuery
        .debounce(300)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "")
    
    private val _filterCriteria = MutableStateFlow(TaskFilterCriteria())
    val filterCriteria: StateFlow<TaskFilterCriteria> = _filterCriteria.asStateFlow()

    val uiState: StateFlow<KanbanUiState> = combine(
        taskDao.getAllTasks(),
        debouncedSearchQuery,
        _filterCriteria
    ) { entities, search, filters ->
        val tasks = entities.map { it.toDomain() }
        val filteredTasks = applyFilters(tasks, search, filters)
        val board = TaskColumn.values().associateWith { column ->
            filteredTasks.filter { it.column == column }
        }
        KanbanUiState.Success(
            board = board,
            totalTasks = tasks.size,
            filteredTasks = filteredTasks.size,
            filters = filters.copy(searchQuery = search)
        ) as KanbanUiState
    }
        .onStart { emit(KanbanUiState.Loading) }
        .catch { error ->
            emit(KanbanUiState.Error(error.message ?: "Failed to load tasks"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = KanbanUiState.Loading
        )
    
    private fun applyFilters(
        tasks: List<Task>,
        searchQuery: String,
        filters: TaskFilterCriteria
    ): List<Task> {
        return tasks.filter { task ->
            if (searchQuery.isNotBlank()) {
                val query = searchQuery.lowercase()
                if (!task.title.lowercase().contains(query) &&
                    !task.description.lowercase().contains(query)) {
                    return@filter false
                }
            }
            
            if (filters.selectedPriorities.isNotEmpty() &&
                !filters.selectedPriorities.contains(task.priority)) {
                return@filter false
            }
            
            if (filters.selectedSections.isNotEmpty() &&
                !filters.selectedSections.contains(task.section)) {
                return@filter false
            }
            
            if (filters.showOverdueOnly) {
                val dueDate = task.dueDate ?: return@filter false
                if (dueDate >= LocalDateTime.now()) {
                    return@filter false
                }
            }
            
            if (!filters.showCompletedTasks && task.column == TaskColumn.DONE) {
                return@filter false
            }
            
            true
        }
    }
    
    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }
    
    fun togglePriorityFilter(priority: Priority) {
        _filterCriteria.update { current ->
            val newPriorities = if (current.selectedPriorities.contains(priority)) {
                current.selectedPriorities - priority
            } else {
                current.selectedPriorities + priority
            }
            current.copy(selectedPriorities = newPriorities)
        }
    }
    
    fun toggleSectionFilter(section: TaskSection) {
        _filterCriteria.update { current ->
            val newSections = if (current.selectedSections.contains(section)) {
                current.selectedSections - section
            } else {
                current.selectedSections + section
            }
            current.copy(selectedSections = newSections)
        }
    }
    
    fun toggleOverdueFilter() {
        _filterCriteria.update { it.copy(showOverdueOnly = !it.showOverdueOnly) }
    }
    
    fun toggleCompletedTasksFilter() {
        _filterCriteria.update { it.copy(showCompletedTasks = !it.showCompletedTasks) }
    }
    
    fun clearFilters() {
        _searchQuery.value = ""
        _filterCriteria.value = TaskFilterCriteria()
    }
    
    // Legacy support - maintain backward compatibility
    val boardState: StateFlow<Map<TaskColumn, List<Task>>> = uiState
        .map { state ->
            when (state) {
                is KanbanUiState.Success -> state.board
                else -> TaskColumn.values().associateWith { emptyList<Task>() }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TaskColumn.values().associateWith { emptyList<Task>() }
        )

    fun moveTask(taskId: String, targetColumn: TaskColumn) {
        viewModelScope.launch {
            try {
                val maxOrder = taskDao.getMaxOrderPosition(targetColumn.name.lowercase()) ?: 0
                taskDao.updateColumnAndOrder(taskId, targetColumn.name.lowercase(), maxOrder + 1)
            } catch (e: Exception) {
                // Error handling - could emit to error state if needed
                android.util.Log.e("KanbanViewModel", "Failed to move task", e)
            }
        }
    }
    
    /**
     * Move a task to a different column at a specific position.
     * 
     * @param taskId The task being moved
     * @param targetColumn The target column
     * @param targetPosition The position (0-based index) in the target column
     */
    fun moveTaskToPosition(taskId: String, targetColumn: TaskColumn, targetPosition: Int) {
        viewModelScope.launch {
            try {
                val currentState = uiState.value
                if (currentState is KanbanUiState.Success) {
                    // Get tasks in target column
                    val targetTasks = currentState.board[targetColumn].orEmpty().toMutableList()
                    
                    // Update the moved task's column and position
                    taskDao.updateColumnAndOrder(taskId, targetColumn.name.lowercase(), targetPosition)
                    
                    // Reorder existing tasks in target column to make space
                    targetTasks.forEachIndexed { index, task ->
                        if (task.id != taskId) {
                            val newPosition = if (index >= targetPosition) index + 1 else index
                            taskDao.updateOrderPosition(task.id, newPosition)
                        }
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("KanbanViewModel", "Failed to move task to position", e)
            }
        }
    }
    
    /**
     * Reorder a task within the same column.
     * 
     * @param taskId The task being moved
     * @param targetPosition The new position (0-based index)
     */
    fun reorderTask(taskId: String, targetPosition: Int) {
        viewModelScope.launch {
            try {
                val currentState = uiState.value
                if (currentState is KanbanUiState.Success) {
                    // Find the column containing the task
                    val columnEntry = currentState.board.entries.firstOrNull { (_, tasks) ->
                        tasks.any { it.id == taskId }
                    }
                    
                    if (columnEntry != null) {
                        val (column, tasks) = columnEntry
                        val draggedTask = tasks.first { it.id == taskId }
                        val currentIndex = tasks.indexOf(draggedTask)
                        
                        // Calculate new order positions
                        val reorderedTasks = tasks.toMutableList()
                        reorderedTasks.removeAt(currentIndex)
                        val safeTargetPosition = targetPosition.coerceIn(0, reorderedTasks.size)
                        reorderedTasks.add(safeTargetPosition, draggedTask)
                        
                        // Update all tasks in this column with new positions
                        reorderedTasks.forEachIndexed { index, task ->
                            taskDao.updateOrderPosition(task.id, index)
                        }
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("KanbanViewModel", "Failed to reorder task", e)
            }
        }
    }
}
