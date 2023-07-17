package com.example.chitchatapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.chitchatapp.constants.SuccessMessages
import com.example.chitchatapp.databinding.FragmentNameBinding
import com.example.chitchatapp.viewModels.UserDetailsViewModel

class NameFragment : Fragment() {
    private lateinit var binding: FragmentNameBinding
    private lateinit var userDetailsViewModel: UserDetailsViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNameBinding.inflate(inflater, container, false)
        userDetailsViewModel = ViewModelProvider(this)[UserDetailsViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.nameEt.requestFocus()

        binding.nameBackBtn.setOnClickListener {
            requireActivity().finish()
        }

        binding.nameSaveBtn.setOnClickListener {
            saveName()
        }

        binding.nameProgressBar.visibility = View.GONE
        getName()
    }

    private fun getName() {
        userDetailsViewModel.userDetails.observe(viewLifecycleOwner) { userDetails ->
            if (userDetails != null) {
                binding.nameEt.setText(userDetails.name)
            }
        }
    }

    private fun saveName() {
        binding.nameProgressBar.visibility = View.VISIBLE
        binding.nameSaveBtn.visibility = View.GONE
        val name = binding.nameEt.text.toString().trim()

        if(name.isEmpty()){
            binding.nameEt.error = "Name cannot be empty"
            binding.nameProgressBar.visibility = View.GONE
            binding.nameSaveBtn.visibility = View.VISIBLE
            return
        }

        userDetailsViewModel.updateName(requireContext(), name) { message ->
            if (message == SuccessMessages.NAME_UPDATED_SUCCESSFULLY) {
                requireActivity().finish()
            } else {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }

            binding.nameProgressBar.visibility = View.GONE
            binding.nameSaveBtn.visibility = View.VISIBLE
        }
    }
}