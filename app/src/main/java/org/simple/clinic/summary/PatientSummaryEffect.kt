package org.simple.clinic.summary

import org.simple.clinic.patient.businessid.Identifier
import org.threeten.bp.Instant
import java.util.UUID

sealed class PatientSummaryEffect

data class LoadPatientSummaryProfile(val patientUuid: UUID) : PatientSummaryEffect()

// TODO(vs): 2020-01-15 Revisit this effect once the patient summary migration is done
data class HandleBackClick(
    val patientUuid: UUID,
    val screenCreatedTimestamp: Instant,
    val openIntention: OpenIntention
) : PatientSummaryEffect()

// TODO(vs): 2020-01-16 Revisit this effect once the patient summary migration is done
data class HandleDoneClick(
    val patientUuid: UUID
) : PatientSummaryEffect()

object LoadCurrentFacility : PatientSummaryEffect()

data class HandleEditClick(val patientSummaryProfile: PatientSummaryProfile) : PatientSummaryEffect()

object HandleLinkIdCancelled: PatientSummaryEffect()

object GoBackToPreviousScreen: PatientSummaryEffect()

object GoToHomeScreen: PatientSummaryEffect()

data class CheckForInvalidPhone(val patientUuid: UUID): PatientSummaryEffect()

data class FetchHasShownMissingPhoneReminder(val patientUuid: UUID): PatientSummaryEffect()

data class MarkReminderAsShown(val patientUuid: UUID): PatientSummaryEffect()

data class ShowAddPhonePopup(val patientUuid: UUID): PatientSummaryEffect()

data class ShowLinkIdWithPatientView(val patientUuid: UUID, val identifier: Identifier): PatientSummaryEffect()

object HideLinkIdWithPatientView : PatientSummaryEffect()

data class ReportViewedPatientToAnalytics(val patientUuid: UUID, val openIntention: OpenIntention): PatientSummaryEffect()
