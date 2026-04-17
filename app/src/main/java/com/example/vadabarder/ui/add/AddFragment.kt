package com.example.vadabarder.ui.add

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.vadabarder.data.model.Cita
import com.example.vadabarder.databinding.FragmentAddBinding
import com.example.vadabarder.viewmodel.UserViewModel
import com.google.android.material.chip.Chip

class AddFragment : Fragment() {

    private var _binding : FragmentAddBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by activityViewModels()

    private var fechaSeleccionada: String? = null

    private val preciosPorServicio = mapOf(
        "Corte clásico" to "12€",
        "Fade"          to "8€",
        "Barba"         to "8€",
        "Corte + Barba" to "18€"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentAddBinding.inflate(layoutInflater)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val horasDisponibles = listOf(
            "10:00", "10:30", "11:00",
            "11:30", "12:00", "12:30",
            "16:00", "16:30", "17:00"
        )

        val serviciosDisponibles = listOf(
            "Corte clásico", "Fade", "Barba", "Corte + Barba"
        )

        cargarHoras(horasDisponibles)
        cargarServicios(serviciosDisponibles)

        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            fechaSeleccionada = "%02d/%02d/%04d".format(dayOfMonth, month + 1, year)
        }

        binding.btnAgregarCita.setOnClickListener {
            val fecha    = fechaSeleccionada
            val horaChip = binding.chipGroupHoras.checkedChipId
            val servChip = binding.chipGroupServicios.checkedChipId

            if (fecha == null) {
                Toast.makeText(requireContext(), "Selecciona una fecha", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (horaChip == View.NO_ID) {
                Toast.makeText(requireContext(), "Selecciona una hora", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (servChip == View.NO_ID) {
                Toast.makeText(requireContext(), "Selecciona un servicio", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val hora     = binding.chipGroupHoras.findViewById<Chip>(horaChip).text.toString()
            val servicio = binding.chipGroupServicios.findViewById<Chip>(servChip).text.toString()
            val precio   = preciosPorServicio[servicio] ?: "-"

            userViewModel.agregarCita(Cita(fecha, hora, servicio, precio))
            Toast.makeText(requireContext(), "Cita añadida", Toast.LENGTH_SHORT).show()
            resetFormulario()
        }

    }

    private fun resetFormulario() {
        fechaSeleccionada = null
        binding.calendarView.date = System.currentTimeMillis()
        binding.chipGroupHoras.clearCheck()
        binding.chipGroupServicios.clearCheck()
        binding.cardHoras.visibility = View.GONE
    }

    private fun cargarHoras(horas: List<String>) {
        binding.chipGroupHoras.removeAllViews()
        binding.cardHoras.visibility = View.VISIBLE

        horas.forEach { hora ->
            val chip = Chip(requireContext()).apply {
                text = hora
                isCheckable = true
                isClickable = true

                layoutParams = ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(8, 8, 8, 8)
                }
            }
            binding.chipGroupHoras.addView(chip)
        }
    }

    private fun cargarServicios(servicios: List<String>) {
        binding.chipGroupServicios.removeAllViews()

        servicios.forEach { servicio ->
            val chip = Chip(requireContext()).apply {
                text = servicio
                isCheckable = true
                isClickable = true

                layoutParams = ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(8, 8, 8, 8)
                }
            }
            binding.chipGroupServicios.addView(chip)
        }
    }

    // Memory Leaks
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}