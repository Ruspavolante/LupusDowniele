package com.lupus.game.ui.day

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.lupus.game.R
import com.lupus.game.databinding.FragmentVoteResultBinding
import com.lupus.game.viewmodel.GameViewModel

class VoteResultFragment : Fragment() {

    private var _binding: FragmentVoteResultBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GameViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentVoteResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val state = viewModel.gameState.value ?: return
        val eliminated = state.lastEliminatedByVote

        if (eliminated != null) {
            binding.tvVoteResult.text = "☀️ Il villaggio ha eliminato:\n\n${eliminated.name}\n\nEra: ${eliminated.role.displayName}"
        }

        binding.btnNextRound.setOnClickListener {
            findNavController().navigate(R.id.action_vote_result_to_next_round)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
