package com.lupus.game.ui.night

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lupus.game.R
import com.lupus.game.databinding.FragmentNightSeerBinding
import com.lupus.game.model.GamePhase
import com.lupus.game.model.GameState
import com.lupus.game.model.Player
import com.lupus.game.model.Role

class NightSeerFragment : BaseNightRoleFragment() {

    private var _binding: FragmentNightSeerBinding? = null
    private val binding get() = _binding!!

    override val tvRoleNameId = R.id.tv_seer_name
    override val rvTargetsId = R.id.rv_targets
    override val btnActionId = R.id.btn_reveal
    override val cardResultId = R.id.card_reveal
    override val tvResultId = R.id.tv_reveal_result
    override val btnContinueId = R.id.btn_continue_to_day

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNightSeerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun buildConfig(state: GameState): NightRoleConfig {
        val seer = state.players.firstOrNull { it.role == Role.SEER }
        return NightRoleConfig(
            roleOwnerName = seer?.name,
            isOwnerAlive = seer?.isAlive == true,
            canAct = true,
            headerAlive = com.lupus.game.constants.SEER_CALL+"${seer?.name}",
            headerDeadOrSpent = com.lupus.game.constants.SEER_DEAD,
            actionButtonText = com.lupus.game.constants.SEER_ACTION,
            confirmText = { target ->
                if (target.role.isEvil || target.role == Role.WENDIGO)
                    "${target.name} " + com.lupus.game.constants.SEER_CONFIRM_BAD
                else "✅ ${target.name} " + com.lupus.game.constants.SEER_CONFIRM_GOOD
            }
        )
    }

    override fun onActionConfirmed(target: Player) {
        // Il veggente non modifica lo stato, guarda solo
    }

    override fun onContinue() = viewModel.seerDone()

    override fun navigationActionFor(phase: GamePhase) = when (phase) {
        GamePhase.NIGHT_KNIGHT  -> R.id.action_night_seer_to_knight
        GamePhase.NIGHT_WOLVES  -> R.id.action_night_seer_to_wolves
        GamePhase.NIGHT_WENDIGO -> R.id.action_night_seer_to_wendigo
        GamePhase.NIGHT_BOIA    -> R.id.action_night_seer_to_boia
        GamePhase.VIGILANTE     -> R.id.action_night_seer_to_vigilante
        GamePhase.NIGHT_DEATHS  -> R.id.action_night_seer_to_night_deaths
        GamePhase.DAY_VOTE      -> R.id.action_night_seer_to_day
        GamePhase.GAME_OVER     -> R.id.action_night_seer_to_result
        else                    -> R.id.action_night_seer_to_night_deaths
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}