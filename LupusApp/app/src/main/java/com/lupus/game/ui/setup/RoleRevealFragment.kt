package com.lupus.game.ui.setup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.lupus.game.R
import com.lupus.game.databinding.FragmentRoleRevealBinding
import com.lupus.game.model.GamePhase
import com.lupus.game.model.Player
import com.lupus.game.model.Role
import com.lupus.game.viewmodel.GameViewModel

class RoleRevealFragment : Fragment() {

    private var _binding: FragmentRoleRevealBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GameViewModel by activityViewModels()

    private var currentIndex = 0
    private var isShowingRole = false
    private var isGameReady = false
    private var isDescriptionVisible = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRoleRevealBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val players = viewModel.gameState.value?.players ?: return

        binding.layoutPassPhone.setOnClickListener {
            if (!isShowingRole && !isGameReady) {
                isShowingRole = true
                updateUI(players)
            }
        }

        binding.btnToggleInfo.setOnClickListener {
            isDescriptionVisible = !isDescriptionVisible
            updateUI(players)
        }

        binding.btnNext.setOnClickListener {
            if (currentIndex < players.size - 1) {
                currentIndex++
                isShowingRole = false
                isDescriptionVisible = false
            } else {
                isGameReady = true
                isShowingRole = false
            }
            updateUI(players)
        }

        binding.layoutMaster.setOnClickListener {
            val action = when (viewModel.firstPhase()) {
                GamePhase.NIGHT_SEER -> R.id.action_reveal_to_seer
                else                 -> R.id.action_reveal_to_wolves
            }
            findNavController().navigate(action)
        }

        updateUI(players)
    }

    private fun updateUI(players: List<Player>) {
        when {
            isGameReady -> {
                binding.layoutPassPhone.visibility = View.GONE
                binding.layoutRole.visibility = View.GONE
                binding.layoutMaster.visibility = View.VISIBLE
            }
            isShowingRole -> {
                binding.layoutPassPhone.visibility = View.GONE
                binding.layoutRole.visibility = View.VISIBLE
                binding.layoutMaster.visibility = View.GONE

                val player = players[currentIndex]
                binding.ivRoleImage.setImageResource(imageForRole(player.role))
                binding.tvRoleName.text = player.role.displayName
                binding.tvRoleName.setTextColor(ContextCompat.getColor(requireContext(), colorForRole(player.role)))
                binding.tvDescription.text = player.role.description
                binding.tvWinsWith.text = player.role.winsWith

                if (isDescriptionVisible) {
                    binding.layoutDescription.visibility = View.VISIBLE
                    binding.btnToggleInfo.text = "✖ Nascondi info"
                } else {
                    binding.layoutDescription.visibility = View.GONE
                    binding.btnToggleInfo.text = "ℹ️ Mostra info ruolo"
                }
            }
            else -> {
                binding.layoutPassPhone.visibility = View.VISIBLE
                binding.layoutRole.visibility = View.GONE
                binding.layoutMaster.visibility = View.GONE

                val name = players[currentIndex].name
                binding.tvPassInstruction.text = "Passa il telefono a\n$name"
            }
        }
    }

    private fun imageForRole(role: Role) = when (role) {
        Role.WOLF      -> R.drawable.ic_role_wolf
        Role.VILLAGER  -> R.drawable.ic_role_villager
        Role.SEER      -> R.drawable.ic_role_seer
        Role.VIGILANTE -> R.drawable.ic_role_vigilante
    }

    private fun colorForRole(role: Role) = when (role) {
        Role.WOLF      -> R.color.wolf_red
        Role.VILLAGER  -> R.color.villager_blue
        Role.SEER      -> R.color.seer_purple
        Role.VIGILANTE -> R.color.vigilante_red
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
