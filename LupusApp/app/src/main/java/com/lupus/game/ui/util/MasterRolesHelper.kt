package com.lupus.game.ui.util

import android.app.AlertDialog
import android.content.Context
import com.lupus.game.model.Player
import com.lupus.game.model.Role

fun showMasterRolesDialog(context: Context, players: List<Player>) {
    AlertDialog.Builder(context)
        .setTitle("👑 Accesso master")
        .setMessage("Stai per vedere i ruoli di tutti i giocatori.\nAssicurati che nessuno stia guardando.")
        .setPositiveButton("Mostra ruoli") { _, _ ->
            AlertDialog.Builder(context)
                .setTitle("👑 Ruoli dei giocatori")
                .setMessage(buildRolesText(players))
                .setPositiveButton("Chiudi", null)
                .show()
        }
        .setNegativeButton("Annulla", null)
        .show()
}

private fun buildRolesText(players: List<Player>): String =
    players.joinToString("\n") { p ->
        val status = if (p.isAlive) "🟢" else "💀"
        val emoji = when (p.role) {
            Role.WOLF      -> "🐺"
            Role.VILLAGER  -> "👤"
            Role.SEER      -> "🔮"
            Role.VIGILANTE -> "💥"
        }
        "$status ${p.name} — $emoji ${p.role.displayName}"
    }
