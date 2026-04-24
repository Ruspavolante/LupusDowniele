package com.lupus.game.ui.night

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lupus.game.R
import com.lupus.game.constants.KNIGHT_CALL
import com.lupus.game.constants.KNIGHT_CONFIRM
import com.lupus.game.constants.KNIGHT_DEAD
import com.lupus.game.constants.KNIGHT_ACTION
import com.lupus.game.databinding.FragmentKnightBinding
import com.lupus.game.model.GamePhase
import com.lupus.game.model.GameState
import com.lupus.game.model.Player
import com.lupus.game.model.Role

class KnightFragment : BaseNightRoleFragment() {

    private var _binding: FragmentKnightBinding? = null
    private val binding get() = _binding!!

    override val tvRoleNameId = R.id.tv_knight_name
    override val rvTargetsId = R.id.rv_targets
    override val btnActionId = R.id.btn_reveal
    override val cardResultId = R.id.card_reveal
    override val tvResultId = R.id.tv_reveal_result
    override val btnContinueId = R.id.btn_continue_to_day

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentKnightBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun buildConfig(state: GameState): NightRoleConfig {
        val knight = state.players.firstOrNull { it.role == Role.KNIGHT }
        return NightRoleConfig(
            roleOwnerName = knight?.name,
            isOwnerAlive = knight?.isAlive == true,
            canAct = true,
            headerAlive = KNIGHT_CALL + (knight?.name ?: ""),
            headerDeadOrSpent = KNIGHT_DEAD,
            actionButtonText = KNIGHT_ACTION,
            confirmText = { target -> "${target.name} $KNIGHT_CONFIRM" }
        )
    }

    override fun onActionConfirmed(target: Player) {
        viewModel.knightProtect(target.id)
    }

    override fun onContinue() = viewModel.knightDone()

    override fun navigationActionFor(phase: GamePhase) = when (phase) {
        GamePhase.NIGHT_WOLVES  -> R.id.action_knight_to_wolves
        GamePhase.NIGHT_WENDIGO -> R.id.action_knight_to_wendigo
        GamePhase.VIGILANTE     -> R.id.action_knight_to_vigilante
        GamePhase.NIGHT_DEATHS  -> R.id.action_knight_to_night_deaths
        GamePhase.DAY_VOTE      -> R.id.action_knight_to_day
        GamePhase.GAME_OVER     -> R.id.action_knight_to_result
        else                    -> R.id.action_knight_to_night_deaths
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
