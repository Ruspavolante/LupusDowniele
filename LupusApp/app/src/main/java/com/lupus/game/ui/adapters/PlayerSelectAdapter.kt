package com.lupus.game.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lupus.game.databinding.ItemPlayerSelectBinding
import com.lupus.game.model.Player

class PlayerSelectAdapter(
    private val players: List<Player>,
    private val onSelect: (Player) -> Unit
) : RecyclerView.Adapter<PlayerSelectAdapter.VH>() {

    private var selectedId: Int = -1

    inner class VH(val binding: ItemPlayerSelectBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemPlayerSelectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val player = players[position]
        val isSelected = player.id == selectedId
        holder.binding.tvPlayerName.text = player.name
        holder.binding.root.isSelected = isSelected
        holder.binding.ivSelected.visibility = if (isSelected) View.VISIBLE else View.GONE
        holder.binding.root.setOnClickListener {
            val prev = selectedId
            selectedId = player.id
            val prevIdx = players.indexOfFirst { it.id == prev }
            if (prevIdx >= 0) notifyItemChanged(prevIdx)
            notifyItemChanged(position)
            onSelect(player)
        }
    }

    override fun getItemCount() = players.size
}
