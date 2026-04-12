package com.lupus.game.ui.day

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.lupus.game.R

public class DayVoteFragmentDirections private constructor() {
  public companion object {
    public fun actionDayToVoteResult(): NavDirections =
        ActionOnlyNavDirections(R.id.action_day_to_vote_result)

    public fun actionDayToResult(): NavDirections =
        ActionOnlyNavDirections(R.id.action_day_to_result)
  }
}
