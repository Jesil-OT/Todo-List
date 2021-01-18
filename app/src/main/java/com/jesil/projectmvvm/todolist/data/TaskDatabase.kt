package com.jesil.projectmvvm.todolist.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.jesil.projectmvvm.todolist.annotation.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

/**
 * created by @author [Jesil Toborowei]
 * */

/**
 * the [TaskDatabase] class with [@Database]
 * is the class responsible for data storage
 * and class other class like the [Dao] and the [Entity]
 * */

@Database(entities = [Task::class], version = 1)
abstract class TaskDatabase: RoomDatabase() {

    abstract fun taskDao(): TaskDao

    class Callback @Inject constructor(
            private val database: Provider<TaskDatabase>,
            @ApplicationScope private val applicationScope: CoroutineScope
    ) : RoomDatabase.Callback(){

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

           val dao = database.get().taskDao()

            applicationScope.launch {
                dao.insert(Task("Wash the dishes", completed = true))
                dao.insert(Task("Wash the clothes"))
                dao.insert(Task("My work matters a lot", completed = true))
                dao.insert(Task("Android jobs", completed = true, important = true))
                dao.insert(Task("Database using room", important = true))
                dao.insert(Task("My name is jesil",completed = true, important = true))
                dao.insert(Task("Eat the rice and beans", completed = true,  important = true))
            }

        }
    }
}