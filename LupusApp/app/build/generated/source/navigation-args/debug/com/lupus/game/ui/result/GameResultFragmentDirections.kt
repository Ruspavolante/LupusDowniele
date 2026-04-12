package com.lupus.game.ui.result

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.lupus.game.R

public class GameResultFragmentDirections private constructor() {
  public companion object {
    public fun actionResultToSetup(): NavDirections =
        ActionOnlyNavDirections(R.id.action_result_to_setup)
  }
}
