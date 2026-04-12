package com.lupus.game.ui.result

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.lupus.game.R
import com.lupus.game.databinding.FragmentResultBinding
import com.lupus.game.model.Winner
import com.lupus.game.viewmodel.GameViewModel

class GameResultFragment : Fragment() {

    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GameViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val state = viewModel.gameState.value ?: return

        when (state.winner) {
            Winner.GOOD -> {
                binding.tvWinner.text = "🎉 I BUONI VINCONO!"
                binding.tvWinnerDesc.text = "Tutti i lupi sono stati eliminati!\nIl villaggio è salvo."
                binding.tvWinner.setTextColor(resources.getColor(R.color.good_green, null))
            }
            Winner.EVIL -> {
                binding.tvWinner.text = "🐺 I LUPI VINCONO!"
                binding.tvWinnerDesc.text = "I lupi hanno preso il controllo del villaggio!"
                binding.tvWinner.setTextColor(resources.getColor(R.color.wolf_red, null))
            }
            else -> {}
        }

        // Show all players and their roles
        val rolesSummary = state.players.joinToString("\n") { p ->
            val status = if (p.isAlive) "🟢" else "💀"
            "$status ${p.name} — ${p.role.displayName}"
        }
        binding.tvRoles.text = rolesSummary
        binding.tvRoundInfo.text = "Partita durata ${state.round} round"

        binding.btnNewGame.setOnClickListener {
            findNavController().navigate(R.id.action_result_to_setup)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
