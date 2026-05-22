package com.example.vadabarder.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.vadabarder.R
import com.example.vadabarder.data.BarberiaData
import com.example.vadabarder.data.model.Cita
import com.example.vadabarder.databinding.FragmentHomeBinding
import com.example.vadabarder.viewmodel.AuthViewModel
import com.example.vadabarder.viewmodel.CitaViewModel
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val authViewModel: AuthViewModel by activityViewModels()
    private val citaViewModel: CitaViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Reactivo: se actualiza si Firebase rehidrata la sesión tras process death
        authViewModel.currentUser.observe(viewLifecycleOwner) { user ->
            binding.bienvenida.text = getString(R.string.bienvenida, user?.displayName ?: "")
            user?.uid?.let { uid -> citaViewModel.setUsuario(uid) }
        }

        citaViewModel.citas.observe(viewLifecycleOwner) { citas ->
            val pendientes = citas.filter { esFutura(it) }
            actualizarCitasHome(pendientes)
        }

        val verHorario   = view.findViewById<MaterialButton>(R.id.verHorario)
        val tablaHorario = view.findViewById<MaterialCardView>(R.id.cardHorario)

        verHorario.setOnClickListener {
            if (tablaHorario.visibility == View.GONE) {
                tablaHorario.visibility = View.VISIBLE
                verHorario.text = getString(R.string.btn_ocultar_horario)
            } else {
                tablaHorario.visibility = View.GONE
                verHorario.text = getString(R.string.btn_ver_horario)
            }
        }

        binding.btnLlamar.setOnClickListener {
            val intent = android.content.Intent(android.content.Intent.ACTION_DIAL)
            intent.data = android.net.Uri.parse("tel:${BarberiaData.TELEFONO}")
            startActivity(intent)
        }

        binding.btnMapa.setOnClickListener {
            val uri = android.net.Uri.parse("geo:0,0?q=${BarberiaData.DIRECCION_MAPS}")
            val mapsIntent = android.content.Intent(android.content.Intent.ACTION_VIEW, uri).apply {
                setPackage("com.google.android.apps.maps")
            }
            if (mapsIntent.resolveActivity(requireContext().packageManager) != null) {
                startActivity(mapsIntent)
            } else {
                startActivity(android.content.Intent(android.content.Intent.ACTION_VIEW, uri))
            }
        }

        construirHorario()
        construirServicios()
    }

    private fun construirHorario() {
        val contenedor = binding.contenedorHorario
        contenedor.removeAllViews()
        val entradas = BarberiaData.horarioSemana.entries.toList()

        entradas.forEachIndexed { index, (diaResId, horario) ->
            val fila = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = android.view.Gravity.CENTER_VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(0, 0, 0, dp(10)) }
            }

            val tvDia = TextView(requireContext()).apply {
                text = getString(diaResId)          // ← nombre del día localizado
                textSize = 13f
                setTypeface(ResourcesCompat.getFont(requireContext(), R.font.grotesk),
                    android.graphics.Typeface.BOLD)
                setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary_light))
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            val tvHoras = TextView(requireContext()).apply {
                textSize = 12f
                if (horario != null) {
                    text = horario
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary_light))
                } else {
                    text = getString(R.string.cerrado)
                    setTypeface(null, android.graphics.Typeface.BOLD)
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.red_500))
                }
            }

            fila.addView(tvDia)
            fila.addView(tvHoras)
            contenedor.addView(fila)

            // Divider entre filas (no después de la última)
            if (index < entradas.size - 1) {
                contenedor.addView(buildDividerHorario())
            }
        }
    }

    private fun construirServicios() {
        val contenedor = binding.contenedorServicios
        contenedor.removeAllViews()
        val populares = BarberiaData.serviciosPopulares

        populares.forEachIndexed { index, resId ->
            val precio = BarberiaData.servicios[resId] ?: 0
            val fila = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(0, 0, 0, dp(10)) }
            }

            val tvNombre = TextView(requireContext()).apply {
                text = getString(resId)             // ← nombre de servicio localizado
                textSize = 15f
                setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary_light))
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            val tvPrecio = TextView(requireContext()).apply {
                text = "${precio}€"
                textSize = 15f
                setTypeface(null, android.graphics.Typeface.BOLD)
                setTextColor(ContextCompat.getColor(requireContext(), R.color.deep_purple_500))
            }

            fila.addView(tvNombre)
            fila.addView(tvPrecio)
            contenedor.addView(fila)

            if (index < populares.size - 1) {
                contenedor.addView(buildDividerServicios())
            }
        }
    }

    private fun esFutura(cita: Cita): Boolean {
        return try {
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            sdf.parse("${cita.fecha} ${cita.hora}")?.after(Date()) ?: false
        } catch (e: Exception) { false }
    }

    private fun formatCuentaAtras(fecha: String, hora: String): String {
        return try {
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val citaDate = sdf.parse("$fecha $hora") ?: return ""
            val diffMs = citaDate.time - Date().time
            val diffHoras = TimeUnit.MILLISECONDS.toHours(diffMs)
            val diffDias = TimeUnit.MILLISECONDS.toDays(diffMs)
            when {
                diffHoras < 1  -> getString(R.string.countdown_menos_1h)
                diffHoras < 24 -> getString(R.string.countdown_horas, diffHoras.toInt())
                diffDias == 1L -> getString(R.string.countdown_manana)
                else           -> getString(R.string.countdown_dias, diffDias.toInt())
            }
        } catch (e: Exception) { "" }
    }

    private fun actualizarCitasHome(citas: List<Cita>) {
        val contenedor = binding.contenedorCitasHome
        contenedor.removeAllViews()
        if (citas.isEmpty()) {
            val texto  = getString(R.string.sin_citas_mensaje)
            val parte  = getString(R.string.parte_agenda)
            val span   = SpannableString(texto)
            val inicio = texto.indexOf(parte)
            if (inicio >= 0) {
                span.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        findNavController().navigate(R.id.addFragment)
                    }
                    override fun updateDrawState(ds: TextPaint) {
                        ds.color = ContextCompat.getColor(requireContext(), R.color.deep_purple_500)
                        ds.isUnderlineText = true
                    }
                }, inicio, texto.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            binding.tvSinCitasHome.text = span
            binding.tvSinCitasHome.movementMethod = LinkMovementMethod.getInstance()
            binding.tvSinCitasHome.visibility = View.VISIBLE
            return
        }
        binding.tvSinCitasHome.visibility = View.GONE
        citas.forEachIndexed { index, cita ->
            if (index > 0) {
                contenedor.addView(View(requireContext()).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, dp(1)
                    ).apply { setMargins(0, dp(8), 0, dp(6)) }
                    setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.orange_300))
                    alpha = 0.5f
                })
            }
            val filaTop = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }
            filaTop.addView(TextView(requireContext()).apply {
                text = BarberiaData.resolverServicio(requireContext(), cita.servicio)
                textSize = 13f
                setTypeface(null, android.graphics.Typeface.BOLD)
                setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary_light))
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            })
            filaTop.addView(TextView(requireContext()).apply {
                text = "${cita.fecha} · ${cita.hora}"
                textSize = 12f
                setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary_light))
            })
            contenedor.addView(filaTop)
            contenedor.addView(TextView(requireContext()).apply {
                text = formatCuentaAtras(cita.fecha, cita.hora)
                textSize = 11f
                setTypeface(null, android.graphics.Typeface.BOLD)
                setTextColor(ContextCompat.getColor(requireContext(), R.color.orange_700))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(0, dp(2), 0, 0) }
            })
        }
    }

    private fun buildDividerHorario() = View(requireContext()).apply {
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, dp(1)
        ).apply { setMargins(0, 0, 0, dp(10)) }
        setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.divider_light))
    }

    private fun buildDividerServicios() = View(requireContext()).apply {
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, dp(1)
        ).apply { setMargins(0, 0, 0, dp(10)) }
        setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.purple_300))
        alpha = 0.4f
    }

    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
