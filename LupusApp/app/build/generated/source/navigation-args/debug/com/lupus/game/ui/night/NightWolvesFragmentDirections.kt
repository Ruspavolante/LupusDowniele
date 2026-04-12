package com.lupus.game.ui.night

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.lupus.game.R

public class NightWolvesFragmentDirections private constructor() {
  public companion object {
    public fun actionNightWolvesToKillResult(): NavDirections =
        ActionOnlyNavDirections(R.id.action_night_wolves_to_kill_result)
  }
}
