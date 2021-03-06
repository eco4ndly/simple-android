package org.simple.clinic.registration.location

import org.simple.clinic.util.RuntimePermissionResult
import org.simple.clinic.widgets.UiEvent

data class RegistrationLocationPermissionChanged(val result: RuntimePermissionResult) : UiEvent {
  override val analyticsName = "Registration:Location Permission:$result"
}
