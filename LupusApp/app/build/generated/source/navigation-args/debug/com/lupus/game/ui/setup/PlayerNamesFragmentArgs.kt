package com.lupus.game.ui.setup

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavArgs
import java.lang.IllegalArgumentException
import kotlin.Int
import kotlin.jvm.JvmStatic

public data class PlayerNamesFragmentArgs(
  public val playerCount: Int = 4,
) : NavArgs {
  public fun toBundle(): Bundle {
    val result = Bundle()
    result.putInt("playerCount", this.playerCount)
    return result
  }

  public fun toSavedStateHandle(): SavedStateHandle {
    val result = SavedStateHandle()
    result.set("playerCount", this.playerCount)
    return result
  }

  public companion object {
    @JvmStatic
    public fun fromBundle(bundle: Bundle): PlayerNamesFragmentArgs {
      bundle.setClassLoader(PlayerNamesFragmentArgs::class.java.classLoader)
      val __playerCount : Int
      if (bundle.containsKey("playerCount")) {
        __playerCount = bundle.getInt("playerCount")
      } else {
        __playerCount = 4
      }
      return PlayerNamesFragmentArgs(__playerCount)
    }

    @JvmStatic
    public fun fromSavedStateHandle(savedStateHandle: SavedStateHandle): PlayerNamesFragmentArgs {
      val __playerCount : Int?
      if (savedStateHandle.contains("playerCount")) {
        __playerCount = savedStateHandle["playerCount"]
        if (__playerCount == null) {
          throw IllegalArgumentException("Argument \"playerCount\" of type integer does not support null values")
        }
      } else {
        __playerCount = 4
      }
      return PlayerNamesFragmentArgs(__playerCount)
    }
  }
}
