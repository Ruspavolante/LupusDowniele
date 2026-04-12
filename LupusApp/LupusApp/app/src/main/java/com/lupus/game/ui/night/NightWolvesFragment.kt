package com.lupus.game.ui.night

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.lupus.game.R
import com.lupus.game.databinding.FragmentNightWolvesBinding
import com.lupus.game.model.Player
import com.lupus.game.ui.adapters.PlayerSelectAdapter
import com.lupus.game.viewmodel.GameViewModel

class NightWolvesFragment : Fragment() {

    private var _binding: FragmentNightWolvesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GameViewModel by activityViewModels()
    private var selectedTarget: Player? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNightWolvesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val state = viewModel.gameState.value ?: return
        binding.tvRound.text = "Round ${state.round} — Notte"

        // Show wolves to call
        val wolves = state.aliveWolves
        binding.tvWolvesToCall.text = "Chiama i lupi:\n${wolves.joinToString(", ") { it.name }}"

        // Victims = alive players (wolves can pick anyone alive for dramatic effect, or restrict to non-wolves)
        val targets = state.alivePlayers
        val adapter = PlayerSelectAdapter(targets) { player ->
            selectedTarget = player
        }
        binding.rvTargets.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTargets.adapter = adapter

        binding.btnConfirmKill.setOnClickListener {
            val target = selectedTarget
            if (target == null) {
                Toast.makeText(requireContext(), "Seleziona una vittima", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.wolvesKill(target.id)
            findNavController().navigate(R.id.action_night_wolves_to_kill_result)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
