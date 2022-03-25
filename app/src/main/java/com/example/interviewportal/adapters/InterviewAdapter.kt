package com.example.interviewportal.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.interviewportal.databinding.ItemInterviewBinding
import com.example.interviewportal.models.InterviewEntity

class InterviewAdapter :
    ListAdapter<InterviewEntity, InterviewAdapter.InterviewViewHolder>(InterviewItemComparator()) {

    class InterviewViewHolder(private val binding: ItemInterviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: InterviewEntity) =
            binding.apply {
                itemInterviewDate.text = item.date
                itemInterviewTitle.text = item.title
                itemTime.text = "From : ${item.startTime} to ${item.endTime}"
                chipParticipantsCount.text = item.numberOfParticipants.toString()
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InterviewViewHolder =
        InterviewViewHolder(
            ItemInterviewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: InterviewViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}