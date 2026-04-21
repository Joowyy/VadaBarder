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
import androidx.core.content.ContextCompat
import com.example.vadabarder.R
import com.example.vadabarder.data.BarberiaData
import java.util.Calendar

class AddFragment : Fragment() {

    private var _binding : FragmentAddBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by activityViewModels()

    private var fechaSeleccionada: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { }
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

        // Deshabilitar fechas pasadas en el calendario
        binding.calendarView.minDate = System.currentTimeMillis()

        cargarServicios(BarberiaData.servicios.keys.toList())

        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            fechaSeleccionada = "%02d/%02d/%04d".format(dayOfMonth, month + 1, year)
            val cal = Calendar.getInstance().apply { set(year, month, dayOfMonth) }
            cargarHoras(cal.get(Calendar.DAY_OF_WEEK))
        }

        binding.tvToggleDesglose.setOnClickListener {
            if (binding.layoutDesglose.visibility == View.GONE) {
                binding.layoutDesglose.visibility = View.VISIBLE
                binding.tvToggleDesglose.text = "Ocultar ▴"
            } else {
                binding.layoutDesglose.visibility = View.GONE
                binding.tvToggleDesglose.text = "Ver desglose ▾"
            }
        }

        binding.btnAgregarCita.setOnClickListener {
            val fecha         = fechaSeleccionada
            val horaChip      = binding.chipGroupHoras.checkedChipId
            val serviciosIds  = binding.chipGroupServicios.checkedChipIds

            if (fecha == null) {
                Toast.makeText(requireContext(), "Selecciona una fecha", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (horaChip == View.NO_ID) {
                Toast.makeText(requireContext(), "Selecciona una hora", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (serviciosIds.isEmpty()) {
                Toast.makeText(requireContext(), "Selecciona al menos un servicio", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val hora      = binding.chipGroupHoras.findViewById<Chip>(horaChip).text.toString()
            val servicios = serviciosIds.map { id ->
                binding.chipGroupServicios.findViewById<Chip>(id).text.toString()
            }
            val servicio  = servicios.joinToString(" + ")
            val precio    = "${servicios.sumOf { BarberiaData.servicios[it] ?: 0 }}€"

            userViewModel.agregarCita(Cita(fecha, hora, servicio, precio))
            Toast.makeText(requireContext(), "Cita añadida", Toast.LENGTH_SHORT).show()
            resetFormulario()
        }
    }

    private fun resetFormulario() {
        fechaSeleccionada = null
        binding.calendarView.date = System.currentTimeMillis()
        binding.chipGroupHoras.clearCheck()
        for (i in 0 until binding.chipGroupServicios.childCount) {
            (binding.chipGroupServicios.getChildAt(i) as? Chip)?.apply {
                isChecked = false
                isEnabled = true
            }
        }
        binding.cardHoras.visibility = View.GONE
        binding.cardResumen.visibility = View.GONE
        binding.layoutDesglose.visibility = View.GONE
        binding.tvToggleDesglose.text = "Ver desglose ▾"
    }

    private fun cargarHoras(diaSemana: Int) {
        binding.chipGroupHoras.removeAllViews()

        if (diaSemana == Calendar.SUNDAY) {
            binding.cardHoras.visibility = View.GONE
            Toast.makeText(requireContext(), "La barbería está cerrada los domingos", Toast.LENGTH_SHORT).show()
            return
        }

        val horasBase = BarberiaData.horasPorDia(diaSemana) ?: run {
            Toast.makeText(requireContext(), "La barbería está cerrada los domingos", Toast.LENGTH_SHORT).show()
            return
        }

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
        val ahora  = Calendar.getInstance()
        val partes = fechaSeleccionada!!.split("/")

        val esHoy = partes[0].toInt() == ahora.get(Calendar.DAY_OF_MONTH) &&
                    partes[1].toInt() - 1 == ahora.get(Calendar.MONTH) &&
                    partes[2].toInt() == ahora.get(Calendar.YEAR)

        if (!esHoy) return horas

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
                ).apply { setMargins(8, 8, 8, 8) }

                // Control de máximo 2 servicios + actualizar resumen
                setOnCheckedChangeListener { _, _ ->
                    val seleccionados = binding.chipGroupServicios.checkedChipIds.size
                    for (i in 0 until binding.chipGroupServicios.childCount) {
                        val c = binding.chipGroupServicios.getChildAt(i) as? Chip
                        if (c != null && !c.isChecked) c.isEnabled = seleccionados < 2
                    }
                    actualizarResumen()
                }
            }
            binding.chipGroupServicios.addView(chip)
        }
    }

    private fun actualizarResumen() {
        val ids = binding.chipGroupServicios.checkedChipIds
        if (ids.isEmpty()) {
            binding.cardResumen.visibility = View.GONE
            return
        }

        val servicios = ids.map { id ->
            binding.chipGroupServicios.findViewById<Chip>(id).text.toString()
        }
        val total = servicios.sumOf { BarberiaData.servicios[it] ?: 0 }

        binding.cardResumen.visibility = View.VISIBLE
        binding.tvPrecioTotal.text = "${total}€"

        // Actualizar filas del desglose
        binding.contenedorDesglose.removeAllViews()
        servicios.forEach { servicio ->
            val precio = BarberiaData.servicios[servicio] ?: 0
            val fila = android.widget.LinearLayout(requireContext()).apply {
                orientation = android.widget.LinearLayout.HORIZONTAL
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                setPadding(0, 4, 0, 4)
            }
            val tvNombre = android.widget.TextView(requireContext()).apply {
                text = servicio
                textSize = 13f
                setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary_light))
                layoutParams = android.widget.LinearLayout.LayoutParams(
                    0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f
                )
            }
            val tvPrecio = android.widget.TextView(requireContext()).apply {
                text = "${precio}€"
                textSize = 13f
                setTypeface(null, android.graphics.Typeface.BOLD)
                setTextColor(ContextCompat.getColor(requireContext(), R.color.deep_purple_500))
            }
            fila.addView(tvNombre)
            fila.addView(tvPrecio)
            binding.contenedorDesglose.addView(fila)
        }
    }

    // Memory Leaks
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
