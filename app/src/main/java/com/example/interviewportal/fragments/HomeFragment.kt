package com.example.interviewportal.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.interviewportal.adapters.InterviewAdapter
import com.example.interviewportal.databinding.FragmentHomeBinding
import com.example.interviewportal.utils.Resource
import com.example.interviewportal.viewmodels.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<HomeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.floatingActionButton.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToInterviewFragment())
        }

        val interviewAdapter = InterviewAdapter(onClick = { interview ->
            findNavController()
                .navigate(HomeFragmentDirections.actionHomeFragmentToEditInterviewFragment(interview))
        })
        binding.recyclerViewInterviews.apply {
            setHasFixedSize(false)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = interviewAdapter
        }

        viewModel.interviewList.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> binding.progressBar3.visibility = View.VISIBLE
                is Resource.Error -> {
                    binding.progressBar3.visibility = View.GONE
                }
                is Resource.Success -> {
                    binding.progressBar3.visibility = View.GONE
                    interviewAdapter.submitList(result.data)
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}