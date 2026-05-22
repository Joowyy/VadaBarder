package com.example.vadabarder.ui.add

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.vadabarder.data.model.Cita
import com.example.vadabarder.databinding.FragmentAddBinding
import com.example.vadabarder.viewmodel.AuthViewModel
import com.example.vadabarder.viewmodel.CitaViewModel
import com.google.android.material.chip.Chip
import androidx.core.content.ContextCompat
import com.example.vadabarder.R
import com.example.vadabarder.data.BarberiaData
import com.example.vadabarder.data.model.AuthState
import java.util.Calendar

class AddFragment : Fragment() {

    private var _binding : FragmentAddBinding? = null
    private val binding get() = _binding!!
    private val authViewModel: AuthViewModel by activityViewModels()
    private val citaViewModel: CitaViewModel by activityViewModels()

    private var fechaSeleccionada: String? = null
    private var horasOcupadasActuales: Set<String> = emptySet()

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

        cargarServicios(BarberiaData.servicios.keys.toList())  // List<@StringRes Int>

        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            fechaSeleccionada = "%02d/%02d/%04d".format(dayOfMonth, month + 1, year)
            // Lanza el listener Firestore para los huecos de ese día
            citaViewModel.consultarHorasDe(fechaSeleccionada!!)
            val cal = Calendar.getInstance().apply { set(year, month, dayOfMonth) }
            cargarHoras(cal.get(Calendar.DAY_OF_WEEK))
        }

        // Cuando lleguen (o cambien en vivo) las horas ocupadas, refleja en los chips
        citaViewModel.horasOcupadas.observe(viewLifecycleOwner) { ocupadas ->
            horasOcupadasActuales = ocupadas
            aplicarHorasOcupadas()
        }

        binding.tvToggleDesglose.setOnClickListener {
            if (binding.layoutDesglose.visibility == View.GONE) {
                binding.layoutDesglose.visibility = View.VISIBLE
                binding.tvToggleDesglose.text = getString(R.string.btn_ocultar_desglose)
            } else {
                binding.layoutDesglose.visibility = View.GONE
                binding.tvToggleDesglose.text = getString(R.string.btn_ver_desglose)
            }
        }

        // Observador para la lista de citas.
        citaViewModel.insertState.observe(viewLifecycleOwner) { state ->
            state ?: return@observe
            when (state) {
                is AuthState.Loading -> binding.btnAgregarCita.isEnabled = false
                is AuthState.Success -> {
                    binding.btnAgregarCita.isEnabled = true
                    Toast.makeText(requireContext(), getString(R.string.msg_cita_anadida), Toast.LENGTH_SHORT).show()
                    resetFormulario()
                }
                is AuthState.Error -> {
                    binding.btnAgregarCita.isEnabled = true
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnAgregarCita.setOnClickListener {
            if (!hayConexion(requireContext())) {
                Toast.makeText(requireContext(), getString(R.string.error_sin_conexion), Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val fecha         = fechaSeleccionada
            val horaChip      = binding.chipGroupHoras.checkedChipId
            val serviciosIds  = binding.chipGroupServicios.checkedChipIds

            if (fecha == null) {
                Toast.makeText(requireContext(), getString(R.string.error_selecciona_fecha), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (horaChip == View.NO_ID) {
                Toast.makeText(requireContext(), getString(R.string.error_selecciona_hora), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (serviciosIds.isEmpty()) {
                Toast.makeText(requireContext(), getString(R.string.error_selecciona_servicio), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val hora     = binding.chipGroupHoras.findViewById<Chip>(horaChip).text.toString()
            // Recuperar resIds guardados en chip.tag para calcular precio
            val resIds   = serviciosIds.map { id ->
                (binding.chipGroupServicios.findViewById<Chip>(id).tag as? Int) ?: 0
            }
            val servicio = resIds.joinToString("||") { resources.getResourceEntryName(it) }
            val precio   = "${resIds.sumOf { BarberiaData.servicios[it] ?: 0 }}€"

            val userId = authViewModel.getCurrentUser()?.uid ?: run {
                Toast.makeText(requireContext(), getString(R.string.error_sesion), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            citaViewModel.insertar(
                Cita(userId = userId, fecha = fecha, hora = hora, servicio = servicio, precio = precio)
            )
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
        binding.tvToggleDesglose.text = getString(R.string.btn_ver_desglose)
    }

    private fun cargarHoras(diaSemana: Int) {
        binding.chipGroupHoras.removeAllViews()

        if (diaSemana == Calendar.SUNDAY) {
            binding.cardHoras.visibility = View.GONE
            Toast.makeText(requireContext(), getString(R.string.barberia_cerrada_domingos), Toast.LENGTH_SHORT).show()
            return
        }

        val horasBase = BarberiaData.horasPorDia(diaSemana) ?: run {
            Toast.makeText(requireContext(), getString(R.string.barberia_cerrada_domingos), Toast.LENGTH_SHORT).show()
            return
        }

        val horasFiltradas = filtrarHorasPasadas(horasBase)

        if (horasFiltradas.isEmpty()) {
            binding.cardHoras.visibility = View.GONE
            Toast.makeText(requireContext(), getString(R.string.sin_horas_disponibles), Toast.LENGTH_SHORT).show()
            return
        }

        binding.cardHoras.visibility = View.VISIBLE
        horasFiltradas.forEach { hora ->
            val ocupada = horasOcupadasActuales.contains(hora)
            val chip = Chip(requireContext()).apply {
                text = hora
                isCheckable = true
                isClickable = !ocupada
                isEnabled = !ocupada
                alpha = if (ocupada) 0.4f else 1f
                layoutParams = ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(8, 8, 8, 8) }
            }
            binding.chipGroupHoras.addView(chip)
        }
    }

    // Recorre los chips ya construidos y aplica el estado ocupado/libre.
    // Se usa para reaccionar a cambios en vivo del SnapshotListener.
    private fun aplicarHorasOcupadas() {
        for (i in 0 until binding.chipGroupHoras.childCount) {
            val chip = binding.chipGroupHoras.getChildAt(i) as? Chip ?: continue
            val ocupada = horasOcupadasActuales.contains(chip.text.toString())
            chip.isEnabled = !ocupada
            chip.isClickable = !ocupada
            chip.alpha = if (ocupada) 0.4f else 1f
            // Si la hora seleccionada se acaba de ocupar (otro usuario), deselecciónala
            if (ocupada && chip.isChecked) chip.isChecked = false
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

    private fun cargarServicios(servicioResIds: List<Int>) {
        binding.chipGroupServicios.removeAllViews()

        servicioResIds.forEach { resId ->
            val chip = Chip(requireContext()).apply {
                tag  = resId                    // ← clave para precio y lookup
                text = getString(resId)         // ← texto localizado
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

        // resId guardado en chip.tag → clave en BarberiaData.servicios
        val resIds = ids.map { chipId ->
            (binding.chipGroupServicios.findViewById<Chip>(chipId).tag as? Int) ?: 0
        }
        val total = resIds.sumOf { BarberiaData.servicios[it] ?: 0 }

        binding.cardResumen.visibility = View.VISIBLE
        binding.tvPrecioTotal.text = "${total}€"

        // Actualizar filas del desglose
        binding.contenedorDesglose.removeAllViews()
        resIds.forEach { resId ->
            val precio         = BarberiaData.servicios[resId] ?: 0
            val nombreLocalizado = getString(resId)
            val fila = android.widget.LinearLayout(requireContext()).apply {
                orientation = android.widget.LinearLayout.HORIZONTAL
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                setPadding(0, 4, 0, 4)
            }
            val tvNombre = android.widget.TextView(requireContext()).apply {
                text = nombreLocalizado
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

    private fun hayConexion(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    // Memory Leaks — onDestroyView (no onDestroy) para liberar el binding
    // cuando la vista se destruye pero el fragment sigue en el back stack
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
