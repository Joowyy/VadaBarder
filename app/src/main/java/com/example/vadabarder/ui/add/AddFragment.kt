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
import java.util.Calendar

class AddFragment : Fragment() {

    private var _binding : FragmentAddBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by activityViewModels()

    private var fechaSeleccionada: String? = null

    // Slots de hora sincronizados con el horario del local (Home)
    private val horasMañana = listOf(
        "09:00", "09:30", "10:00", "10:30", "11:00", "11:30",
        "12:00", "12:30", "13:00", "13:30"
    )
    private val horasTarde = listOf(
        "16:00", "16:30", "17:00", "17:30", "18:00", "18:30",
        "19:00", "19:30"
    )

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

        val serviciosDisponibles = listOf(
            "Corte clásico", "Fade", "Barba", "Corte + Barba"
        )

        // Deshabilitar fechas pasadas en el calendario
        binding.calendarView.minDate = System.currentTimeMillis()

        cargarServicios(serviciosDisponibles)

        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            fechaSeleccionada = "%02d/%02d/%04d".format(dayOfMonth, month + 1, year)
            val cal = Calendar.getInstance().apply { set(year, month, dayOfMonth) }
            cargarHoras(cal.get(Calendar.DAY_OF_WEEK))
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

    private fun cargarHoras(diaSemana: Int) {
        binding.chipGroupHoras.removeAllViews()

        // Domingo — cerrado
        if (diaSemana == Calendar.SUNDAY) {
            binding.cardHoras.visibility = View.GONE
            Toast.makeText(requireContext(), "La barbería está cerrada los domingos", Toast.LENGTH_SHORT).show()
            return
        }

        // Sábado solo mañana; Lun–Vie mañana + tarde
        val horasBase = if (diaSemana == Calendar.SATURDAY) horasMañana
                        else horasMañana + horasTarde

        val horasFiltradas = filtrarHorasPasadas(horasBase)

        if (horasFiltradas.isEmpty()) {
            binding.cardHoras.visibility = View.GONE
            Toast.makeText(requireContext(), "No hay horas disponibles para hoy", Toast.LENGTH_SHORT).show()
            return
        }

        binding.cardHoras.visibility = View.VISIBLE
        horasFiltradas.forEach { hora ->
            val chip = Chip(requireContext()).apply {
                text = hora
                isCheckable = true
                isClickable = true
                layoutParams = ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(8, 8, 8, 8) }
            }
            binding.chipGroupHoras.addView(chip)
        }
    }

    // Filtra las horas que ya han pasado si el día seleccionado es hoy
    private fun filtrarHorasPasadas(horas: List<String>): List<String> {
        val ahora = Calendar.getInstance()
        val partes = fechaSeleccionada!!.split("/")

        val esHoy = partes[0].toInt() == ahora.get(Calendar.DAY_OF_MONTH) &&
                    partes[1].toInt() - 1 == ahora.get(Calendar.MONTH) &&
                    partes[2].toInt() == ahora.get(Calendar.YEAR)

        if (!esHoy) {
            return horas
        }

        val horaActual   = ahora.get(Calendar.HOUR_OF_DAY)
        val minutoActual = ahora.get(Calendar.MINUTE)

        return horas.filter { horaStr ->
            val (h, m) = horaStr.split(":").map { it.toInt() }
            h > horaActual || (h == horaActual && m > minutoActual)
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