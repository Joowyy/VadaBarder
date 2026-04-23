package com.example.vadabarder.ui.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import com.example.vadabarder.R
import com.example.vadabarder.databinding.FragmentLoginBinding
import com.example.vadabarder.viewmodel.UserViewModel

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener {
            val nombre = binding.editTextNombre.text.toString().trim()
            val psswd  = binding.editTextPsswd.text.toString()

            if (nombre.isEmpty() || psswd.isEmpty()) {
                binding.editTextNombre.error = "Campo obligatorio"
                binding.editTextPsswd.error  = "Campo obligatorio"
                Toast.makeText(requireContext(), "Completa los campos obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Busca el nombre de usuario en Room; el callback llega al hilo principal
            // Lo use bastante en las practicas con Java, realmente es mas comodo
            // Que hacer una simple condicion con if, me aseguro condicionar antes que mostrar.
            userViewModel.login(
                nombre  = nombre,
                onExito = {
                    // Usuario encontrado → navegar a Home
                    findNavController(view).navigate(R.id.action_loginFragment_to_homeFragment)
                },
                onFallo = {
                    // Correo no registrado
                    Toast.makeText(
                        requireContext(),
                        "Usuario no registrado o contraseña inválida. ¿Tienes cuenta?",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }

        binding.camRegistro.setOnClickListener {
            val navController: NavController = findNavController(view)
            navController.navigate(R.id.registroFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
