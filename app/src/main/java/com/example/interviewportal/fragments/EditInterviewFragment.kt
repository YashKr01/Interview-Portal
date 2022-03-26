package com.example.interviewportal.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.interviewportal.R
import com.example.interviewportal.adapters.ParticipantsAdapter
import com.example.interviewportal.databinding.FragmentEditInterviewBinding
import com.example.interviewportal.models.InterviewEntity
import com.example.interviewportal.models.User
import com.example.interviewportal.utils.Constants
import com.example.interviewportal.utils.Resource
import com.example.interviewportal.viewmodels.EditInterviewViewModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditInterviewFragment : Fragment() {

    private var _binding: FragmentEditInterviewBinding? = null
    private val binding get() = _binding!!

    private val argument by navArgs<EditInterviewFragmentArgs>()

    private var startTimeInt: Int? = null
    private var endTimeInt: Int? = null

    private val viewModel by viewModels<EditInterviewViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditInterviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val interview = argument.interview

        startTimeInt = interview.startTimeInt
        endTimeInt = interview.endTimeInt

        binding.apply {
            textInterviewTitle.setText(interview.title)
            textStartTime.text = interview.startTime
            textEndTime.text = interview.endTime
            textViewDate.text = interview.date
        }

        val participantAdapter = ParticipantsAdapter(requireContext())
        binding.recyclerViewParticipants.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = participantAdapter
        }

        viewModel.interviewCreationResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> {
                    Snackbar.make(
                        requireContext(),
                        binding.root,
                        getString(R.string.creating_interview),
                        Snackbar.LENGTH_SHORT
                    ).show()
                    binding.btnEditInterview.isEnabled = false
                }
                is Resource.Error -> {
                    Snackbar.make(
                        requireContext(),
                        binding.root,
                        result.message.toString(),
                        Snackbar.LENGTH_SHORT
                    ).show()
                    binding.btnEditInterview.isClickable = true
                }
                is Resource.Success -> {
                    Snackbar.make(
                        requireContext(),
                        binding.root,
                        getString(R.string.interview_created_successfully),
                        Snackbar.LENGTH_SHORT
                    ).show()
                    lifecycleScope.launch(Dispatchers.Main) {
                        delay(1500)
                        findNavController().popBackStack()
                    }
                }
            }
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
                        val listSet = HashSet<String>(interview.participants.split(Regex(", ")))
                        val list = mutableListOf<User>()
                        for (item in result.data) {
                            if (listSet.contains(item.uid)) list.add(item.copy(isSelected = true))
                            else list.add(item)
                        }
                        participantAdapter.submitList(list)
                        listSet.clear()
                    }
                }
            }
        }

        binding.btnEditInterview.setOnClickListener {
            val list = participantAdapter.getList()
            val entity = InterviewEntity(
                uid = interview.uid,
                date = binding.textViewDate.text.toString(),
                numberOfParticipants = list.size,
                endTime = binding.textEndTime.text.toString(),
                startTime = binding.textStartTime.text.toString(),
                startTimeInt = startTimeInt,
                endTimeInt = endTimeInt,
                participants = list.toString()
                    .substring(1, list.toString().length - 1),
                title = binding.textInterviewTitle.text.toString()
            )
            viewModel.createInterview(entity)
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