package com.example.interviewportal.adapters

import androidx.recyclerview.widget.DiffUtil
import com.example.interviewportal.models.User

class ParticipantItemComparator : DiffUtil.ItemCallback<User>() {

    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean =
        oldItem.uid == newItem.uid

    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean =
        oldItem == newItem

}