package org.simple.clinic.analytics

import org.simple.clinic.user.User

class MockAnalyticsReporter : AnalyticsReporter {

  var user: User? = null
  var isANewRegistration: Boolean? = null
  val receivedEvents = mutableListOf<Event>()

  override fun setLoggedInUser(user: User, isANewRegistration: Boolean) {
    this.user = user
    this.isANewRegistration = isANewRegistration
  }

  override fun createEvent(event: String, props: Map<String, Any>) {
    receivedEvents.add(Event(event, props))
  }

  override fun resetUser() {
    user = null
  }

  fun clear() {
    clearReceivedEvents()
    clearUsers()
  }

  private fun clearReceivedEvents() {
    receivedEvents.clear()
  }

  private fun clearUsers() {
    user = null
  }

  data class Event(val name: String, val props: Map<String, Any>)
}
