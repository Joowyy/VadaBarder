package com.example.vadabarder.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.vadabarder.R
import com.example.vadabarder.viewmodel.UserViewModel
import com.example.vadabarder.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding : FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var nombre = userViewModel.user.toString()
        var correo = userViewModel.correo.toString()

        binding.tvNombre.text = nombre
        binding.tvCorreo.text = correo

    }

    // Memory Leaks
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}