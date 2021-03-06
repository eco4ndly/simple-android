package org.simple.clinic.onboarding

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import com.f2prateek.rx.preferences2.Preference
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import io.reactivex.rxkotlin.cast
import kotlinx.android.synthetic.main.screen_onboarding.view.*
import org.simple.clinic.ReportAnalyticsEvents
import org.simple.clinic.di.injector
import org.simple.clinic.mobius.MobiusDelegate
import org.simple.clinic.platform.crash.CrashReporter
import org.simple.clinic.router.screen.RouterDirection
import org.simple.clinic.router.screen.ScreenRouter
import org.simple.clinic.selectcountry.SelectCountryScreenKey
import org.simple.clinic.util.clamp
import org.simple.clinic.util.scheduler.SchedulersProvider
import org.simple.clinic.util.unsafeLazy
import javax.inject.Inject
import javax.inject.Named

class OnboardingScreen(context: Context, attributeSet: AttributeSet) : RelativeLayout(context, attributeSet), OnboardingUi {

  @Inject
  lateinit var activity: AppCompatActivity

  @field:[Inject Named("onboarding_complete")]
  lateinit var hasUserCompletedOnboarding: Preference<Boolean>

  @Inject
  lateinit var schedulersProvider: SchedulersProvider

  @Inject
  lateinit var crashReporter: CrashReporter

  @Inject
  lateinit var screenRouter: ScreenRouter

  private val events: Observable<OnboardingEvent>
    get() = getStartedClicks()
        .compose(ReportAnalyticsEvents())
        .cast()

  private val delegate by unsafeLazy {
    MobiusDelegate(
        events,
        OnboardingModel,
        null,
        OnboardingUpdate(),
        OnboardingEffectHandler.createEffectHandler(hasUserCompletedOnboarding, this, schedulersProvider),
        { /* No-op, there's nothing to render */ },
        crashReporter
    )
  }

  override fun onFinishInflate() {
    super.onFinishInflate()

    if (isInEditMode) {
      return
    }

    context.injector<OnboardingScreenInjector>().inject(this)

    fadeLogoWithContentScroll()
    delegate.prepare()
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    delegate.start()
  }

  override fun onDetachedFromWindow() {
    delegate.stop()
    super.onDetachedFromWindow()
  }

  private fun fadeLogoWithContentScroll() {
    scrollView.setOnScrollChangeListener { _: NestedScrollView, _: Int, scrollY: Int, _: Int, _: Int ->
      val distanceBetweenLogoAndScrollTop = appLogoImageView.top - scrollY.toFloat()
      val opacity = (distanceBetweenLogoAndScrollTop / appLogoImageView.top).clamp(0F, 1F)
      appLogoImageView.alpha = opacity
    }
  }

  private fun getStartedClicks(): Observable<OnboardingEvent> {
    return RxView.clicks(getStartedButton).map { GetStartedClicked }
  }

  override fun moveToRegistrationScreen() {
    screenRouter.clearHistoryAndPush(SelectCountryScreenKey(), RouterDirection.FORWARD)
  }
}
