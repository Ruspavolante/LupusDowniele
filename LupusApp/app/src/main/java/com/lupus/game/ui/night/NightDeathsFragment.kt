package com.lupus.game.ui.night

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.lupus.game.R
import com.lupus.game.databinding.FragmentNightDeathsBinding
import com.lupus.game.model.GamePhase
import com.lupus.game.viewmodel.GameViewModel

class NightDeathsFragment : Fragment() {

    private var _binding: FragmentNightDeathsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GameViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNightDeathsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val state = viewModel.gameState.value ?: return
        binding.tvNightRound.text = "Notte ${state.round}"

        val nightDeaths = state.deathLog.filter { it.round == state.round && it.isNight }

        if (nightDeaths.isEmpty()) {
            binding.tvNoDeaths.visibility = View.VISIBLE
            binding.cardDeaths.visibility = View.GONE
        } else {
            binding.tvNoDeaths.visibility = View.GONE
            binding.cardDeaths.visibility = View.VISIBLE
            nightDeaths.forEach { death ->
                val tv = TextView(requireContext()).apply {
                    text = "💀 ${death.playerName}"
                    textSize = 20f
                    setTextColor(resources.getColor(R.color.text_primary, null))
                    setPadding(0, 12, 0, 12)
                }
                binding.llDeathsList.addView(tv)
            }
        }

        binding.btnContinueDay.setOnClickListener {
            viewModel.nightDeathsDone()
            val phase = viewModel.gameState.value?.phase ?: GamePhase.DAY_VOTE
            findNavController().navigate(
                if (phase == GamePhase.GAME_OVER) R.id.action_night_deaths_to_result
                else R.id.action_night_deaths_to_day
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
