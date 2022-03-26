package com.example.interviewportal.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.example.interviewportal.utils.Constants.buildMaterialDatePicker
import com.example.interviewportal.utils.Constants.buildMaterialTimePicker
import com.example.interviewportal.utils.Constants.showSnackBar
import com.example.interviewportal.utils.ExtensionFunctions.hide
import com.example.interviewportal.utils.ExtensionFunctions.show
import com.example.interviewportal.utils.Resource
import com.example.interviewportal.viewmodels.EditInterviewViewModel
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
                    showSnackBar(
                        requireContext(),
                        binding.root,
                        getString(R.string.creating_interview)
                    )
                    binding.progressBar.show()
                }
                is Resource.Error -> {
                    binding.progressBar.hide()
                    showSnackBar(requireContext(), binding.root, result.message.toString())
                }
                is Resource.Success -> {
                    binding.progressBar.hide()
                    showSnackBar(
                        requireContext(),
                        binding.root,
                        getString(R.string.interview_created_successfully)
                    )
                    lifecycleScope.launch(Dispatchers.Main) {
                        delay(1500)
                        findNavController().popBackStack()
                    }
                }
            }
        }

        viewModel.participantList.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> binding.progressBar.show()
                is Resource.Error -> {
                    binding.progressBar.hide()
                    showSnackBar(requireContext(), binding.root, result.message.toString())
                }
                is Resource.Success -> {
                    binding.progressBar.hide()
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

        binding.inputDate.setOnClickListener { selectDate() }

        binding.inputStartTime.setOnClickListener { selectStartTime() }

        binding.inputEndTime.setOnClickListener { selectEndTime() }

    }

    private fun selectEndTime() {

        val picker = buildMaterialTimePicker()

        picker.show(parentFragmentManager, picker.tag)

        picker.addOnPositiveButtonClickListener {

            val pickedHour: Int = picker.hour
            val pickedMinute: Int = picker.minute

            val formattedTime = Constants.getFormattedTime(pickedHour, pickedMinute, picker)

            binding.textEndTime.text = formattedTime
            endTimeInt = Constants.getFormattedTime(formattedTime)
        }

    }

    private fun selectStartTime() {

        val picker = buildMaterialTimePicker()

        picker.show(parentFragmentManager, picker.tag)

        picker.addOnPositiveButtonClickListener {

            val pickedHour: Int = picker.hour
            val pickedMinute: Int = picker.minute

            val formattedTime = Constants.getFormattedTime(pickedHour, pickedMinute, picker)

            binding.textStartTime.text = formattedTime
            startTimeInt = Constants.getFormattedTime(formattedTime)
        }

    }

    private fun selectDate() {

        val datePicker = buildMaterialDatePicker()

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