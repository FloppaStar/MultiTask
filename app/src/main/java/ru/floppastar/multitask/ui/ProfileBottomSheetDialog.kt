package ru.floppastar.multitask.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.floppastar.multitask.DataClasses.Profile
import ru.floppastar.multitask.R

class ProfileBottomSheetDialog(
    private val profile: Profile?,
    private val onSave: (Profile) -> Unit,
    private val onDelete: ((Profile) -> Unit)? = null
) : BottomSheetDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        val view = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_profile, null)
        dialog.setContentView(view)

        val etName = view.findViewById<EditText>(R.id.etProfileName)
        val etType = view.findViewById<EditText>(R.id.etProfileType)
        val btSave = view.findViewById<Button>(R.id.btnSaveProfile)
        val btDelete = view.findViewById<Button>(R.id.btnDeleteProfile)

        if (profile != null) {
            etName.setText(profile.profileName)
            etType.setText(profile.profileType)
            btDelete.visibility = View.VISIBLE
        }

        btSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val type = etType.text.toString().trim()

            if (name.isNotEmpty() && type.isNotEmpty()) {
                val profile = profile ?: Profile(0, name, type)
                profile.profileName = name
                profile.profileType = type
                onSave(profile)
                dismiss()
            } else {
                etName.error = if (name.isEmpty()) "Имя обязательно" else null
                etType.error = if (type.isEmpty()) "Тип обязателен" else null
            }
        }

        btDelete.setOnClickListener {
            profile?.let {
                onDelete?.invoke(it)
                dismiss()
            }
        }
        return dialog
    }
}