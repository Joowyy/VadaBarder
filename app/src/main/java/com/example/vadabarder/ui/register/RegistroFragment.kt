package com.example.vadabarder.ui.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.fragment.app.activityViewModels
import com.example.vadabarder.R
import com.example.vadabarder.databinding.FragmentRegistroBinding
import com.example.vadabarder.viewmodel.UserViewModel

class RegistroFragment : Fragment() {

    private var _binding : FragmentRegistroBinding? = null
    private val binding get() = _binding!!
    private val userViewModel : UserViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {

        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentRegistroBinding.inflate(layoutInflater)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Pulsar boton de login
        binding.btnRegistro.setOnClickListener {

            // Obtenemos valores
            val user = binding.editTextNombre.text.toString()
            val correo = binding.editTextCorreo.text.toString()
            val psswd = binding.editTextPsswd.text.toString()

            // Comprueba y mensaje de error
            if (psswd.isEmpty() || correo.isEmpty() || user.isEmpty()) {

                if (user.isEmpty()) {

                    binding.editTextNombre.error = "Campo obligatorio"

                }

                if (correo.isEmpty()) {

                    binding.editTextCorreo.error = "Campo obligatorio"

                }

                if (psswd.isEmpty()) {

                    binding.editTextPsswd.error = "Campo obligatorio"

                }

                Toast.makeText(requireContext(), "Completa los campos obligatorios", Toast.LENGTH_SHORT).show()

            } else {

                userViewModel.user = user
                userViewModel.correo = correo

                // Navega
                var navController = findNavController(view)
                navController.navigate(R.id.action_registroFragment_to_homeFragment)

            }

        }

        // Volver al Inicio de Sesion
        binding.camInicioSesion.setOnClickListener {

            val navController: NavController = findNavController(view)
            navController.navigate(R.id.action_registroFragment_to_loginFragment)

        }

    }

    // Memory Leaks
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}