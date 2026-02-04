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
import com.example.vadabarder.data.model.Cita
import com.example.vadabarder.data.model.HistorialAdapter
import com.example.vadabarder.viewmodel.UserViewModel
import com.example.vadabarder.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding : FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by activityViewModels()
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

        var nombre = userViewModel.user.toString()
        var correo = userViewModel.correo.toString()

        binding.tvNombre.text = nombre
        binding.tvCorreo.text = correo

        // Configurar RecyclerView
        adapter = HistorialAdapter(emptyList())
        binding.rvHistorial.adapter = adapter
        binding.rvHistorial.layoutManager = LinearLayoutManager(requireContext())

        // Observar cambios en el historial de citas
        userViewModel.historialCitas.observe(viewLifecycleOwner) { citas ->
            adapter.actualizarCitas(citas)
        }

        // Ejemplo de agregar citas (puedes reemplazar con tu lógica real)
        userViewModel.agregarCita(Cita("04/02/2026", "16:00", "Corte clásico", "12€"))
        userViewModel.agregarCita(Cita("02/02/2026", "11:30", "Barba", "8€"))

        binding.cerrarSesion.setOnClickListener {
            // Crear un diálogo de confirmación
            val alertDialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Cerrar sesión")
                .setMessage("¿Estás seguro de que quieres cerrar sesión?")
                .setPositiveButton("Sí") { dialog, _ ->

                    // Vaciar los valores del ViewModel
                    userViewModel.limpiarViewModel()

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

    // Memory Leaks
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}