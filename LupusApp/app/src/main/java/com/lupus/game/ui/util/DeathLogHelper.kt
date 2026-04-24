package com.lupus.game.ui.util

import android.app.AlertDialog
import android.content.Context
import com.lupus.game.model.DeathRecord

fun showDeathLogDialog(context: Context, deathLog: List<DeathRecord>) {
    AlertDialog.Builder(context)
        .setTitle("📜 Cronologia morti")
        .setMessage(buildDeathLogText(deathLog))
        .setPositiveButton("Chiudi", null)
        .show()
}

fun buildDeathLogText(deathLog: List<DeathRecord>): String {
    if (deathLog.isEmpty()) return "Nessuna morte ancora."
    val maxRound = deathLog.maxOf { it.round }
    val sb = StringBuilder()
    for (r in 1..maxRound) {
        val nightDeaths = deathLog.filter { it.round == r && it.isNight }
        val dayDeaths   = deathLog.filter { it.round == r && !it.isNight }
        if (nightDeaths.isNotEmpty())
            sb.appendLine("🌙 Notte $r: ${nightDeaths.joinToString(", ") { it.playerName }}")
        if (dayDeaths.isNotEmpty())
            sb.appendLine("☀️ Giorno $r: ${dayDeaths.joinToString(", ") { it.playerName }}")
    }
    return sb.toString().trimEnd()
}
