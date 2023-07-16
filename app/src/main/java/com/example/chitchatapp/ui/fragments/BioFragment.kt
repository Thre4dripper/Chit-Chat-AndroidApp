package com.example.chitchatapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.chitchatapp.Constants
import com.example.chitchatapp.databinding.FragmentBioBinding
import com.example.chitchatapp.viewModels.UserDetailsViewModel

class BioFragment : Fragment() {
    private lateinit var binding: FragmentBioBinding
    private lateinit var userDetailsViewModel: UserDetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBioBinding.inflate(inflater, container, false)
        userDetailsViewModel = ViewModelProvider(this)[UserDetailsViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bioEt.requestFocus()

        binding.bioBackBtn.setOnClickListener {
            requireActivity().finish()
        }

        binding.bioSaveBtn.setOnClickListener {
            saveBio()
        }

        binding.bioProgressBar.visibility = View.GONE
        getBio()
    }

    private fun getBio() {
        userDetailsViewModel.userDetails.observe(viewLifecycleOwner) { userDetails ->
            if (userDetails != null) {
                binding.bioEt.setText(userDetails.bio)
            }
        }
    }

    private fun saveBio() {
        binding.bioProgressBar.visibility = View.VISIBLE
        binding.bioSaveBtn.visibility = View.GONE
        val bio = binding.bioEt.text.toString().trim()

        userDetailsViewModel.updateBio(requireContext(), bio) { message ->
            if (message == Constants.BIO_UPDATED_SUCCESSFULLY) {
                requireActivity().finish()
            } else {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
            binding.bioProgressBar.visibility = View.GONE
            binding.bioSaveBtn.visibility = View.VISIBLE
        }
    }
}