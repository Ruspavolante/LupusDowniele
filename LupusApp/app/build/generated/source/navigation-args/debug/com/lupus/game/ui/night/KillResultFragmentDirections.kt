package com.lupus.game.ui.night

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.lupus.game.R

public class KillResultFragmentDirections private constructor() {
  public companion object {
    public fun actionKillResultToNightSeer(): NavDirections =
        ActionOnlyNavDirections(R.id.action_kill_result_to_night_seer)

    public fun actionKillResultToDay(): NavDirections =
        ActionOnlyNavDirections(R.id.action_kill_result_to_day)

    public fun actionKillResultToResult(): NavDirections =
        ActionOnlyNavDirections(R.id.action_kill_result_to_result)
  }
}
