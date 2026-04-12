package com.lupus.game.ui.night

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.lupus.game.R
import com.lupus.game.databinding.FragmentKillResultBinding
import com.lupus.game.model.GamePhase
import com.lupus.game.viewmodel.GameViewModel

class KillResultFragment : Fragment() {

    private var _binding: FragmentKillResultBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GameViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentKillResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val state = viewModel.gameState.value ?: return
        val killed = state.lastKilledByWolves

        binding.tvKillAnnouncement.text = if (killed != null)
            "🌙 Stanotte i lupi hanno ucciso:\n\n${killed.name}"
        else
            "🌙 Stanotte nessuno è morto"

        binding.btnContinue.setOnClickListener {
            val state = viewModel.gameState.value ?: return@setOnClickListener
            when (state.phase) {
                GamePhase.GAME_OVER -> findNavController().navigate(R.id.action_kill_result_to_result)
                GamePhase.DAY_VOTE  -> findNavController().navigate(R.id.action_kill_result_to_day)
                GamePhase.NIGHT_SEER -> findNavController().navigate(R.id.action_kill_result_to_night_seer)
                else -> findNavController().navigate(R.id.action_kill_result_to_day)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
