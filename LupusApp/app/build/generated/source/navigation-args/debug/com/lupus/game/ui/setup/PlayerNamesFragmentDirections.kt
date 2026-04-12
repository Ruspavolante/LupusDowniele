package com.lupus.game.ui.setup

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.lupus.game.R

public class PlayerNamesFragmentDirections private constructor() {
  public companion object {
    public fun actionNamesToRoles(): NavDirections =
        ActionOnlyNavDirections(R.id.action_names_to_roles)
  }
}
