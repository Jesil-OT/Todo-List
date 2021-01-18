package com.jesil.projectmvvm.todolist.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.text.DateFormat

/**
 * created by @author [Jesil Toborowei]
 * */

/**
 * taskTable [@Entity] class for storing
 * the all about the Task and also [@Parcelize],
 * [@primaryKey] is auto generated
 * */

@Entity(tableName = "task_table")
@Parcelize
data class Task(
     val name: String,
     val important : Boolean = false,
     val completed: Boolean = false,
     val created : Long = System.currentTimeMillis(),

     @PrimaryKey(autoGenerate = true)
     val id: Int = 0

) : Parcelable {

    val createdDataFormatted : String
        get() = DateFormat.getDateInstance().format(created)

}