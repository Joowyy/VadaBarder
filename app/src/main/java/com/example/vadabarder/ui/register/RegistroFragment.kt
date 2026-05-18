package com.example.vadabarder.ui.register

import android.animation.ObjectAnimator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation.findNavController
import com.example.vadabarder.R
import com.example.vadabarder.data.model.AuthState
import com.example.vadabarder.data.prefs.SessionPreferences
import com.example.vadabarder.databinding.FragmentRegistroBinding
import com.example.vadabarder.viewmodel.AuthViewModel

class RegistroFragment : Fragment() {

    private var _binding: FragmentRegistroBinding? = null
    private val binding get() = _binding!!
    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistroBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Si ya hay sesión activa, saltar directamente a Home
        if (authViewModel.getCurrentUser() != null) {
            findNavController(view).navigate(R.id.action_registroFragment_to_homeFragment)
            return
        }

        animarLogo(binding.imgLogo)

        // Reflejar el último estado guardado en la checkbox
        binding.cbRecordar.isChecked = SessionPreferences.isRecordar(requireContext())

        authViewModel.authState.observe(viewLifecycleOwner) { state ->
            state ?: return@observe
            when (state) {
                is AuthState.Loading -> Unit
                is AuthState.Success -> findNavController(view).navigate(R.id.action_registroFragment_to_homeFragment)
                is AuthState.Error   -> Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnRegistro.setOnClickListener {
            val nombre = binding.editTextNombre.text.toString().trim()
            val correo = binding.editTextCorreo.text.toString().trim()
            val psswd  = binding.editTextPsswd.text.toString()
            SessionPreferences.setRecordar(requireContext(), binding.cbRecordar.isChecked)
            authViewModel.registrar(nombre, correo, psswd)
        }

        binding.camInicioSesion.setOnClickListener {
            findNavController(view).navigate(R.id.action_registroFragment_to_loginFragment)
        }
    }

    private fun animarLogo(logo: ImageView) {
        // Entrada: scale desde 0.65 a 1.0 con efecto overshoot + fade in
        logo.scaleX = 0.65f
        logo.scaleY = 0.65f
        logo.alpha = 0f
        logo.animate()
            .scaleX(1f)
            .scaleY(1f)
            .alpha(1f)
            .setDuration(520)
            .setInterpolator(OvershootInterpolator(1.6f))
            .withEndAction {
                // Pulso sutil continuo
                ObjectAnimator.ofFloat(logo, View.SCALE_X, 1f, 1.06f, 1f).apply {
                    duration = 3200
                    repeatCount = ObjectAnimator.INFINITE
                    interpolator = AccelerateDecelerateInterpolator()
                    start()
                }
                ObjectAnimator.ofFloat(logo, View.SCALE_Y, 1f, 1.06f, 1f).apply {
                    duration = 3200
                    repeatCount = ObjectAnimator.INFINITE
                    interpolator = AccelerateDecelerateInterpolator()
                    start()
                }
            }
            .start()
    }

    override fun onDestroyView() {
        binding.imgLogo.animate().cancel()
        binding.imgLogo.clearAnimation()
        super.onDestroyView()
        _binding = null
    }
}
