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
import com.lupus.game.model.GamePhase
import com.lupus.game.model.Player
import com.lupus.game.ui.adapters.PlayerSelectAdapter
import com.lupus.game.ui.util.showMasterRolesDialog
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

        binding.btnMaster.setOnClickListener {
            showMasterRolesDialog(requireContext(), state.players)
        }

        binding.tvRound.text = "Round ${state.round} — Notte"

        // Show wolves to call
        val wolves = state.aliveWolves
        binding.tvWolvesToCall.text = "Chiama i lupi:\n${wolves.joinToString(", ") { it.name }}"

        // Victims = alive players non-wolves
        val wolfIds = state.aliveWolves.map { it.id }
        val targets = state.alivePlayers.filter { player ->
            player.id !in wolfIds
        }
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
            val nextPhase = viewModel.gameState.value?.phase ?: GamePhase.DAY_VOTE
            findNavController().navigate(navigationActionFor(nextPhase))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun navigationActionFor(phase: GamePhase) = when (phase) {
        GamePhase.NIGHT_WENDIGO -> R.id.action_night_wolves_to_wendigo
        GamePhase.NIGHT_BOIA    -> R.id.action_night_wolves_to_boia
        GamePhase.NIGHT_DEATHS  -> R.id.action_night_wolves_to_night_deaths
        GamePhase.DAY_VOTE      -> R.id.action_night_wolves_to_day
        GamePhase.GAME_OVER     -> R.id.action_night_wolves_to_result
        GamePhase.VIGILANTE     -> R.id.action_night_wolves_to_vigilante
        else                    -> R.id.action_night_wolves_to_night_deaths
    }
}
