package ru.floppastar.multitask.db

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.provider.ContactsContract.Data
import android.util.Log
import ru.floppastar.multitask.DataClasses.Profile
import ru.floppastar.multitask.DataClasses.SingleTask
import ru.floppastar.multitask.utils.PrefsManager
import java.util.Calendar

class DatabaseRepository(private val dbHelper: DatabaseHelper) {
    fun getAllSingleTasks(): MutableList<SingleTask> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_SINGLE_TASK} " +
                "WHERE ${DatabaseHelper.COLUMN_SINGLE_TASK_PROFILE_ID} = ${PrefsManager.getProfileId()}", null)
        val singleTask = mutableListOf<SingleTask>()
        with(cursor) {
            while (moveToNext()) {
                val taskId = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_SINGLE_TASK_ID))
                val profileId = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_SINGLE_TASK_PROFILE_ID))
                val taskName = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_SINGLE_TASK_NAME))
                val isCompleted = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_SINGLE_TASK_IS_COMPLETED))
                val deadline = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_SINGLE_TASK_DEADLINE))
                val taskRepeat = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_SINGLE_TASK_REPEAT))
                val lastUpdateTime = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_SINGLE_TASK_LAST_UPDATE))
                singleTask.add(SingleTask(taskId, profileId, taskName, isCompleted, deadline, taskRepeat, lastUpdateTime))
            }
        }
        cursor.close()
        db.close()
        return singleTask
    }
    fun insertSingleTask(name: String, profileId: Int, isCompleted: Int, deadline: String, taskRepeat: Int, lastUpdate: String) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_SINGLE_TASK_NAME, name)
            put(DatabaseHelper.COLUMN_SINGLE_TASK_PROFILE_ID, profileId)
            put(DatabaseHelper.COLUMN_SINGLE_TASK_IS_COMPLETED, isCompleted)
            put(DatabaseHelper.COLUMN_SINGLE_TASK_DEADLINE, deadline)
            put(DatabaseHelper.COLUMN_SINGLE_TASK_REPEAT, taskRepeat)
            put(DatabaseHelper.COLUMN_SINGLE_TASK_LAST_UPDATE, lastUpdate)
        }
        db.insert(DatabaseHelper.TABLE_SINGLE_TASK, null, values)
        db.close()
    }
    fun editSingleTask(singleTask: SingleTask) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_SINGLE_TASK_NAME, singleTask.taskName)
            put(DatabaseHelper.COLUMN_SINGLE_TASK_PROFILE_ID, singleTask.profileId)
            put(DatabaseHelper.COLUMN_SINGLE_TASK_IS_COMPLETED, singleTask.isCompleted)
            put(DatabaseHelper.COLUMN_SINGLE_TASK_DEADLINE, singleTask.deadline)
            put(DatabaseHelper.COLUMN_SINGLE_TASK_REPEAT, singleTask.taskRepeat)
        }
        db.update(
            DatabaseHelper.TABLE_SINGLE_TASK,
            values,
            "${DatabaseHelper.COLUMN_SINGLE_TASK_ID} = ${singleTask.taskId}",
            null
        )
        db.close()
    }

    fun deleteSingleTask(taskId: Int) {
        val db = dbHelper.writableDatabase
        db.delete(
            DatabaseHelper.TABLE_SINGLE_TASK,
            "${DatabaseHelper.COLUMN_SINGLE_TASK_ID} = $taskId", null
        )
        db.close()
    }

    fun updateTaskCompletion(taskId: Int, isCompleted: Int) {
        val db: SQLiteDatabase = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_SINGLE_TASK_IS_COMPLETED, isCompleted)
        }
        val selection = "${DatabaseHelper.COLUMN_SINGLE_TASK_ID} LIKE ?"
        val selectionArgs = arrayOf(taskId.toString())
        db.update(DatabaseHelper.TABLE_SINGLE_TASK, values, selection, selectionArgs)
    }

    fun resetDailyTasks() {
        val db = dbHelper.writableDatabase
        db.execSQL(
            "UPDATE ${DatabaseHelper.TABLE_SINGLE_TASK} " +
                    "SET ${DatabaseHelper.COLUMN_SINGLE_TASK_IS_COMPLETED} = 0 " +
                    "WHERE ${DatabaseHelper.COLUMN_SINGLE_TASK_REPEAT} = 1"
        )
    }

    fun resetWeeklyTasks() {
        val db = dbHelper.writableDatabase
        db.execSQL(
            "UPDATE ${DatabaseHelper.TABLE_SINGLE_TASK} " +
                    "SET ${DatabaseHelper.COLUMN_SINGLE_TASK_IS_COMPLETED} = 0 " +
                    "WHERE ${DatabaseHelper.COLUMN_SINGLE_TASK_REPEAT} = 2"
        )
    }

    fun resetMonthlyTasks() {
        val db = dbHelper.writableDatabase
        db.execSQL(
            "UPDATE ${DatabaseHelper.TABLE_SINGLE_TASK} " +
                    "SET ${DatabaseHelper.COLUMN_SINGLE_TASK_IS_COMPLETED} = 0 " +
                    "WHERE ${DatabaseHelper.COLUMN_SINGLE_TASK_REPEAT} = 3"
        )
    }

    fun getAllProfiles(): MutableList<Profile> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_PROFILE}", null)
        val profile = mutableListOf<Profile>()
        with(cursor) {
            while (moveToNext()) {
                val profileId = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROFILE_ID))
                val profileName = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROFILE_NAME))
                val profileType = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROFILE_TYPE))
                profile.add(Profile(profileId, profileName, profileType))
            }
        }
        cursor.close()
        db.close()
        return profile
    }

    fun insertProfile(name: String, type: String): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_PROFILE_NAME, name)
            put(DatabaseHelper.COLUMN_PROFILE_TYPE, type)
        }
        val rowId = db.insert(DatabaseHelper.TABLE_PROFILE, null, values)
        return rowId.toInt()
    }

    fun editProfile(profile: Profile) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_PROFILE_NAME, profile.profileName)
            put(DatabaseHelper.COLUMN_PROFILE_TYPE, profile.profileType)
        }
        db.update(
            DatabaseHelper.TABLE_PROFILE,
            values,
            "${DatabaseHelper.COLUMN_PROFILE_ID} = ${profile.profileId}",
            null
        )
        db.close()
    }

    fun deleteProfile(profileId: Int) {
        val db = dbHelper.writableDatabase
        db.delete(
            DatabaseHelper.TABLE_PROFILE,
            "${DatabaseHelper.COLUMN_PROFILE_ID} = $profileId", null
        )
        db.close()
    }

    fun getProfileName(id: Int): String {
        val db = dbHelper.readableDatabase
        var profileName = ""
        val cursor = db.rawQuery("SELECT ${DatabaseHelper.COLUMN_PROFILE_NAME} FROM ${DatabaseHelper.TABLE_PROFILE} " +
                "WHERE ${DatabaseHelper.COLUMN_PROFILE_ID} = $id", null)
        with(cursor) {
            while (moveToNext()) {
                profileName = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROFILE_NAME))
            }
        }
        cursor.close()
        db.close()
        return profileName
    }
}