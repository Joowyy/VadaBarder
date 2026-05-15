package com.example.vadabarder.ui.profile

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavOptions
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vadabarder.R
import com.example.vadabarder.data.model.Cita
import com.example.vadabarder.databinding.FragmentProfileBinding
import com.example.vadabarder.viewmodel.AuthViewModel
import com.example.vadabarder.viewmodel.CitaViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

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
            val pendientes = citas.filter { esFutura(it) }
            actualizarCitasPendientesPerfil(pendientes)
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
                diffHoras < 1  -> "En menos de 1h"
                diffHoras < 24 -> "En ~${diffHoras}h"
                diffDias == 1L -> "Mañana"
                else           -> "En ${diffDias} días"
            }
        } catch (e: Exception) { "" }
    }

    private fun actualizarCitasPendientesPerfil(citas: List<Cita>) {
        val contenedor = binding.contenedorCitasPendientesPerfil
        contenedor.removeAllViews()
        if (citas.isEmpty()) {
            val texto = "No tienes citas pendientes.\n¡Agenda una pulsando aquí!"
            val span = SpannableString(texto)
            val inicio = texto.indexOf("¡Agenda")
            span.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    findNavController().navigate(R.id.addFragment)
                }
                override fun updateDrawState(ds: TextPaint) {
                    ds.color = ContextCompat.getColor(requireContext(), R.color.deep_purple_500)
                    ds.isUnderlineText = true
                }
            }, inicio, texto.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            binding.tvSinCitasPerfil.text = span
            binding.tvSinCitasPerfil.movementMethod = LinkMovementMethod.getInstance()
            binding.tvSinCitasPerfil.visibility = View.VISIBLE
            return
        }
        binding.tvSinCitasPerfil.visibility = View.GONE
        citas.forEachIndexed { index, cita ->
            if (index > 0) {
                contenedor.addView(View(requireContext()).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, dp(1)
                    ).apply { setMargins(0, dp(10), 0, dp(10)) }
                    setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.orange_300))
                    alpha = 0.5f
                })
            }
            contenedor.addView(TextView(requireContext()).apply {
                text = cita.servicio
                textSize = 14f
                setTypeface(null, android.graphics.Typeface.BOLD)
                setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary_light))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(0, 0, 0, dp(4)) }
            })
            val filaInfo = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(0, 0, 0, dp(4)) }
            }
            filaInfo.addView(TextView(requireContext()).apply {
                text = "${cita.fecha} · ${cita.hora}"
                textSize = 13f
                setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary_light))
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            })
            filaInfo.addView(TextView(requireContext()).apply {
                text = formatCuentaAtras(cita.fecha, cita.hora)
                textSize = 12f
                setTypeface(null, android.graphics.Typeface.BOLD)
                setTextColor(ContextCompat.getColor(requireContext(), R.color.orange_700))
            })
            contenedor.addView(filaInfo)
            contenedor.addView(TextView(requireContext()).apply {
                text = cita.precio
                textSize = 16f
                setTypeface(null, android.graphics.Typeface.BOLD)
                setTextColor(ContextCompat.getColor(requireContext(), R.color.orange_500))
            })
        }
    }

    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
