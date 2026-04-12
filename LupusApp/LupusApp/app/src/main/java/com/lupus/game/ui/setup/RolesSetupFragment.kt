package com.lupus.game.ui.setup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.lupus.game.R
import com.lupus.game.databinding.FragmentRolesSetupBinding
import com.lupus.game.viewmodel.GameViewModel

class RolesSetupFragment : Fragment() {

    private var _binding: FragmentRolesSetupBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GameViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRolesSetupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val names = arguments?.getStringArray("playerNames")?.toList() ?: emptyList()
        val total = names.size

        binding.tvTotalPlayers.text = "Giocatori totali: $total"

        val updateVillagers = {
            val wolves = binding.npWolves.value
            val seers = binding.npSeers.value
            val villagers = total - wolves - seers
            binding.tvVillagers.text = "Villici: ${if (villagers >= 0) villagers else "⚠️"}"
        }

        binding.npWolves.apply {
            minValue = 1
            maxValue = total - 1
            value = 1
            setOnValueChangedListener { _, _, _ -> updateVillagers() }
        }

        binding.npSeers.apply {
            minValue = 0
            maxValue = total - 1
            value = if (total >= 5) 1 else 0
            setOnValueChangedListener { _, _, _ -> updateVillagers() }
        }

        updateVillagers()

        binding.btnStartGame.setOnClickListener {
            val wolves = binding.npWolves.value
            val seers = binding.npSeers.value
            val villagers = total - wolves - seers

            if (villagers < 0) {
                Toast.makeText(requireContext(), "Troppi ruoli speciali!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (villagers == 0 && seers == 0) {
                Toast.makeText(requireContext(), "Ci deve essere almeno un non-lupo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.startGame(names, wolves, seers)
            findNavController().navigate(R.id.action_roles_to_game)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
