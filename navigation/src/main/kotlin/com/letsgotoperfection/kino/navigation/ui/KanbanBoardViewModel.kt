package com.letsgotoperfection.kino.navigation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letsgotoperfection.kino.core.database.dao.TaskDao
import com.letsgotoperfection.kino.core.database.dao.SectionDao
import com.letsgotoperfection.kino.core.database.entity.SectionEntity
import com.letsgotoperfection.kino.core.database.mapper.toDomain
import com.letsgotoperfection.kino.core.model.Task
import com.letsgotoperfection.kino.core.model.TaskSection
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class KanbanBoardViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val sectionDao: SectionDao
) : ViewModel() {

    val uiState: StateFlow<KanbanUiState> = combine(
        taskDao.getTasksBySection(TaskSection.PERSONAL.name.lowercase()).map { it.map { e -> e.toDomain() } },
        taskDao.getTasksBySection(TaskSection.WORK.name.lowercase()).map { it.map { e -> e.toDomain() } },
        taskDao.getTasksBySection(TaskSection.FAMILY.name.lowercase()).map { it.map { e -> e.toDomain() } }
    ) { personal, work, family ->
        KanbanUiState(
            personalTasks = personal,
            workTasks = work,
            familyTasks = family
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = KanbanUiState()
    )

    val sections: StateFlow<List<SectionEntity>> = sectionDao.getSections()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Seed default sections if none exist, using stable IDs to match existing task.section values
        viewModelScope.launch {
            sections.collect { current ->
                if (current.isEmpty()) {
                    sectionDao.upsert(SectionEntity(id = "personal", name = "Personal", orderIndex = 0))
                    sectionDao.upsert(SectionEntity(id = "work", name = "Work", orderIndex = 1))
                    sectionDao.upsert(SectionEntity(id = "family", name = "Family", orderIndex = 2))
                }
            }
        }
    }

    data class SectionWithTasks(
        val section: SectionEntity,
        val tasks: List<Task>
    )

    val board: StateFlow<List<SectionWithTasks>> = sections.flatMapLatest { list ->
        if (list.isEmpty()) {
            // Emit empty immediately until seeding completes
            kotlinx.coroutines.flow.flowOf(emptyList())
        } else {
            combine(
                list.map { section ->
                    taskDao.getTasksBySection(section.id)
                        .map { entities -> entities.map { e -> e.toDomain() } }
                        .map { tasks -> SectionWithTasks(section, tasks) }
                }
            ) { sectionWithTasksArray -> sectionWithTasksArray.toList() }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun addSection(name: String) {
        viewModelScope.launch {
            // orderIndex = append to end
            val nextOrder = sections.value.size
            // use a slugified id to be human-readable
            val id = name.trim().lowercase().replace("\n", " ").replace(" ", "_")
            sectionDao.upsert(SectionEntity(id = id, name = name.trim(), orderIndex = nextOrder))
        }
    }

    fun renameSection(id: String, newName: String) {
        viewModelScope.launch {
            sectionDao.rename(id, newName)
            // Optional: if mapping tasks to renamed section strings is desired, call taskDao.renameSection(old, new)
        }
    }

    fun deleteSection(section: SectionEntity) {
        viewModelScope.launch {
            sectionDao.delete(section)
        }
    }
}

data class KanbanUiState(
    val personalTasks: List<Task> = emptyList(),
    val workTasks: List<Task> = emptyList(),
    val familyTasks: List<Task> = emptyList()
)




