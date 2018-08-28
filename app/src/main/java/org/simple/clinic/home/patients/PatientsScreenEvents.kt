package org.simple.clinic.home.patients

import org.simple.clinic.widgets.UiEvent

class NewPatientClicked : UiEvent {
  override val analyticsName = "Patients:Search For Patient Clicked"
}

class ScanAadhaarClicked : UiEvent {
  override val analyticsName = "Patients:Scan For Aadhaar Clicked"
}

class UserApprovedStatusDismissed : UiEvent {
  override val analyticsName = "Patients:Dismissed User Approved Status"
}
