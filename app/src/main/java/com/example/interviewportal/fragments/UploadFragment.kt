package com.example.interviewportal.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.interviewportal.R
import com.example.interviewportal.databinding.FragmentUploadBinding
import com.example.interviewportal.utils.Constants.FILE_LOCATION
import com.example.interviewportal.utils.Constants.FILE_TYPE
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.storage.FirebaseStorage

class UploadFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentUploadBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUploadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnUpload.setOnClickListener {
            val galleryIntent = Intent()
            galleryIntent.action = Intent.ACTION_GET_CONTENT
            galleryIntent.type = FILE_TYPE
            startActivityForResult(galleryIntent, 1)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
        super.onActivityResult(requestCode, resultCode, intentData)

        if (resultCode == RESULT_OK && intentData?.data != null) {

            Toast.makeText(requireContext(), getString(R.string.uploading), Toast.LENGTH_LONG)
                .show()
            binding.btnUpload.isClickable = false

            val storageReference = FirebaseStorage.getInstance().getReference(FILE_LOCATION)
            val reference = storageReference.child("${System.currentTimeMillis()}.pdf")

            reference.putFile(intentData.data!!).addOnSuccessListener {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.upload_success),
                    Toast.LENGTH_SHORT
                ).show()
                binding.btnUpload.isClickable = true
            }.addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.upload_fail),
                    Toast.LENGTH_SHORT
                ).show()
                binding.btnUpload.isClickable = true
            }

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}