package com.lupus.game.ui.night

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.lupus.game.R
import com.lupus.game.constants.BOIA_CALL
import com.lupus.game.constants.BOIA_DEAD
import com.lupus.game.constants.BOIA_SPENT
import com.lupus.game.databinding.FragmentBoiaBinding
import com.lupus.game.model.GamePhase
import com.lupus.game.model.Player
import com.lupus.game.model.Role
import com.lupus.game.ui.adapters.PlayerSelectAdapter
import com.lupus.game.ui.util.showMasterRolesDialog
import com.lupus.game.viewmodel.GameViewModel

class BoiaFragment : Fragment() {

    private var _binding: FragmentBoiaBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GameViewModel by activityViewModels()
    private var selectedTarget: Player? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBoiaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val state = viewModel.gameState.value ?: return
        val boia = state.players.firstOrNull { it.role == Role.BOIA }
        val isAlive = boia?.isAlive == true

        binding.btnMaster.setOnClickListener {
            showMasterRolesDialog(requireContext(), state.players)
        }

        if (!isAlive) {
            binding.tvBoiaHeader.text = BOIA_DEAD
            binding.layoutAction.visibility = View.GONE
            binding.btnContinueDead.visibility = View.VISIBLE
            binding.btnContinueDead.setOnClickListener { navigateNext() }
            return
        }

        val hasActed = boia?.isLoaded == false
        if (hasActed) {
            binding.tvBoiaHeader.text = BOIA_SPENT
            binding.layoutAction.visibility = View.GONE
            binding.btnContinueDead.visibility = View.VISIBLE
            binding.btnContinueDead.setOnClickListener { navigateNext() }
            return
        }

        binding.tvBoiaHeader.text = BOIA_CALL + (boia?.name ?: "")

        val targets = state.alivePlayers.filter { it.id != boia?.id }
        val adapter = PlayerSelectAdapter(targets) { player -> selectedTarget = player }
        binding.rvTargets.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTargets.adapter = adapter

        val roles = state.players.map { it.role }.distinct()
        roles.forEach { role ->
            val rb = RadioButton(requireContext()).apply {
                text = role.displayName
                setTextColor(resources.getColor(R.color.text_primary, null))
                textSize = 16f
                tag = role
            }
            binding.rgRoles.addView(rb)
        }

        binding.btnBoiaAct.setOnClickListener {
            val target = selectedTarget
            if (target == null) {
                Toast.makeText(requireContext(), "Seleziona un bersaglio", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val selectedRbId = binding.rgRoles.checkedRadioButtonId
            if (selectedRbId == -1) {
                Toast.makeText(requireContext(), "Indovina il ruolo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val guessedRole = binding.rgRoles.findViewById<RadioButton>(selectedRbId).tag as Role
            viewModel.boiaAct(boia!!.id, target.id, guessedRole)
            navigateNext()
        }

        binding.btnBoiaSkip.setOnClickListener {
            viewModel.boiaDone()
            navigateNext()
        }
    }

    private fun navigateNext() {
        val phase = viewModel.gameState.value?.phase ?: GamePhase.NIGHT_DEATHS
        findNavController().navigate(navigationActionFor(phase))
    }

    private fun navigationActionFor(phase: GamePhase) = when (phase) {
        GamePhase.VIGILANTE   -> R.id.action_boia_to_vigilante
        GamePhase.GAME_OVER   -> R.id.action_boia_to_result
        else                  -> R.id.action_boia_to_night_deaths
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
