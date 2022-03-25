package com.example.interviewportal.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.interviewportal.R
import com.example.interviewportal.databinding.ItemParticipantBinding
import com.example.interviewportal.models.User

class ParticipantsAdapter(private val context: Context) :
    ListAdapter<User, ParticipantsAdapter.ParticipantViewHolder>(ParticipantItemComparator()) {

    inner class ParticipantViewHolder(private val binding: ItemParticipantBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: User) {
            binding.apply {
                itemTextName.text = item.username
                itemTextEmail.text = item.email
                val str = item.username.split(Regex(" "), 2)
                itemChipName.text = str.first()[0].toString() + str.last()[0].toString()
                when (item.color) {
                    0 -> itemChipName.chipBackgroundColor =
                        ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorPink))
                    1 -> itemChipName.chipBackgroundColor =
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                context,
                                R.color.colorOrange
                            )
                        )
                    2 -> itemChipName.chipBackgroundColor =
                        ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorPurple))
                    3 -> itemChipName.chipBackgroundColor =
                        ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorBlue))
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParticipantViewHolder =
        ParticipantViewHolder(
            ItemParticipantBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ParticipantViewHolder, position: Int) =
        holder.bind(getItem(position))

}