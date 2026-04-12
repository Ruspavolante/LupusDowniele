# Lupus in Fabula — App Android

App per il Master per gestire partite di Lupus in Fabula in locale.

## Come aprire il progetto in Android Studio

1. Apri **Android Studio** (Hedgehog o più recente)
2. Seleziona **"Open"** e naviga nella cartella `LupusApp`
3. Attendi il sync di Gradle (prima volta richiede qualche minuto)
4. Collega un dispositivo Android (API 24+) o avvia un emulatore
5. Premi **Run ▶**

## Struttura del progetto

```
app/src/main/
├── java/com/lupus/game/
│   ├── model/
│   │   └── Models.kt           ← Player, Role, GameState, enums
│   ├── viewmodel/
│   │   └── GameViewModel.kt    ← Logica di gioco centralizzata
│   ├── ui/
│   │   ├── setup/
│   │   │   ├── SetupFragment.kt         ← Numero giocatori
│   │   │   ├── PlayerNamesFragment.kt   ← Inserimento nomi
│   │   │   └── RolesSetupFragment.kt    ← Scelta lupi/veggenti
│   │   ├── night/
│   │   │   ├── NightWolvesFragment.kt   ← Lupi scelgono vittima
│   │   │   ├── KillResultFragment.kt    ← Annuncio vittima
│   │   │   └── NightSeerFragment.kt     ← Veggente investiga
│   │   ├── day/
│   │   │   ├── DayVoteFragment.kt       ← Voto villaggio
│   │   │   └── VoteResultFragment.kt    ← Annuncio eliminato
│   │   ├── result/
│   │   │   └── GameResultFragment.kt    ← Fine partita
│   │   └── adapters/
│   │       └── PlayerSelectAdapter.kt   ← RecyclerView selezione
│   └── MainActivity.kt
└── res/
    ├── layout/         ← Tutti i layout XML
    ├── navigation/
    │   └── nav_graph.xml   ← Grafo di navigazione
    ├── drawable/       ← Sfondi, bottoni, icone
    ├── values/
    │   ├── colors.xml
    │   ├── strings.xml
    │   └── themes.xml
    └── anim/           ← Animazioni transizione
```

## Flusso di gioco

```
Setup → Nomi → Ruoli → [ROUND] → Notte (Lupi) → Annuncio vittima
                                               → Notte (Veggente) → Giorno (Voto)
                                                                  → Annuncio eliminato
                                                                  → Check vittoria
                                                                  → Nuovo round / Fine
```

## Condizioni di vittoria

- **I Buoni vincono** quando tutti i lupi sono eliminati
- **I Lupi vincono** quando il numero di lupi è ≥ al numero di buoni vivi

## Dipendenze principali

- AndroidX Navigation Component 2.7.6
- Material Components 1.11.0
- ViewModel + LiveData (Lifecycle 2.7.0)
- ViewBinding abilitato

## Note

- L'app è pensata per uso esclusivo del **Master** — i ruoli non vengono mai mostrati ai giocatori durante il setup
- Durante la fase notte, ogni giocatore non deve vedere lo schermo degli altri
- Il veggente vede solo se il giocatore ispezionato è un lupo o no (non il ruolo esatto)
