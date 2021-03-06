package org.simple.clinic.storage.migrations

import androidx.test.runner.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import org.simple.clinic.assertTableDoesNotExist
import org.simple.clinic.assertTableExists

@RunWith(AndroidJUnit4::class)
class Migration55AndroidTest : BaseDatabaseMigrationTest(fromVersion = 54, toVersion = 55) {

  @Test
  fun migrating_to_55_should_generate_the_BloodSugarMeasurements_table() {
    before.assertTableDoesNotExist("BloodSugarMeasurements")

    after.assertTableExists("BloodSugarMeasurements")
  }
}
