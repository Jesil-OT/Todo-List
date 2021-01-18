package com.jesil.projectmvvm.todolist.data

import com.jesil.projectmvvm.todolist.util.SortOrder

data class FilterPreference(
        val sortOrder: SortOrder,
        val hideComplete : Boolean
)