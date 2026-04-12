package com.lupus.game.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lupus.game.model.*

class GameViewModel : ViewModel() {

    val gameState = MutableLiveData<GameState>()

    fun startGame(playerNames: List<String>, wolfCount: Int, seerCount: Int) {
        val players = mutableListOf<Player>()
        val roles = mutableListOf<Role>()

        repeat(wolfCount) { roles.add(Role.WOLF) }
        repeat(seerCount) { roles.add(Role.SEER) }
        val villagerCount = playerNames.size - wolfCount - seerCount
        repeat(villagerCount) { roles.add(Role.VILLAGER) }
        roles.shuffle()

        playerNames.forEachIndexed { index, name ->
            players.add(Player(index, name, roles[index]))
        }

        gameState.value = GameState(players)
    }

    fun wolvesKill(targetId: Int) {
        val state = gameState.value ?: return
        val target = state.players.find { it.id == targetId } ?: return
        target.isAlive = false
        state.lastKilledByWolves = target
        state.phase = GamePhase.NIGHT_SEER

        val winner = state.checkWinner()
        if (winner != Winner.NONE) {
            state.winner = winner
            state.phase = GamePhase.GAME_OVER
        }
        gameState.value = state
    }

    fun skipSeer() {
        val state = gameState.value ?: return
        state.phase = GamePhase.DAY_VOTE
        gameState.value = state
    }

    fun advanceToDay() {
        val state = gameState.value ?: return
        state.phase = GamePhase.DAY_VOTE
        gameState.value = state
    }

    fun villageVote(targetId: Int) {
        val state = gameState.value ?: return
        val target = state.players.find { it.id == targetId } ?: return
        target.isAlive = false
        state.lastEliminatedByVote = target

        val winner = state.checkWinner()
        if (winner != Winner.NONE) {
            state.winner = winner
            state.phase = GamePhase.GAME_OVER
        } else {
            state.round++
            state.lastKilledByWolves = null
            state.lastEliminatedByVote = null
            state.phase = GamePhase.NIGHT_WOLVES
        }
        gameState.value = state
    }

    fun getPlayerById(id: Int): Player? =
        gameState.value?.players?.find { it.id == id }
}
