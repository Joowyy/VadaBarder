package com.example.vadabarder.ui.profile

import androidx.recyclerview.widget.DiffUtil
import com.example.vadabarder.data.model.Cita

class CitaDiffCallback : DiffUtil.ItemCallback<Cita>() {
    override fun areItemsTheSame(oldItem: Cita, newItem: Cita): Boolean = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Cita, newItem: Cita): Boolean = oldItem == newItem
}
