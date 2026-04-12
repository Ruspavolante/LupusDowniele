package com.lupus.game.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

enum class Role(val displayName: String, val isEvil: Boolean) {
    WOLF("Lupo", true),
    VILLAGER("Villico", false),
    SEER("Veggente", false)
}

enum class GamePhase {
    SETUP, NIGHT_WOLVES, NIGHT_SEER, DAY_VOTE, GAME_OVER
}

enum class Winner {
    GOOD, EVIL, NONE
}

@Parcelize
data class Player(
    val id: Int,
    val name: String,
    val role: Role,
    var isAlive: Boolean = true
) : Parcelable

data class GameState(
    val players: MutableList<Player>,
    var round: Int = 1,
    var phase: GamePhase = GamePhase.NIGHT_WOLVES,
    var lastKilledByWolves: Player? = null,
    var lastEliminatedByVote: Player? = null,
    var winner: Winner = Winner.NONE
) {
    val alivePlayers get() = players.filter { it.isAlive }
    val aliveWolves get() = players.filter { it.isAlive && it.role == Role.WOLF }
    val aliveGood get() = players.filter { it.isAlive && !it.role.isEvil }
    val aliveSeer get() = players.firstOrNull { it.isAlive && it.role == Role.SEER }

    fun checkWinner(): Winner {
        val wolves = aliveWolves.size
        val good = aliveGood.size
        return when {
            wolves == 0 -> Winner.GOOD
            wolves >= good -> Winner.EVIL
            else -> Winner.NONE
        }
    }
}
