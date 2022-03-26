package com.example.interviewportal.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.interviewportal.R
import com.example.interviewportal.databinding.ItemInterviewBinding
import com.example.interviewportal.models.InterviewEntity

class InterviewAdapter(
    private val onClick: (InterviewEntity) -> Unit,
    private val context: Context
) :
    ListAdapter<InterviewEntity, InterviewAdapter.InterviewViewHolder>(InterviewItemComparator()) {

    inner class InterviewViewHolder(private val binding: ItemInterviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: InterviewEntity) =
            binding.apply {
                itemInterviewDate.text = item.date
                itemInterviewTitle.text = item.title
                itemTime.text = context.getString(
                    R.string.formatted_time,
                    item.startTime,
                    item.endTime
                )
                chipParticipantsCount.text =
                    context.getString(R.string.formatted_participant_count, item.startTimeInt)
                root.setOnClickListener {
                    onClick(item)
                }
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