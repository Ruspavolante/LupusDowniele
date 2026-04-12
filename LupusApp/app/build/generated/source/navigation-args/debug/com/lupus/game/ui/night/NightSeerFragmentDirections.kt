package com.lupus.game.ui.night

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.lupus.game.R

public class NightSeerFragmentDirections private constructor() {
  public companion object {
    public fun actionNightSeerToDay(): NavDirections =
        ActionOnlyNavDirections(R.id.action_night_seer_to_day)
  }
}
