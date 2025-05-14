package ru.floppastar.multitask.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ru.floppastar.multitask.Adapters.SingleTaskAdapter
import ru.floppastar.multitask.DataClasses.SingleTask
import ru.floppastar.multitask.R
import ru.floppastar.multitask.db.DatabaseHelper
import ru.floppastar.multitask.db.DatabaseRepository

class SingleTaskListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var repository: DatabaseRepository
    private lateinit var taskAdapter: SingleTaskAdapter
    private var taskList: MutableList<SingleTask> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(R.layout.fragment_single_task_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repository = DatabaseRepository(DatabaseHelper(view.context))
        recyclerView = view.findViewById(R.id.recyclerViewSingleTaskList)
        taskList = repository.getAllSingleTasks()
        taskAdapter = SingleTaskAdapter(taskList, repository, { singleTask -> openEditTaskFragment(singleTask)})

        recyclerView.adapter = taskAdapter

        val btAddTask = view.findViewById<FloatingActionButton>(R.id.fabAddTask)
        btAddTask.setOnClickListener {
            openEditTaskFragment(null)
        }
    }

    private fun openEditTaskFragment(singleTask: SingleTask?) {
        val bundle = Bundle().apply {
            putParcelable("singleTask", singleTask)
        }
        findNavController().navigate(R.id.action_singleTaskListFragment_to_editTaskFragment, bundle)
    }
}
