package ru.floppastar.multitask.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ru.floppastar.multitask.R
import ru.floppastar.multitask.DataClasses.SingleTask
import ru.floppastar.multitask.db.DatabaseHelper
import ru.floppastar.multitask.db.DatabaseRepository
import ru.floppastar.multitask.utils.PrefsManager
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditSingleTaskFragment : Fragment() {
    private val args: EditSingleTaskFragmentArgs by navArgs()
    private lateinit var etTaskName: EditText
    private lateinit var btSelectDateTime: Button
    private lateinit var btClearDateTime: Button
    private lateinit var tvSelectedDateTime: TextView
    private lateinit var rgRepeat: RadioGroup
    private lateinit var rbRepeatNone: RadioButton
    private lateinit var rbRepeatDaily: RadioButton
    private lateinit var rbRepeatWeekly: RadioButton
    private lateinit var rbRepeatMonthly: RadioButton
    private lateinit var repository: DatabaseRepository
    private lateinit var tvDateRepeatConflict: TextView
    private lateinit var dateTimeSelector: LinearLayout
    private var selectedDateTime: Calendar? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_single_task, container, false)
        rgRepeat = view.findViewById(R.id.repeatRadioGroup)
        rbRepeatNone = view.findViewById(R.id.repeatNoneRadioButton)
        rbRepeatDaily = view.findViewById(R.id.repeatDailyRadioButton)
        rbRepeatWeekly = view.findViewById(R.id.repeatWeeklyRadioButton)
        rbRepeatMonthly = view.findViewById(R.id.repeatMonthlyRadioButton)
        tvSelectedDateTime = view.findViewById(R.id.selectedDateTimeTextView)
        btClearDateTime = view.findViewById(R.id.clearDateTimeButton)
        tvDateRepeatConflict = view.findViewById(R.id.dateRepeatConflictTextView)
        dateTimeSelector = view.findViewById(R.id.dateTimeSelector)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var singleTask: SingleTask? = args.singleTask
        etTaskName = view.findViewById(R.id.taskNameEditText)
        val btSave = view.findViewById<Button>(R.id.saveTaskButton)
        val btDelete = view.findViewById<Button>(R.id.deleteTaskButton)
        if (singleTask != null)
            initializeFragment(singleTask)
        else {
            singleTask = SingleTask(0, PrefsManager.getProfileId(), "", 0, "",  0)
            btDelete.isVisible = false
        }
        repository = DatabaseRepository(DatabaseHelper(view.context))

        dateTimeSelector.background = requireContext().getDrawable(R.drawable.selector_background)
        dateTimeSelector.isClickable = true
        dateTimeSelector.isFocusable = true

        dateTimeSelector.setOnClickListener {
            val repeatId = rgRepeat.checkedRadioButtonId
            if (repeatId != R.id.repeatNoneRadioButton) {
                Toast.makeText(requireContext(), "Сначала уберите повторение", Toast.LENGTH_SHORT).show()
            } else {
                showDateTimePicker()
            }
        }

        btClearDateTime.setOnClickListener {
            clearDateTime()
            rbRepeatNone.isChecked = true
        }

        rgRepeat.setOnCheckedChangeListener { _, checkedId ->
            if (selectedDateTime != null && checkedId != R.id.repeatNoneRadioButton) {
                tvDateRepeatConflict.visibility = View.VISIBLE
                Toast.makeText(requireContext(), "Сначала очистите дату и время", Toast.LENGTH_SHORT).show()
                rbRepeatNone.isChecked = true
            }
        }

        btSave.setOnClickListener {
            val updatedTask = saveData(singleTask)
            try {
                if(updatedTask.taskId != 0)
                    repository.editSingleTask(updatedTask)
                else
                    repository.insertSingleTask(updatedTask.taskName, PrefsManager.getProfileId(), updatedTask.isCompleted, updatedTask.deadline, updatedTask.taskRepeat)
                findNavController().popBackStack()
            }
            catch (e: Exception){
                Log.d("uwu", "Error ${e.message}")
            }
        }
        btDelete.setOnClickListener{
            repository.deleteSingleTask(singleTask.taskId)
            findNavController().popBackStack()
        }
    }

    private fun saveData(singleTask: SingleTask): SingleTask {
        singleTask.taskName = etTaskName.text.toString()
        if (selectedDateTime == null)
            singleTask.deadline = ""
        else
            singleTask.deadline = tvSelectedDateTime.text.toString()
        val selectedRadioButtonId = rgRepeat.checkedRadioButtonId
        singleTask.taskRepeat = when (selectedRadioButtonId) {
            R.id.repeatNoneRadioButton -> 0
            R.id.repeatDailyRadioButton -> 1
            R.id.repeatWeeklyRadioButton -> 2
            R.id.repeatMonthlyRadioButton -> 3
            else -> 0
        }
        return singleTask
    }

    private fun initializeFragment(singleTask: SingleTask){
        etTaskName.setText(singleTask.taskName)
        tvSelectedDateTime.text = singleTask.deadline
        val repeatType = singleTask.taskRepeat
        val radioButtonId = when (repeatType) {
            0 -> R.id.repeatNoneRadioButton
            1 -> R.id.repeatDailyRadioButton
            2 -> R.id.repeatWeeklyRadioButton
            3 -> R.id.repeatMonthlyRadioButton
            else -> R.id.repeatNoneRadioButton
        }
        rgRepeat.check(radioButtonId)
        if (!singleTask.deadline.isNullOrEmpty()) {
            val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
            selectedDateTime = Calendar.getInstance()
            selectedDateTime!!.time = dateFormat.parse(singleTask.deadline)!!
        } else
            tvSelectedDateTime.text = "Дата и время не выбраны"
    }

    private fun showDateTimePicker() {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                val currentCalendar = Calendar.getInstance()
                val isCurrentDay = year == currentCalendar.get(Calendar.YEAR) &&
                        month == currentCalendar.get(Calendar.MONTH) &&
                        dayOfMonth == currentCalendar.get(Calendar.DAY_OF_MONTH)

                val timePickerDialog = TimePickerDialog(
                    requireContext(),
                    { _, hourOfDay, minute ->
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        calendar.set(Calendar.MINUTE, minute)
                        if (calendar.timeInMillis < System.currentTimeMillis()) {
                            Toast.makeText(
                                requireContext(),
                                "Нельзя выбрать текущую или прошлую дату и время",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@TimePickerDialog
                        }
                        selectedDateTime = calendar
                        updateDateTimeText()
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                )
                if (isCurrentDay) {
                    timePickerDialog.updateTime(
                        currentCalendar.get(Calendar.HOUR_OF_DAY),
                        currentCalendar.get(Calendar.MINUTE)
                    )
                    timePickerDialog.show()
                } else {
                    timePickerDialog.show()
                }
            },
            currentYear,
            currentMonth,
            currentDay
        )
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun clearDateTime() {
        selectedDateTime = null
        updateDateTimeText()
    }

    private fun updateDateTimeText() {
        if (selectedDateTime != null) {
            val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
            tvSelectedDateTime.text = dateFormat.format(selectedDateTime!!.time)
        } else {
            tvSelectedDateTime.text = "Дата и время не выбраны"
        }
    }
}