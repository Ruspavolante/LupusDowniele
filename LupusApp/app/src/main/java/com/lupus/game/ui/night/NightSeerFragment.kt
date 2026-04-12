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
import com.lupus.game.databinding.FragmentNightSeerBinding
import com.lupus.game.model.GamePhase
import com.lupus.game.model.Player
import com.lupus.game.model.Role
import com.lupus.game.ui.adapters.PlayerSelectAdapter
import com.lupus.game.viewmodel.GameViewModel

class NightSeerFragment : Fragment() {

    private var _binding: FragmentNightSeerBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GameViewModel by activityViewModels()
    private var selectedPlayer: Player? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNightSeerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val state = viewModel.gameState.value ?: return
        val seer = state.players.firstOrNull { it.role == Role.SEER } // vivo O morto

        val seerIsAlive = seer?.isAlive == true

        if (seerIsAlive) {
            binding.tvSeerName.text = "Chiama il veggente:\n${seer!!.name}"
        } else {
            binding.tvSeerName.text = "Il veggente è morto — fase saltata"
        }

        // Targets: solo se il veggente è vivo
        val targets = if (seerIsAlive)
            state.alivePlayers.filter { it.id != seer!!.id }
        else
            emptyList()

        val adapter = PlayerSelectAdapter(targets) { player ->
            selectedPlayer = player
            binding.cardReveal.visibility = View.GONE
        }
        binding.rvTargets.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTargets.adapter = adapter

        // Bottone rivela: visibile solo se veggente vivo
        binding.btnReveal.visibility = if (seerIsAlive) View.VISIBLE else View.GONE

        binding.btnReveal.setOnClickListener {
            val target = selectedPlayer ?: run {
                Toast.makeText(requireContext(), "Seleziona un giocatore", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val isWolf = target.role == Role.WOLF
            binding.tvRevealResult.text = if (isWolf)
                "🐺 ${target.name} È UN LUPO!"
            else
                "✅ ${target.name} non è un lupo"
            binding.cardReveal.visibility = View.VISIBLE
            binding.tvRevealResult.setTextColor(
                if (isWolf) resources.getColor(R.color.wolf_red, null)
                else resources.getColor(R.color.good_green, null)
            )
            binding.btnReveal.visibility = View.GONE
        }

        binding.btnContinueToDay.setOnClickListener {
            viewModel.seerDone()
            navigateToCurrentPhase(viewModel.gameState.value?.phase ?: GamePhase.DAY_VOTE)
        }
    }

    private fun navigateToCurrentPhase(phase: GamePhase) {
        when (phase) {
            GamePhase.NIGHT_WOLVES -> findNavController().navigate(R.id.action_night_seer_to_wolves)
            GamePhase.DAY_VOTE -> findNavController().navigate(R.id.action_night_seer_to_day)
            GamePhase.GAME_OVER -> findNavController().navigate(R.id.action_night_seer_to_result)
            else -> findNavController().navigate(R.id.action_night_seer_to_day)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
