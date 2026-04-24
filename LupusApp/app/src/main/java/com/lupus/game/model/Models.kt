package com.lupus.game.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

enum class Role(
    val displayName: String,
    val isEvil: Boolean,
    val description: String,
    val winsWith: String
) {
    WOLF("Lupo", true,
        "Una volta per notte scegli un bersaglio e lo mangi.",
        "Vince con i lupi."),
    VILLAGER("Villico", false,
        "Non hai poteri speciali. Durante il giorno vota per eliminare un sospetto.",
        "Vince con i buoni."),
    SEER("Veggente", false,
        "Una volta per notte puoi scoprire il ruolo di un giocatore a scelta.",
        "Vince con i buoni."),
    VIGILANTE("Giustiziere", false,
        "Una volta nella partita puoi eliminare un giocatore durante la notte.",
        "Vince con i buoni."),
    WENDIGO("Wendigo", false,
        "Ogni notte scegli un giocatore e indovina il suo ruolo. Se indovini, quel giocatore muore. I lupi non possono ucciderti di notte.",
        "Vince da solo. Deve restare in gioco con un solo altro giocatore."),
    KNIGHT("Cavaliere", false,
        "Ogni notte puoi proteggere un altro giocatore dall'attacco dei lupi. Non puoi proteggere te stesso. Giustiziere e Wendigo ignorano la tua protezione.",
        "Vince con i buoni.")
}

// Ogni fase ha una priorità — ordine crescente = ordine di esecuzione nel round
enum class GamePhase(val priority: Int) {
    SETUP(0),
    NIGHT_SEER(10),
    NIGHT_KNIGHT(15),
    NIGHT_WOLVES(20),
    NIGHT_WENDIGO(22),
    VIGILANTE(25),
    NIGHT_DEATHS(29),
    DAY_VOTE(30),
    GAME_OVER(999)
}

// Mappa: quale ruolo deve essere presente (vivo) per attivare quella fase
// null = fase sempre presente
val PHASE_ROLE_REQUIREMENT: Map<GamePhase, Role?> = mapOf(
    GamePhase.NIGHT_SEER to Role.SEER,
    GamePhase.NIGHT_KNIGHT to Role.KNIGHT,
    GamePhase.NIGHT_WOLVES to Role.WOLF,
    GamePhase.NIGHT_WENDIGO to Role.WENDIGO,
    GamePhase.VIGILANTE to Role.VIGILANTE,
    GamePhase.NIGHT_DEATHS to null,
    GamePhase.DAY_VOTE to null
)

data class DeathRecord(
    val playerName: String,
    val round: Int,
    val isNight: Boolean
)

enum class Winner {
    GOOD, EVIL, WENDIGO, NONE
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
    var winner: Winner = Winner.NONE,
    val deathLog: MutableList<DeathRecord> = mutableListOf(),
    var wolfKillTargetId: Int? = null,
    var knightProtectId: Int? = null
) {
    val alivePlayers get() = players.filter { it.isAlive }
    val aliveWolves get() = players.filter { it.isAlive && it.role == Role.WOLF }
    val aliveWendigo get() = players.filter { it.isAlive && it.role == Role.WENDIGO }
    val aliveGood get() = players.filter { it.isAlive && !it.role.isEvil && it.role != Role.WENDIGO }

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
        val wendigo = aliveWendigo.size
        val total = alivePlayers.size
        if (wendigo == 1 && total == 2) return Winner.WENDIGO
        if (wolves == 0 && wendigo == 0) return Winner.GOOD
        if (wolves >= good + wendigo) return Winner.EVIL
        return Winner.NONE
    }
}