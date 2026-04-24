package com.lupus.game.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lupus.game.model.DeathRecord
import com.lupus.game.model.GamePhase
import com.lupus.game.model.GameState
import com.lupus.game.model.Player
import com.lupus.game.model.Role
import com.lupus.game.model.Winner

class GameViewModel : ViewModel() {

    val gameState = MutableLiveData<GameState>()

    fun startGame(playerNames: List<String>, wolfCount: Int, seerCount: Int, vigilanteCount: Int, wendigoCount: Int = 0, knightCount: Int = 0) {
        val roles = mutableListOf<Role>()
        repeat(wolfCount) { roles.add(Role.WOLF) }
        repeat(seerCount) { roles.add(Role.SEER) }
        repeat(vigilanteCount) { roles.add(Role.VIGILANTE) }
        repeat(wendigoCount) { roles.add(Role.WENDIGO) }
        repeat(knightCount) { roles.add(Role.KNIGHT) }
        repeat(playerNames.size - wolfCount - seerCount - vigilanteCount - wendigoCount - knightCount) { roles.add(Role.VILLAGER) }
        roles.shuffle()

        val players = playerNames.mapIndexed { index, name ->
            Player(index, name, roles[index])
        }.toMutableList()

        val state = GameState(players)
        // Parti dalla prima fase della coda
        state.phase = state.buildPhaseQueue().first()
        gameState.value = state
    }

    // Avanza alla prossima fase della coda, o termina il round
    private fun advancePhase(state: GameState) {
        if (state.phase == GamePhase.GAME_OVER) return
        val next = state.nextPhase()
        if (next != null) {
            if (next == GamePhase.NIGHT_DEATHS) {
                // Wolf kill: blocked if target is Wendigo or knight-protected
                val wolfTarget = state.wolfKillTargetId?.let { id -> state.players.find { it.id == id } }
                if (wolfTarget != null && wolfTarget.isAlive) {
                    val wendigoImmune = wolfTarget.role == Role.WENDIGO
                    val knightProtected = wolfTarget.id == state.knightProtectId
                    if (!wendigoImmune && !knightProtected) {
                        wolfTarget.isAlive = false
                        state.deathLog.add(DeathRecord(wolfTarget.name, state.round, isNight = true))
                    }
                }
                state.wolfKillTargetId = null
                state.knightProtectId = null
                // Vigilante / wendigo kills
                state.players.filter { it.killedInRound }.forEach { p ->
                    p.killedInRound = false
                    if (p.isAlive) {
                        p.isAlive = false
                        state.deathLog.add(DeathRecord(p.name, state.round, isNight = true))
                    }
                }
                val winner = state.checkWinner()
                if (winner != Winner.NONE) {
                    state.winner = winner
                    state.phase = GamePhase.GAME_OVER
                    return
                }
            }
            state.phase = next
        } else {
            // Fine round: ricomincia dal primo della nuova coda
            val winner = state.checkWinner()
            if (winner != Winner.NONE) {
                state.winner = winner
                state.phase = GamePhase.GAME_OVER
                return
            }
            state.round++
            state.lastKilledByWolves = null
            state.phase = state.buildPhaseQueue().first()
        }
    }

    fun wolvesKill(targetId: Int) {
        val state = gameState.value ?: return
        state.wolfKillTargetId = targetId
        state.lastKilledByWolves = state.players.find { it.id == targetId }
        advancePhase(state)
        gameState.value = state
    }

    fun vigilanteShoot(shooterId: Int, targetId: Int) {
        val state = gameState.value ?: return
        val target = state.players.find { it.id == targetId } ?: return
        val shooter = state.players.find { it.id == shooterId } ?: return
        target.killedInRound = true
        shooter.isLoaded = false


    }

    fun confirmKills() {
        val state = gameState.value ?: return
        val deadPlayers = state.players.filter { it.killedInRound }

        for (pl in deadPlayers) {
            pl.isAlive = false
        }
    }

    fun seerDone() {
        val state = gameState.value ?: return
        advancePhase(state)
        gameState.value = state
    }



    fun vigilanteDone() {
        val state = gameState.value ?: return
        advancePhase(state)
        gameState.value = state
    }

    fun knightProtect(targetId: Int) {
        val state = gameState.value ?: return
        state.knightProtectId = targetId
        gameState.value = state
    }

    fun knightDone() {
        val state = gameState.value ?: return
        advancePhase(state)
        gameState.value = state
    }

    fun wendigoAct(targetId: Int, guessedRole: Role) {
        val state = gameState.value ?: return
        val target = state.players.find { it.id == targetId }
        if (target != null && target.role == guessedRole) target.killedInRound = true
        advancePhase(state)
        gameState.value = state
    }

    fun wendigoDone() {
        val state = gameState.value ?: return
        advancePhase(state)
        gameState.value = state
    }

    fun firstPhase(): GamePhase {
        return gameState.value?.buildPhaseQueue()?.first() ?: GamePhase.NIGHT_WOLVES
    }

    fun villageVote(targetId: Int) {
        val state = gameState.value ?: return
        val target = state.players.find { it.id == targetId } ?: return
        target.isAlive = false
        state.deathLog.add(DeathRecord(target.name, state.round, isNight = false))
        state.lastEliminatedByVote = target
        advancePhase(state)
        gameState.value = state
    }

    fun nightDeathsDone() {
        val state = gameState.value ?: return
        advancePhase(state)
        gameState.value = state
    }

    fun getPlayerById(id: Int): Player? =
        gameState.value?.players?.find { it.id == id }
}