package com.lupus.game.ui.day

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.lupus.game.R

public class VoteResultFragmentDirections private constructor() {
  public companion object {
    public fun actionVoteResultToNightWolves(): NavDirections =
        ActionOnlyNavDirections(R.id.action_vote_result_to_night_wolves)
  }
}
