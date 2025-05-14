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
            startActivity(intent)}, {_, _ -> })

        recyclerView.adapter = profileAdapter
    }
}