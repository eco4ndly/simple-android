package org.simple.clinic.bp.history

import com.spotify.mobius.test.NextMatchers.hasEffects
import com.spotify.mobius.test.NextMatchers.hasModel
import com.spotify.mobius.test.NextMatchers.hasNoEffects
import com.spotify.mobius.test.NextMatchers.hasNoModel
import com.spotify.mobius.test.UpdateSpec
import com.spotify.mobius.test.UpdateSpec.assertThatNext
import org.junit.Test
import org.simple.clinic.patient.PatientMocker
import java.util.UUID

class BloodPressureHistoryScreenUpdateTest {
  private val patientUuid = UUID.fromString("6b455aea-1580-4900-9524-c19a4d3db676")
  private val model = BloodPressureHistoryScreenModel.create(patientUuid)
  private val updateSpec = UpdateSpec<BloodPressureHistoryScreenModel, BloodPressureHistoryScreenEvent, BloodPressureHistoryScreenEffect>(BloodPressureHistoryScreenUpdate())

  @Test
  fun `when patient is loaded, then show patient information`() {
    val patient = PatientMocker.patient(uuid = UUID.fromString("7d04777d-3480-451b-b571-0e114c87cebf"))

    updateSpec
        .given(model)
        .whenEvent(PatientLoaded(patient))
        .then(
            assertThatNext(
                hasModel(model.patientLoaded(patient)),
                hasNoEffects()
            )
        )
  }

  @Test
  fun `when blood pressure history is loaded, then show blood pressures`() {
    val bloodPressure1 = PatientMocker.bp(UUID.fromString("8815d0fc-73cc-44a2-a4b3-473c4c0989aa"))
    val bloodPressure2 = PatientMocker.bp(UUID.fromString("ddf87db7-1034-4618-bc0e-879d7d357adf"))
    val bloodPressures = listOf(bloodPressure1, bloodPressure2)

    updateSpec
        .given(model)
        .whenEvent(BloodPressureHistoryLoaded(bloodPressures))
        .then(
            assertThatNext(
                hasModel(model.historyLoaded(bloodPressures)),
                hasNoEffects()
            )
        )
  }

  @Test
  fun `when add new blood pressure is clicked, then open entry sheet`() {
    updateSpec
        .given(model)
        .whenEvent(NewBloodPressureClicked)
        .then(
            assertThatNext(
                hasNoModel(),
                hasEffects(OpenBloodPressureEntrySheet(patientUuid) as BloodPressureHistoryScreenEffect)
            )
        )
  }

  @Test
  fun `when blood pressure is clicked, then open update sheet`() {
    val bloodPressureMeasurement = PatientMocker.bp(
        UUID.fromString("e42d25fb-5693-449c-9ad2-71a172eb8d92"),
        patientUuid
    )
    val bloodPressures = listOf(bloodPressureMeasurement)

    updateSpec
        .given(model.historyLoaded(bloodPressures))
        .whenEvent(BloodPressureClicked(bloodPressureMeasurement))
        .then(
            assertThatNext(
                hasNoModel(),
                hasEffects(OpenBloodPressureUpdateSheet(bloodPressureMeasurement) as BloodPressureHistoryScreenEffect)
            )
        )
  }
}
