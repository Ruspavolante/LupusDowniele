package com.lupus.game.ui.setup

import android.os.Bundle
import androidx.navigation.NavDirections
import com.lupus.game.R
import kotlin.Int

public class SetupFragmentDirections private constructor() {
  private data class ActionSetupToNames(
    public val playerCount: Int = 4,
  ) : NavDirections {
    public override val actionId: Int = R.id.action_setup_to_names

    public override val arguments: Bundle
      get() {
        val result = Bundle()
        result.putInt("playerCount", this.playerCount)
        return result
      }
  }

  public companion object {
    public fun actionSetupToNames(playerCount: Int = 4): NavDirections =
        ActionSetupToNames(playerCount)
  }
}
