package com.jesil.projectmvvm.todolist.ui.tasks

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.jesil.projectmvvm.todolist.data.PreferenceManager
import com.jesil.projectmvvm.todolist.data.Task
import com.jesil.projectmvvm.todolist.data.TaskDao
import com.jesil.projectmvvm.todolist.ui.addedittask.ADD_TASK_RESULT_OK
import com.jesil.projectmvvm.todolist.ui.addedittask.EDIT_TASK_RESULT_OK
import com.jesil.projectmvvm.todolist.util.SortOrder
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class TaskViewModel @ViewModelInject constructor(
        private val taskDao: TaskDao,
        private val preferenceManager: PreferenceManager,
        @Assisted private val state : SavedStateHandle
) : ViewModel() {

    val searchQuery = state.getLiveData("searchQuery", "")

    val preferenceFlow = preferenceManager.preferencesFlow

    private val taskEventChannel = Channel<TasksEvent>()
    val taskEvent = taskEventChannel.receiveAsFlow()

    private val taskFlow = combine(
            searchQuery.asFlow(),
            preferenceFlow
    ){ query, filterPreference ->
//        Triple(query,sortOrder,hideComplete)
          Pair(query, filterPreference)
    }.flatMapLatest {(query, filterPreference) ->
        taskDao.getTask(query, filterPreference.sortOrder, filterPreference.hideComplete)
    }

    val task = taskFlow.asLiveData()

    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferenceManager.updateSortOrder(sortOrder)
    }

    fun onHideCompleteClicked(hideComplete: Boolean) = viewModelScope.launch {
        preferenceManager.updateHideComplete(hideComplete)
    }

    fun onTaskSelected(task: Task) = viewModelScope.launch {
        taskEventChannel.send(TasksEvent.NavigateToEditAddTaskScreen(task))
    }

    fun onTaskChecked(task: Task, isChecked: Boolean) = viewModelScope.launch {
        taskDao.update(task.copy(completed = isChecked))
    }

    fun onTaskSwiped(task: Task) = viewModelScope.launch {
        taskDao.delete(task)
        taskEventChannel.send(TasksEvent.ShowUndoDeleteTaskMessage(task))

    }

    fun onUndoDeleteClick(task: Task)  = viewModelScope.launch {
        taskDao.insert(task)
    }

    fun onAddNewTaskClick() = viewModelScope.launch {
        taskEventChannel.send(TasksEvent.NavigateToAddTaskScreen)
    }

    fun onAddEditResult(result: Int) {
        when(result){
            ADD_TASK_RESULT_OK -> showTaskSavedConfirmationMessage("Task added")
            EDIT_TASK_RESULT_OK -> showTaskSavedConfirmationMessage("Task updated")
        }
    }

    private fun showTaskSavedConfirmationMessage(text: String) = viewModelScope.launch {
        taskEventChannel.send(TasksEvent.ShowTaskSaveConfirmationMessage(text))
    }

    sealed class TasksEvent{
        data class ShowUndoDeleteTaskMessage(val task: Task) : TasksEvent()
        data class NavigateToEditAddTaskScreen(val task: Task) : TasksEvent()
        data class ShowTaskSaveConfirmationMessage(val message: String): TasksEvent()
        object NavigateToAddTaskScreen : TasksEvent()
    }
}