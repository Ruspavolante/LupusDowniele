package com.lupus.game.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lupus.game.model.*

class GameViewModel : ViewModel() {

    val gameState = MutableLiveData<GameState>()

    fun startGame(playerNames: List<String>, wolfCount: Int, seerCount: Int, vigilanteCount: Int) {
        val roles = mutableListOf<Role>()
        repeat(wolfCount) { roles.add(Role.WOLF) }
        repeat(seerCount) { roles.add(Role.SEER) }
        repeat(vigilanteCount) { roles.add(Role.VIGILANTE) }
        repeat(playerNames.size - wolfCount - seerCount) { roles.add(Role.VILLAGER) }
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

        val next = state.nextPhase()
        if (next != null) {
            state.phase = next
            if (next.name.equals("DAY_VOTE"))
            {
                confirmKills()
                val winner = state.checkWinner()
                if (winner != Winner.NONE) {
                    state.winner = winner
                    state.phase = GamePhase.GAME_OVER
                    return
                }
            }
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
            state.lastEliminatedByVote = null
            state.phase = state.buildPhaseQueue().first()
        }
    }

    fun wolvesKill(targetId: Int) {
        val state = gameState.value ?: return
        val target = state.players.find { it.id == targetId } ?: return
        target.killedInRound = true
        state.lastKilledByWolves = target
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

    fun firstPhase(): GamePhase {
        return gameState.value?.buildPhaseQueue()?.first() ?: GamePhase.NIGHT_WOLVES
    }

    fun villageVote(targetId: Int) {
        val state = gameState.value ?: return
        val target = state.players.find { it.id == targetId } ?: return
        target.isAlive = false
        state.lastEliminatedByVote = target
        advancePhase(state)
        gameState.value = state
    }

    fun getPlayerById(id: Int): Player? =
        gameState.value?.players?.find { it.id == id }
}