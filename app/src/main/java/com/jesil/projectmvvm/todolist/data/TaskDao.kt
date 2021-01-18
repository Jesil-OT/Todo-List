package com.jesil.projectmvvm.todolist.data

import androidx.room.*
import com.jesil.projectmvvm.todolist.util.SortOrder
import kotlinx.coroutines.flow.Flow

/**
 * created by @author [Jesil Toborowei]
 * */

/**
 * interface class [TaskDao] that has the [@Dao]
 * annotation and other functions for [Insert]
 * [Query], [Update], [Delete]
 * */
@Dao
interface TaskDao {

    fun getTask(query: String, sortOrder: SortOrder, hideCompleted: Boolean): Flow<List<Task>> =
            when(sortOrder){
                SortOrder.BY_DATE -> getTaskSortByDateCreated(query, hideCompleted)
                SortOrder.BY_NAME -> getTaskSortByName(query, hideCompleted)
            }

    @Query("SELECT * FROM task_table WHERE(completed != :hideCompleted OR completed = 0) AND name LIKE '%' || :searchQuery || '%' ORDER BY important DESC, name")
    fun getTaskSortByName(searchQuery: String, hideCompleted: Boolean): Flow<List<Task>>

    @Query("SELECT * FROM task_table WHERE(completed != :hideCompleted OR completed = 0) AND name LIKE '%' || :searchQuery || '%' ORDER BY important DESC, created")
    fun getTaskSortByDateCreated(searchQuery: String, hideCompleted: Boolean): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)
}