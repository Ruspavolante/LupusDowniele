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
import com.lupus.game.databinding.FragmentVigilanteBinding
import com.lupus.game.model.GamePhase
import com.lupus.game.model.Player
import com.lupus.game.model.Role
import com.lupus.game.ui.adapters.PlayerSelectAdapter
import com.lupus.game.viewmodel.GameViewModel

class VigilanteFragment : Fragment() {

    private var _binding: FragmentVigilanteBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GameViewModel by activityViewModels()
    private var selectedPlayer: Player? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentVigilanteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val state = viewModel.gameState.value ?: return
        val vigil = state.players.firstOrNull { it.role == Role.VIGILANTE } // vivo O morto
        if (vigil?.id != null) {
            val vigilIsAlive = vigil?.isAlive == true
            val vigilHasShot = vigil?.isLoaded == true

            if (vigilIsAlive && vigilHasShot) {
                binding.tvVigilanteName.text = "Chiama il giustiziere:\n${vigil!!.name}"
            } else if (!vigilHasShot && vigilIsAlive) {
                binding.tvVigilanteName.text = "Il giustiziere ha già sparato - fase saltata"
            } else {
                binding.tvVigilanteName.text = "Il giustiziere è morto — fase saltata"
            }

            // Targets: solo se il giustiziere è vivo
            val targets = if (vigilIsAlive && vigilHasShot)
                state.alivePlayers.filter { it.id != vigil!!.id }
            else
                emptyList()

            val adapter = PlayerSelectAdapter(targets) { player ->
                selectedPlayer = player
                binding.cardReveal.visibility = View.GONE
            }
            binding.rvTargets.layoutManager = LinearLayoutManager(requireContext())
            binding.rvTargets.adapter = adapter

            // Bottone rivela: visibile solo se veggente vivo
            binding.btnReveal.visibility = if (vigilIsAlive && vigilHasShot) View.VISIBLE else View.GONE

            binding.btnReveal.setOnClickListener {
                val target = selectedPlayer ?: run {
                    Toast.makeText(requireContext(), "Seleziona un giocatore", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                }
                viewModel.vigilanteShoot(vigil.id, target.id)
                binding.tvRevealResult.text = " ${target.name} È STATO GIUSTIZIATO!"
                binding.cardReveal.visibility = View.VISIBLE
                binding.btnReveal.visibility = View.GONE
            }
        }

        binding.btnContinueToDay.setOnClickListener {
            viewModel.vigilanteDone()
            navigateToCurrentPhase(viewModel.gameState.value?.phase ?: GamePhase.DAY_VOTE)
        }
    }

    private fun navigateToCurrentPhase(phase: GamePhase) {
        when (phase) {
            GamePhase.DAY_VOTE -> findNavController().navigate(R.id.action_vigilante_to_day)
            GamePhase.GAME_OVER -> findNavController().navigate(R.id.action_vigilante_to_result)
            else -> findNavController().navigate(R.id.action_vigilante_to_day)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
