package ru.floppastar.multitask.DataClasses

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SingleTask(
    val taskId: Int,
    val profileId: Int,
    var taskName: String,
    var isCompleted: Int,
    var deadline: String,
    var taskRepeat: Int
) : Parcelable
