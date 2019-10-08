package org.simple.clinic.settings

import com.spotify.mobius.Next
import com.spotify.mobius.Next.next
import com.spotify.mobius.Update

class SettingsUpdate : Update<SettingsModel, SettingsEvent, SettingsEffect> {

  override fun update(model: SettingsModel, event: SettingsEvent): Next<SettingsModel, SettingsEffect> {
    return when (event) {
      is UserDetailsLoaded -> next(model.userDetailsFetched(name = event.name, phoneNumber = event.phoneNumber))
    }
  }
}