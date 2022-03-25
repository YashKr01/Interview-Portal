package com.example.interviewportal.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.interviewportal.R
import com.example.interviewportal.adapters.ParticipantsAdapter
import com.example.interviewportal.databinding.FragmentInterviewBinding
import com.example.interviewportal.models.InterviewEntity
import com.example.interviewportal.utils.Constants
import com.example.interviewportal.utils.Resource
import com.example.interviewportal.viewmodels.InterviewViewModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class InterviewFragment : Fragment() {

    private var _binding: FragmentInterviewBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<InterviewViewModel>()

    private var startTimeInt: Int? = null
    private var endTimeInt: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInterviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val participantAdapter = ParticipantsAdapter(requireContext())
        binding.recyclerViewParticipants.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = participantAdapter
        }

        viewModel.participantList.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> binding.progressBar.visibility = View.VISIBLE
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        "ERROR : ${result.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    if (!result.data.isNullOrEmpty()) {
                        participantAdapter.submitList(result.data)
                    }
                }
            }
        }

        binding.inputDate.setOnClickListener {
            selectDate()
        }

        binding.inputStartTime.setOnClickListener {
            selectStartTime()
        }

        binding.inputEndTime.setOnClickListener {
            selectEndTime()
        }

        viewModel.interviewCreationResult.observe(viewLifecycleOwner) { result->
            when(result){
                is  Resource.Loading -> Log.d("STATUS", "onViewCreated: LOADING")
                is  Resource.Error -> Log.d("STATUS", "onViewCreated: ERROR")
                is  Resource.Success -> Log.d("STATUS", "onViewCreated: SUCCESS")
            }
        }

        binding.btnCreateInterview.setOnClickListener {
            val entity = InterviewEntity(
                uid = UUID.randomUUID().toString(),
                date = binding.textViewDate.text.toString(),
                numberOfParticipants = 8,
                endTime = binding.textEndTime.text.toString(),
                startTime = binding.textStartTime.text.toString(),
                startTimeInt = startTimeInt!!,
                endTimeInt = endTimeInt!!,
                participants = "",
            )
            viewModel.createInterview(entity)
        }

    }

    private fun selectEndTime() {

        val picker =
            MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(12)
                .setMinute(10)
                .setTitleText(getString(R.string.select_time))
                .build()

        picker.show(parentFragmentManager, picker.tag)

        picker.addOnPositiveButtonClickListener {

            val pickedHour: Int = picker.hour
            val pickedMinute: Int = picker.minute

            val formattedTime = Constants.getFormattedTime(pickedHour, pickedMinute, picker)

            binding.textEndTime.text = formattedTime
            endTimeInt = Constants.getFormattedTime(formattedTime)
            Toast.makeText(requireContext(), "$endTimeInt", Toast.LENGTH_SHORT).show()
        }

    }

    private fun selectStartTime() {

        val picker =
            MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(12)
                .setMinute(10)
                .setTitleText("Select time")
                .build()

        picker.show(parentFragmentManager, picker.tag)

        picker.addOnPositiveButtonClickListener {

            val pickedHour: Int = picker.hour
            val pickedMinute: Int = picker.minute

            val formattedTime = Constants.getFormattedTime(pickedHour, pickedMinute, picker)

            binding.textStartTime.text = formattedTime
            startTimeInt = Constants.getFormattedTime(formattedTime)
            Toast.makeText(requireContext(), "$startTimeInt", Toast.LENGTH_SHORT).show()
        }

    }

    private fun selectDate() {

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.pick_date))
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .setCalendarConstraints(
                CalendarConstraints.Builder()
                    .setValidator(DateValidatorPointForward.now()).build()
            )
            .build()

        datePicker.show(parentFragmentManager, datePicker.tag)

        datePicker.addOnPositiveButtonClickListener {
            binding.textViewDate.text = datePicker.headerText
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}