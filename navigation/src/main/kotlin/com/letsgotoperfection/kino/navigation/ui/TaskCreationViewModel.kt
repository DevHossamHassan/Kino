package com.letsgotoperfection.kino.navigation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letsgotoperfection.kino.core.database.dao.TaskDao
import com.letsgotoperfection.kino.core.database.mapper.toEntity
import com.letsgotoperfection.kino.core.model.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class TaskCreationViewModel @Inject constructor(
    private val taskDao: TaskDao
) : ViewModel() {

    fun createTask(task: Task, onDone: (String) -> Unit) {
        viewModelScope.launch {
            taskDao.upsertTask(task.toEntity())
            onDone(task.id)
        }
    }
}





