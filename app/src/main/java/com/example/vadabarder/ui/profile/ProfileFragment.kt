package com.example.vadabarder.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vadabarder.R
import com.example.vadabarder.ui.profile.HistorialAdapter
import com.example.vadabarder.viewmodel.CitaViewModel
import com.example.vadabarder.viewmodel.UserViewModel

import com.example.vadabarder.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding : FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by activityViewModels()
    private val citaViewModel: CitaViewModel by activityViewModels()
    private lateinit var adapter: HistorialAdapter

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

        // Datos del usuario activo desde la sesión en memoria
        binding.tvNombre.text = userViewModel.usuarioActual?.nombre ?: ""
        binding.tvCorreo.text = userViewModel.usuarioActual?.correo ?: ""

        // Indicar al CitaViewModel qué usuario está activo para filtrar sus citas en Room
        userViewModel.usuarioActual?.uid?.let { uid ->
            citaViewModel.setUsuario(uid)
        }

        // Configurar RecyclerView
        adapter = HistorialAdapter(emptyList())
        binding.rvHistorial.adapter = adapter
        binding.rvHistorial.layoutManager = LinearLayoutManager(requireContext())

        // Room notifica automáticamente cuando cambian las citas del usuario
        citaViewModel.citas.observe(viewLifecycleOwner) { citas ->
            adapter.actualizarCitas(citas)
        }

        binding.cerrarSesion.setOnClickListener {
            // Crear un diálogo de confirmación
            val alertDialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Cerrar sesión")
                .setMessage("¿Estás seguro de que quieres cerrar sesión?")
                .setPositiveButton("Sí") { dialog, _ ->

                    // Cerrar la sesión activa
                    userViewModel.cerrarSesion()

                    // Navega
                    var navController = findNavController(view)
                    navController.navigate(R.id.action_profileFragment_to_registroFragment)

                    dialog.dismiss()
                }
                .setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()

            alertDialog.show()
        }

    }

    // Memory Leaks — onDestroyView (no onDestroy) para liberar el binding
    // cuando la vista se destruye pero el fragment sigue en el back stack
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}