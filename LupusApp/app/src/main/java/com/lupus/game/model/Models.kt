package com.lupus.game.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

enum class Role(val displayName: String, val isEvil: Boolean) {
    WOLF("Lupo", true),
    VILLAGER("Villico", false),
    SEER("Veggente", false),
    VIGILANTE("Giustiziere", false)
}

// Ogni fase ha una priorità — ordine crescente = ordine di esecuzione nel round
enum class GamePhase(val priority: Int) {
    SETUP(0),
    NIGHT_SEER(10),
    NIGHT_WOLVES(20),
    VIGILANTE(25),
    DAY_VOTE(30),
    GAME_OVER(999)
}

// Mappa: quale ruolo deve essere presente (vivo) per attivare quella fase
// null = fase sempre presente
val PHASE_ROLE_REQUIREMENT: Map<GamePhase, Role?> = mapOf(
    GamePhase.NIGHT_SEER to Role.SEER,
    GamePhase.VIGILANTE to Role.VIGILANTE,
    GamePhase.NIGHT_WOLVES to Role.WOLF,
    GamePhase.DAY_VOTE to null
)

enum class Winner {
    GOOD, EVIL, NONE
}

@Parcelize
data class Player(
    val id: Int,
    val name: String,
    val role: Role,
    var isAlive: Boolean = true,
    var isLoaded: Boolean = true,
    var killedInRound: Boolean = false
) : Parcelable

data class GameState(
    val players: MutableList<Player>,
    var round: Int = 1,
    var phase: GamePhase = GamePhase.NIGHT_SEER,
    var lastKilledByWolves: Player? = null,
    var lastEliminatedByVote: Player? = null,
    var winner: Winner = Winner.NONE
) {
    val alivePlayers get() = players.filter { it.isAlive }
    val aliveWolves get() = players.filter { it.isAlive && it.role == Role.WOLF }
    val aliveGood get() = players.filter { it.isAlive && !it.role.isEvil }

    // Costruisce la lista ordinata delle fasi attive per questo round
    // basandosi sui ruoli ancora vivi
    fun buildPhaseQueue(): List<GamePhase> {
        print(players)
        return PHASE_ROLE_REQUIREMENT.entries
            .filter { (_, requiredRole) ->
                requiredRole == null || players.any { it.role == requiredRole }
            }
            .map { it.key }
            .sortedBy { it.priority }
    }

    // Dato la fase corrente, restituisce la prossima nella coda
    // Se non c'è una prossima, il round è finito
    fun nextPhase(): GamePhase? {
        val queue = buildPhaseQueue()
        val currentIndex = queue.indexOf(phase)
        return queue.getOrNull(currentIndex + 1)
    }

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