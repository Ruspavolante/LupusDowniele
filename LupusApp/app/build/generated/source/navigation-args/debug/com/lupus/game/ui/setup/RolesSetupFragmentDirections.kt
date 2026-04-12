package com.lupus.game.ui.setup

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.lupus.game.R

public class RolesSetupFragmentDirections private constructor() {
  public companion object {
    public fun actionRolesToGame(): NavDirections =
        ActionOnlyNavDirections(R.id.action_roles_to_game)
  }
}
