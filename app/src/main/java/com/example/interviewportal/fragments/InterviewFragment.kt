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
import com.example.interviewportal.adapters.ParticipantsAdapter
import com.example.interviewportal.databinding.FragmentInterviewBinding
import com.example.interviewportal.utils.Resource
import com.example.interviewportal.viewmodels.InterviewViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InterviewFragment : Fragment() {

    private var _binding: FragmentInterviewBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<InterviewViewModel>()

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
                        Log.d("STATUS", "onViewCreated: ERROR ${result.data}")
                    }
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}