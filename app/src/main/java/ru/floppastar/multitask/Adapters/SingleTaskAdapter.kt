package ru.floppastar.multitask.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.floppastar.multitask.DataClasses.SingleTask
import ru.floppastar.multitask.R
import ru.floppastar.multitask.db.DatabaseRepository

class SingleTaskAdapter(
    var singleTaskList: MutableList<SingleTask>,
    private val repository: DatabaseRepository,
    private val itemClickListener: (SingleTask) -> Unit)
    : RecyclerView.Adapter<SingleTaskAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTaskName = itemView.findViewById<TextView>(R.id.textViewTaskName)
        val tvDeadline = itemView.findViewById<TextView>(R.id.textViewDeadline)
        val btCheckBox = itemView.findViewById<CheckBox>(R.id.checkBoxCompleted)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_single_task, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return singleTaskList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var singleTask = singleTaskList[position]
        holder.tvTaskName.text = singleTask.taskName
        val deadlineText = when {
            !singleTask.deadline.isNullOrEmpty() -> "Срок выполнения: до ${singleTask.deadline}"
            singleTask.taskRepeat != 0 -> "Повтор задания ${getRepeatText(singleTask.taskRepeat)}"
            else -> "Срок выполнения не указан"
        }
        holder.tvDeadline.text = deadlineText
        holder.btCheckBox.setOnCheckedChangeListener(null)
        holder.btCheckBox.isChecked = singleTask.isCompleted == 1
        holder.btCheckBox.setOnCheckedChangeListener { _, isChecked ->
            val newIsCompleted = if (isChecked) 1 else 0
            repository.updateTaskCompletion(singleTask.taskId, newIsCompleted)
            singleTask.isCompleted = newIsCompleted
        }
        holder.itemView.setOnClickListener {
            itemClickListener(singleTask)
        }
    }
    private fun getRepeatText(repeat: Int): String {
        return when (repeat) {
            1 -> "ежедневно"
            2 -> "еженедельно"
            3 -> "ежемесячно"
            else -> "не указано"
        }
    }
}