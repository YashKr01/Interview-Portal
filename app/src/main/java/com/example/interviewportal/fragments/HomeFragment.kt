package com.example.interviewportal.fragments

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.interviewportal.R
import com.example.interviewportal.adapters.InterviewAdapter
import com.example.interviewportal.databinding.FragmentHomeBinding
import com.example.interviewportal.utils.Constants.showSnackBar
import com.example.interviewportal.utils.ExtensionFunctions.hide
import com.example.interviewportal.utils.ExtensionFunctions.show
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

        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)

        binding.floatingActionButton.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToInterviewFragment())
        }

        val interviewAdapter = InterviewAdapter(onClick = { interview ->
            findNavController()
                .navigate(HomeFragmentDirections.actionHomeFragmentToEditInterviewFragment(interview))
        }, context = requireContext())

        binding.recyclerViewInterviews.apply {
            setHasFixedSize(false)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = interviewAdapter
        }

        viewModel.interviewList.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> binding.progressBar3.show()
                is Resource.Error -> {
                    binding.progressBar3.hide()
                    showSnackBar(requireContext(), binding.root, result.message.toString())
                }
                is Resource.Success -> {
                    binding.progressBar3.hide()
                    interviewAdapter.submitList(result.data)
                }
            }
        }

        setHasOptionsMenu(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}