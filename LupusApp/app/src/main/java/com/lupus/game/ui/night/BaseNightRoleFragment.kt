package com.lupus.game.ui.night

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.lupus.game.R
import com.lupus.game.model.GamePhase
import com.lupus.game.model.GameState
import com.lupus.game.model.Player
import com.lupus.game.ui.adapters.PlayerSelectAdapter
import com.lupus.game.ui.util.showMasterRolesDialog
import com.lupus.game.viewmodel.GameViewModel

// Dati che ogni ruolo deve fornire alla view base
data class NightRoleConfig(
    val roleOwnerName: String?,       // nome del giocatore con quel ruolo (null = nessuno in partita)
    val isOwnerAlive: Boolean,
    val canAct: Boolean,              // ha ancora la possibilità di agire (es. giustiziere non ha sparato)
    val headerAlive: String,          // testo header quando può agire
    val headerDeadOrSpent: String,    // testo header quando non può agire
    val actionButtonText: String,     // testo bottone azione
    val confirmText: (Player) -> String  // testo da mostrare dopo azione
)

abstract class BaseNightRoleFragment : Fragment() {

    protected val viewModel: GameViewModel by activityViewModels()
    private var selectedPlayer: Player? = null

    // Ogni sottoclasse fornisce il layout e il binding
    abstract val tvRoleNameId: Int
    abstract val rvTargetsId: Int
    abstract val btnActionId: Int
    abstract val cardResultId: Int
    abstract val tvResultId: Int
    abstract val btnContinueId: Int

    // Ogni sottoclasse descrive il proprio ruolo
    abstract fun buildConfig(state: com.lupus.game.model.GameState): NightRoleConfig

    // Eseguita quando l'utente preme il bottone azione sul target selezionato
    abstract fun onActionConfirmed(target: Player)

    // Chiama viewModel per avanzare la fase
    abstract fun onContinue()

    // Mappa fase → azione di navigazione
    abstract fun navigationActionFor(phase: GamePhase): Int

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val state = viewModel.gameState.value ?: return
        val config = buildConfig(state)

        view.findViewById<android.widget.Button>(R.id.btn_master)?.setOnClickListener {
            showMasterRolesDialog(requireContext(), state.players)
        }

        val tvRoleName = view.findViewById<android.widget.TextView>(tvRoleNameId)
        val rvTargets = view.findViewById<androidx.recyclerview.widget.RecyclerView>(rvTargetsId)
        val btnAction = view.findViewById<android.widget.Button>(btnActionId)
        val cardResult = view.findViewById<androidx.cardview.widget.CardView>(cardResultId)
        val tvResult = view.findViewById<android.widget.TextView>(tvResultId)
        val btnContinue = view.findViewById<android.widget.Button>(btnContinueId)

        // Header
        val canAct = config.isOwnerAlive && config.canAct
        tvRoleName.text = if (canAct) config.headerAlive else config.headerDeadOrSpent

        // Lista target
        val targets = if (canAct)
            state.alivePlayers.filter { it.name != config.roleOwnerName }
        else
            emptyList()

        val adapter = PlayerSelectAdapter(targets) { player ->
            selectedPlayer = player
            cardResult.visibility = View.GONE
        }
        rvTargets.layoutManager = LinearLayoutManager(requireContext())
        rvTargets.adapter = adapter

        // Bottone azione
        btnAction.visibility = if (canAct) View.VISIBLE else View.GONE
        btnAction.text = config.actionButtonText

        btnAction.setOnClickListener {
            val target = selectedPlayer ?: run {
                Toast.makeText(requireContext(), "Seleziona un giocatore", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            onActionConfirmed(target)
            tvResult.text = config.confirmText(target)
            cardResult.visibility = View.VISIBLE
            btnAction.visibility = View.GONE
        }

        // Bottone continua
        btnContinue.setOnClickListener {
            onContinue()
            val nextPhase = viewModel.gameState.value?.phase ?: GamePhase.DAY_VOTE
            findNavController().navigate(navigationActionFor(nextPhase))
        }
    }
}