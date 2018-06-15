package org.resolvetosavelives.red.bp

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.toObservable
import org.resolvetosavelives.red.bp.sync.BloodPressureMeasurementPayload
import org.resolvetosavelives.red.di.AppScope
import org.resolvetosavelives.red.facility.FacilityRepository
import org.resolvetosavelives.red.patient.SyncStatus
import org.resolvetosavelives.red.patient.canBeOverriddenByServerCopy
import org.resolvetosavelives.red.user.UserSession
import org.threeten.bp.Instant
import java.util.UUID
import javax.inject.Inject

@AppScope
class BloodPressureRepository @Inject constructor(
    private val dao: BloodPressureMeasurement.RoomDao,
    private val userSession: UserSession,
    private val facilityRepository: FacilityRepository
) {

  fun saveMeasurement(patientUuid: UUID, systolic: Int, diastolic: Int): Completable {
    if (systolic < 0 || diastolic < 0) {
      throw AssertionError("Cannot have negative BP readings.")
    }

    val loggedInUser = userSession
        .loggedInUser()
        .take(1)

    val currentFacility = facilityRepository
        .currentFacility()
        .take(1)

    return Observables.combineLatest(loggedInUser, currentFacility)
        .flatMapCompletable { (user, facility) ->
          Completable.fromAction {
            val newMeasurement = BloodPressureMeasurement(
                uuid = UUID.randomUUID(),
                systolic = systolic,
                diastolic = diastolic,
                syncStatus = SyncStatus.PENDING,
                patientUuid = patientUuid,
                facilityUuid = facility.uuid,
                userUuid = user.uuid,
                createdAt = Instant.now(),
                updatedAt = Instant.now())
            dao.save(listOf(newMeasurement))
          }
        }
  }

  fun measurementsWithSyncStatus(status: SyncStatus): Single<List<BloodPressureMeasurement>> {
    return dao
        .withSyncStatus(status)
        .firstOrError()
  }

  fun updateMeasurementsSyncStatus(oldStatus: SyncStatus, newStatus: SyncStatus): Completable {
    return Completable.fromAction {
      dao.updateSyncStatus(oldStatus = oldStatus, newStatus = newStatus)
    }
  }

  fun updateMeasurementsSyncStatus(measurementUuids: List<UUID>, newStatus: SyncStatus): Completable {
    if (measurementUuids.isEmpty()) {
      throw AssertionError()
    }
    return Completable.fromAction {
      dao.updateSyncStatus(uuids = measurementUuids, newStatus = newStatus)
    }
  }

  fun mergeWithLocalData(serverPayloads: List<BloodPressureMeasurementPayload>): Completable {
    return serverPayloads
        .toObservable()
        .filter { payload ->
          val localCopy = dao.getOne(payload.uuid)
          localCopy?.syncStatus.canBeOverriddenByServerCopy()
        }
        .map { it.toDatabaseModel(SyncStatus.DONE) }
        .toList()
        .flatMapCompletable { Completable.fromAction { dao.save(it) } }
  }

  fun measurementCount(): Single<Int> {
    return dao.measurementCount().firstOrError()
  }

  fun recentMeasurementsForPatient(patientUuid: UUID): Observable<List<BloodPressureMeasurement>> {
    return dao
        .measurementForPatient(patientUuid)
        .toObservable()
  }
}
