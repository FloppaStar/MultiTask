package ru.floppastar.multitask.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RadioGroup
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
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
    private lateinit var etSearch: EditText
    private lateinit var btnSort: ImageButton
    private lateinit var prefs: SharedPreferences
    private var taskList: MutableList<SingleTask> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(R.layout.fragment_single_task_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = requireContext().getSharedPreferences("multitask_prefs", Context.MODE_PRIVATE)
        repository = DatabaseRepository(DatabaseHelper(view.context))
        recyclerView = view.findViewById(R.id.recyclerViewSingleTaskList)
        val allTasks = repository.getAllSingleTasks()
        taskList = applySort(allTasks)
        taskAdapter = SingleTaskAdapter(taskList, repository, { singleTask -> openEditTaskFragment(singleTask)})

        recyclerView.adapter = taskAdapter
        val etSearch = view.findViewById<EditText>(R.id.etSearch)
        val btnSort = view.findViewById<ImageButton>(R.id.btnSort)
        val btAddTask = view.findViewById<FloatingActionButton>(R.id.fabAddTask)
        btAddTask.setOnClickListener {
            openEditTaskFragment(null)
        }

        etSearch.addTextChangedListener {
            val query = it.toString().lowercase()
            val filtered = if (query.isEmpty()) {
                applySort(repository.getAllSingleTasks()) // ИЗМЕНЕНО
            } else {
                applySort(repository.getAllSingleTasks().filter { task ->
                    task.taskName.lowercase().contains(query)
                }) // ИЗМЕНЕНО
            }
            taskAdapter.updateList(filtered)
        }

        btnSort.setOnClickListener {
            showSortDialog()
        }
    }

    private fun showSortDialog() {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.sort_bottom_sheet, null)
        val radioGroup = view.findViewById<RadioGroup>(R.id.sortRadioGroup)
        val selectedSort = prefs.getInt("sort_option", 0)
        radioGroup.check(
            when (selectedSort) {
                0 -> R.id.sortByUpdateTime
                1 -> R.id.sortByNameAsc
                2 -> R.id.sortByNameDesc
                else -> R.id.sortByStatus
            }
        )

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val option = when (checkedId) {
                R.id.sortByUpdateTime -> 0
                R.id.sortByNameAsc -> 1
                R.id.sortByNameDesc -> 2
                R.id.sortByStatus -> 3
                else -> 0
            }
            prefs.edit().putInt("sort_option", option).apply()
            val newList = applySort(repository.getAllSingleTasks())
            taskAdapter.updateList(newList)
            dialog.dismiss()
        }

        dialog.setContentView(view)
        dialog.show()
    }

    private fun applySort(list: List<SingleTask>): MutableList<SingleTask> {
        return when (prefs.getInt("sort_option", 0)) {
            0 -> list.sortedBy { it.lastUpdateTime }
            1 -> list.sortedBy { it.taskName.lowercase() }
            2 -> list.sortedByDescending { it.taskName.lowercase() }
            3 -> list.sortedBy { it.isCompleted }
            else -> list
        }.toMutableList()
    }

    private fun openEditTaskFragment(singleTask: SingleTask?) {
        val bundle = Bundle().apply {
            putParcelable("singleTask", singleTask)
        }
        findNavController().navigate(R.id.action_singleTaskListFragment_to_editTaskFragment, bundle)
    }
}
