package ru.floppastar.multitask.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.floppastar.multitask.DataClasses.Profile
import ru.floppastar.multitask.R
import ru.floppastar.multitask.db.DatabaseRepository

class ProfileAdapter(
    var profileList: MutableList<Profile>,
    private val itemClickListener: (Profile) -> Unit,
    private val editClickListener: (Profile, Int) -> Unit)
    : RecyclerView.Adapter<ProfileAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvProfileName = itemView.findViewById<TextView>(R.id.tvProfileName)
        val ibEdit = itemView.findViewById<ImageButton>(R.id.ibEdit)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_profile, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return profileList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var profile = profileList[position]
        holder.tvProfileName.text = profile.profileName
        holder.ibEdit.setOnClickListener {
            editClickListener(profile, position)
            notifyItemChanged(position)
        }
        holder.itemView.setOnClickListener {
            itemClickListener(profile)
        }
    }
}