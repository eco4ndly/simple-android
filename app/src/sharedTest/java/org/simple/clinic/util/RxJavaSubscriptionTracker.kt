package org.simple.clinic.util

import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.CompletableSource
import io.reactivex.plugins.RxJavaPlugins

class RxJavaSubscriptionTracker {

  private var assembledCompletables = mutableListOf<CompletableOnAssembly>()

  fun startTracking() {
    RxJavaPlugins.setOnCompletableAssembly { source ->
      val assembly = CompletableOnAssembly(source)

      assembledCompletables.add(assembly)

      assembly
    }
  }

  fun stopTracking() {
    RxJavaPlugins.setOnCompletableAssembly(null)
  }

  fun assertAllCompletablesSubscribed() {
    val assembledCount = assembledCompletables.size
    val subscribedCount = assembledCompletables.count { it.hasBeenSubscribedTo }
    val haveAllBeenSubscribed = assembledCount == subscribedCount

    if (!haveAllBeenSubscribed) {
      throw AssertionError("Assembled $assembledCount Completables, but only subscribed to $subscribedCount!")
    }
  }

  private class CompletableOnAssembly(
      private val source: CompletableSource
  ) : Completable() {

    var hasBeenSubscribedTo: Boolean = false
      private set

    override fun subscribeActual(observer: CompletableObserver) {
      hasBeenSubscribedTo = true
      source.subscribe(observer)
    }
  }
}
