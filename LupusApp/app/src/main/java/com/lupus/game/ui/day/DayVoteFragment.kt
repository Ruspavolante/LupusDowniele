package com.lupus.game.ui.day

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
import com.lupus.game.databinding.FragmentDayVoteBinding
import com.lupus.game.model.Player
import com.lupus.game.ui.adapters.PlayerSelectAdapter
import com.lupus.game.ui.util.showDeathLogDialog
import com.lupus.game.viewmodel.GameViewModel

class DayVoteFragment : Fragment() {

    private var _binding: FragmentDayVoteBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GameViewModel by activityViewModels()
    private var selectedPlayer: Player? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDayVoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val state = viewModel.gameState.value ?: return
        binding.tvDayRound.text = "Round ${state.round} — Giorno ☀️"
        binding.tvAlivePlayers.text = "Giocatori vivi: ${state.alivePlayers.size}"

        val adapter = PlayerSelectAdapter(state.alivePlayers) { player ->
            selectedPlayer = player
        }
        binding.rvVotePlayers.layoutManager = LinearLayoutManager(requireContext())
        binding.rvVotePlayers.adapter = adapter

        binding.btnDeathLog.setOnClickListener {
            showDeathLogDialog(requireContext(), state.deathLog)
        }

        binding.btnEliminate.setOnClickListener {
            val target = selectedPlayer
            if (target == null) {
                Toast.makeText(requireContext(), "Seleziona chi eliminare", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.villageVote(target.id)

            val newState = viewModel.gameState.value
            if (newState?.phase == com.lupus.game.model.GamePhase.GAME_OVER) {
                findNavController().navigate(R.id.action_day_to_result)
            } else {
                findNavController().navigate(R.id.action_day_to_vote_result)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
