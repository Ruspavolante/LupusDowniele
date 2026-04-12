package com.lupus.game.ui.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.lupus.game.R
import com.lupus.game.databinding.FragmentGameBinding
import com.lupus.game.model.GamePhase
import com.lupus.game.viewmodel.GameViewModel

class GameFragment : Fragment() {

    private var _binding: FragmentGameBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GameViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.gameState.observe(viewLifecycleOwner) { state ->
            when (state.phase) {
                GamePhase.NIGHT_WOLVES -> findNavController().navigate(R.id.action_game_to_night_wolves)
                GamePhase.NIGHT_SEER -> findNavController().navigate(R.id.action_game_to_night_seer)
                GamePhase.DAY_VOTE -> findNavController().navigate(R.id.action_game_to_day)
                GamePhase.GAME_OVER -> findNavController().navigate(R.id.action_game_to_result)
                else -> {}
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
