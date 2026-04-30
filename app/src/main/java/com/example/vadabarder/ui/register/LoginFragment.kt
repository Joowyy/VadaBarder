package com.example.vadabarder.ui.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation.findNavController
import com.example.vadabarder.R
import com.example.vadabarder.data.model.AuthState
import com.example.vadabarder.databinding.FragmentLoginBinding
import com.example.vadabarder.viewmodel.AuthViewModel

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Si ya hay sesión activa, saltar directamente a Home
        if (authViewModel.getCurrentUser() != null) {
            findNavController(view).navigate(R.id.action_loginFragment_to_homeFragment)
            return
        }

        authViewModel.authState.observe(viewLifecycleOwner) { state ->
            state ?: return@observe
            when (state) {
                is AuthState.Loading -> Unit
                is AuthState.Success -> findNavController(view).navigate(R.id.action_loginFragment_to_homeFragment)
                is AuthState.Error   -> Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnLogin.setOnClickListener {
            val correo = binding.editTextCorreo.text.toString().trim()
            val psswd  = binding.editTextPsswd.text.toString()
            authViewModel.login(correo, psswd)
        }

        binding.camRegistro.setOnClickListener {
            findNavController(view).navigate(R.id.registroFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
