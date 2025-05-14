package ru.floppastar.multitask.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context)
    : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object
    {
        private const val DATABASE_NAME = "multitask.db"
        private const val DATABASE_VERSION = 1

        //Таблица одиночных задач
        const val TABLE_SINGLE_TASK = "SingleTask"
        const val COLUMN_SINGLE_TASK_ID = "TaskId"
        const val COLUMN_SINGLE_TASK_PROFILE_ID = "ProfileId"
        const val COLUMN_SINGLE_TASK_NAME = "TaskName"
        const val COLUMN_SINGLE_TASK_IS_COMPLETED = "IsCompleted" // 0 - не выполнено, 1 - выполнено
        const val COLUMN_SINGLE_TASK_DEADLINE = "Deadline" // дата и время в формате "dd.MM.yyyy HH:mm"
        const val COLUMN_SINGLE_TASK_REPEAT = "TaskRepeat" // 0 - не повторяется, 1 - повторяется каждый день, 2 - повторяется каждую неделю, 3 - повторяется каждый месяц

        //Таблица профилей
        const val TABLE_PROFILE = "Profile"
        const val COLUMN_PROFILE_ID = "ProfileId"
        const val COLUMN_PROFILE_NAME = "ProfileName"
        const val COLUMN_PROFILE_TYPE = "ProfileType"
        const val COLUMN_PROFILE_PASSWORD = "ProfilePassword"
    }
    override fun onCreate(db: SQLiteDatabase) {
        val createTableSingleTask = ("CREATE TABLE IF NOT EXISTS $TABLE_SINGLE_TASK ( " +
                "$COLUMN_SINGLE_TASK_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_SINGLE_TASK_PROFILE_ID INTEGER, " +
                "$COLUMN_SINGLE_TASK_NAME TEXT, " +
                "$COLUMN_SINGLE_TASK_IS_COMPLETED INTEGER NOT NULL DEFAULT 0, " + // 0 - не выполнено, 1 - выполнено
                "$COLUMN_SINGLE_TASK_DEADLINE TEXT ," + // дата и время в формате "dd.MM.yyyy HH:mm"
                "$COLUMN_SINGLE_TASK_REPEAT INTEGER NOT NULL DEFAULT 0, " + // 0 - не повторяется, 1 - повторяется каждый день, 2 - повторяется каждую неделю, 3 - повторяется каждый месяц
                "FOREIGN KEY ($COLUMN_SINGLE_TASK_PROFILE_ID) REFERENCES $TABLE_PROFILE ($COLUMN_PROFILE_ID) ON DELETE CASCADE)")
        db.execSQL(createTableSingleTask)
        val createTableProfile = ("CREATE TABLE IF NOT EXISTS $TABLE_PROFILE ( " +
                "$COLUMN_PROFILE_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_PROFILE_NAME TEXT NOT NULL, " +
                "$COLUMN_PROFILE_TYPE TEXT, " +
                "$COLUMN_PROFILE_PASSWORD TEXT)")
        db.execSQL(createTableProfile)
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SINGLE_TASK")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PROFILE")
        onCreate(db)
    }
}