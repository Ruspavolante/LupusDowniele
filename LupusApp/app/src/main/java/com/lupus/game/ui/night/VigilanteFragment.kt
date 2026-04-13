package com.lupus.game.ui.night

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lupus.game.R
import com.lupus.game.databinding.FragmentVigilanteBinding
import com.lupus.game.model.GamePhase
import com.lupus.game.model.GameState
import com.lupus.game.model.Player
import com.lupus.game.model.Role

class VigilanteFragment : BaseNightRoleFragment() {

    private var _binding: FragmentVigilanteBinding? = null
    private val binding get() = _binding!!

    override val tvRoleNameId = R.id.tv_vigilante_name
    override val rvTargetsId = R.id.rv_targets
    override val btnActionId = R.id.btn_reveal
    override val cardResultId = R.id.card_reveal
    override val tvResultId = R.id.tv_reveal_result
    override val btnContinueId = R.id.btn_continue_to_day

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentVigilanteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun buildConfig(state: GameState): NightRoleConfig {
        val vigil = state.players.firstOrNull { it.role == Role.VIGILANTE }
        val isAlive = vigil?.isAlive == true
        val hasShot = vigil?.isLoaded == false  // isLoaded=false significa ha già sparato
        return NightRoleConfig(
            roleOwnerName = vigil?.name,
            isOwnerAlive = isAlive,
            canAct = isAlive && !hasShot,
            headerAlive = com.lupus.game.constants.VIGILANTE_CALL+"${vigil?.name}",
            headerDeadOrSpent = if (!isAlive) com.lupus.game.constants.VIGILANTE_DEAD
            else com.lupus.game.constants.VIGILANTE_SHOT,
            actionButtonText = com.lupus.game.constants.VIGILANTE_ACTION,
            confirmText = { target -> "${target.name} " + com.lupus.game.constants.VIGILANTE_CONFIRM }
        )
    }

    override fun onActionConfirmed(target: Player) {
        val vigilId = viewModel.gameState.value
            ?.players?.firstOrNull { it.role == Role.VIGILANTE }?.id ?: return
        viewModel.vigilanteShoot(vigilId, target.id)
    }

    override fun onContinue() = viewModel.vigilanteDone()

    override fun navigationActionFor(phase: GamePhase) = when (phase) {
        GamePhase.DAY_VOTE  -> R.id.action_vigilante_to_day
        GamePhase.GAME_OVER -> R.id.action_vigilante_to_result
        else                -> R.id.action_vigilante_to_day
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}