package com.example.interviewportal.adapters

import androidx.recyclerview.widget.DiffUtil
import com.example.interviewportal.models.InterviewEntity

class InterviewItemComparator : DiffUtil.ItemCallback<InterviewEntity>() {

    override fun areItemsTheSame(oldItem: InterviewEntity, newItem: InterviewEntity): Boolean =
        oldItem.uid == newItem.uid

    override fun areContentsTheSame(oldItem: InterviewEntity, newItem: InterviewEntity): Boolean =
        oldItem == newItem

}