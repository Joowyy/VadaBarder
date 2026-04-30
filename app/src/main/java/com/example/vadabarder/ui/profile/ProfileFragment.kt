package com.example.vadabarder.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavOptions
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vadabarder.R
import com.example.vadabarder.databinding.FragmentProfileBinding
import com.example.vadabarder.viewmodel.AuthViewModel
import com.example.vadabarder.viewmodel.CitaViewModel

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val authViewModel: AuthViewModel by activityViewModels()
    private val citaViewModel: CitaViewModel by activityViewModels()
    private lateinit var adapter: HistorialAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = authViewModel.getCurrentUser()
        binding.tvNombre.text = user?.displayName ?: ""
        binding.tvCorreo.text = user?.email ?: ""

        user?.uid?.let { uid -> citaViewModel.setUsuario(uid) }

        adapter = HistorialAdapter(emptyList())
        binding.rvHistorial.adapter = adapter
        binding.rvHistorial.layoutManager = LinearLayoutManager(requireContext())

        citaViewModel.citas.observe(viewLifecycleOwner) { citas ->
            adapter.actualizarCitas(citas)
        }

        binding.cerrarSesion.setOnClickListener {
            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Cerrar sesión")
                .setMessage("¿Estás seguro de que quieres cerrar sesión?")
                .setPositiveButton("Sí") { dialog, _ ->
                    citaViewModel.limpiarUsuario()
                    authViewModel.cerrarSesion()
                    val options = NavOptions.Builder()
                        .setPopUpTo(R.id.registroFragment, true)
                        .build()
                    findNavController(view).navigate(R.id.registroFragment, null, options)
                    dialog.dismiss()
                }
                .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
