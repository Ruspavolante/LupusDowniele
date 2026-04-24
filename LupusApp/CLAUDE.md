# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
./gradlew assembleDebug       # build debug APK
./gradlew assembleRelease     # build release APK
./gradlew build               # full build (compile + lint + test)
./gradlew clean               # clean build outputs
```

No automated tests exist in this project — there are no test source sets.

## Architecture

**MVVM + Navigation Component.** One shared `GameViewModel` (accessed via `activityViewModels()`) holds all game state in a `MutableLiveData<GameState>`. Fragments observe or read state synchronously; they never modify state directly — they call ViewModel methods which mutate `GameState` in place and re-post the LiveData.

### Phase Queue System

The core mechanic. `GameState.buildPhaseQueue()` inspects the `PHASE_ROLE_REQUIREMENT` map and returns an ordered list of `GamePhase` values (sorted by `priority: Int`) for only the roles present in the current game. `nextPhase()` finds the current phase in that queue and returns the next one. `advancePhase()` in the ViewModel calls this to transition, and handles the `NIGHT_DEATHS` block (kills resolved, winner checked) and end-of-round (round++ and restart queue).

Phase priorities: `NIGHT_SEER(10) → NIGHT_KNIGHT(15) → NIGHT_WOLVES(20) → NIGHT_WENDIGO(22) → VIGILANTE(25) → NIGHT_DEATHS(29) → DAY_VOTE(30)`

### Kill Mechanics

Two separate kill channels feed into the NIGHT_DEATHS resolution:

- **Wolf kill**: `wolvesKill()` sets `wolfKillTargetId` (not `killedInRound`). In NIGHT_DEATHS, this target is killed unless its role is `WENDIGO` (wolf immunity).
- **Vigilante / Wendigo kill**: sets `killedInRound = true` on the target. Processed unconditionally in NIGHT_DEATHS.

Neither the wolves nor the wendigo receive feedback on hit/miss — all death announcements happen in `NightDeathsFragment`.

### Night Fragment Pattern

`BaseNightRoleFragment` is the abstract base for `NightSeerFragment` and `VigilanteFragment`. It wires the master button, target RecyclerView, action button, result card, and continue button via abstract IDs. `NightWolvesFragment` and `WendigoFragment` are standalone fragments that handle their own UI manually.

Each fragment implements `navigationActionFor(phase: GamePhase): Int` to map the next phase → nav action ID.

### Win Conditions (tri-faction)

Checked in `GameState.checkWinner()` — evaluated in this order:
1. Wendigo wins if `aliveWendigo == 1 && totalAlive == 2`
2. Good wins if `aliveWolves == 0 && aliveWendigo == 0`
3. Evil wins if `aliveWolves >= aliveGood + aliveWendigo`

`aliveGood` deliberately excludes `WENDIGO` (neutral faction).

## Key Files

| File | Role |
|---|---|
| `model/Models.kt` | All enums (`Role`, `GamePhase`, `Winner`), `GameState`, `Player`, `PHASE_ROLE_REQUIREMENT` |
| `viewmodel/GameViewModel.kt` | All state transitions: `startGame`, `wolvesKill`, `wendigoAct`, `vigilanteDone`, `villageVote`, `advancePhase` |
| `constants/Constants.kt` | Display strings for night phase headers and confirmations |
| `res/navigation/nav_graph.xml` | Full fragment graph — add new phases here |
| `ui/night/BaseNightRoleFragment.kt` | Shared night UI logic (`NightRoleConfig` data class drives it) |

## Adding a New Role

1. Add to `Role` enum in `Models.kt` with `isEvil`, `description`, `winsWith`
2. Add a `GamePhase` with a priority value and entry in `PHASE_ROLE_REQUIREMENT`
3. Add the `wendigoCount`-style parameter to `startGame()` and the role shuffle
4. Create the fragment and layout (use `BaseNightRoleFragment` if it fits, standalone otherwise)
5. Register the fragment in `nav_graph.xml` with actions from/to adjacent phases
6. Add navigation cases to all `navigationActionFor()` methods in existing night fragments, `VoteResultFragment`, and `RoleRevealFragment`
7. Add the role image/color to `RoleRevealFragment` and emoji to `MasterRolesHelper`
8. Add the NumberPicker row to `fragment_roles_setup.xml` and wire it in `RolesSetupFragment`
