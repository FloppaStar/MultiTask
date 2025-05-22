package ru.floppastar.multitask.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.floppastar.multitask.Adapters.ProfileAdapter
import ru.floppastar.multitask.DataClasses.Profile
import ru.floppastar.multitask.MainActivity
import ru.floppastar.multitask.R
import ru.floppastar.multitask.db.DatabaseHelper
import ru.floppastar.multitask.db.DatabaseRepository
import ru.floppastar.multitask.utils.PrefsManager

class ProfileSelectFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var repository: DatabaseRepository
    private lateinit var profileAdapter: ProfileAdapter
    private var profileList: MutableList<Profile> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile_select, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repository = DatabaseRepository(DatabaseHelper(view.context))
        recyclerView = view.findViewById(R.id.recyclerViewProfile)
        profileList = repository.getAllProfiles()
        profileAdapter = ProfileAdapter(profileList, { profile ->
            PrefsManager.setProfileId(profile.profileId)
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)}, {profile, position ->
            showProfileBottomSheet(profile, position)})

        recyclerView.adapter = profileAdapter

        view.findViewById<View>(R.id.fabAddTask).setOnClickListener {
            showProfileBottomSheet(null, 0)
        }
    }


    private fun showProfileBottomSheet(profile: Profile?, position: Int) {
        val dialog = ProfileBottomSheetDialog(profile,
            onSave = { updatedProfile ->
                if (profile == null) {
                    val newId = repository.insertProfile(updatedProfile.profileName, updatedProfile.profileType)
                    val newProfile = updatedProfile.copy(profileId = newId)
                    profileList.add(newProfile)
                    profileAdapter.notifyItemInserted(profileList.size - 1)
                } else {
                    repository.editProfile(updatedProfile)
                    profileList[position] = updatedProfile
                    profileAdapter.notifyItemChanged(position)
                }
            },
            onDelete = {
                repository.deleteProfile(it.profileId)
                profileList.removeAt(position)
                profileAdapter.notifyItemRemoved(position)
            }
        )
        dialog.show(parentFragmentManager, "ProfileBottomSheetDialog")
    }
}