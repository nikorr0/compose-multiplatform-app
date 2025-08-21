package org.company.app.util

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.lang.ref.WeakReference

/**
 * Хранит WeakReference на Activity, которая сейчас в фокусе.
 * Регистрируется один раз в классе Application.
 */
object ActivityProvider : Application.ActivityLifecycleCallbacks {

    private var currentRef: WeakReference<Activity>? = null
    val currentActivity: Activity?
        get() = currentRef?.get()

    override fun onActivityResumed(activity: Activity) {
        currentRef = WeakReference(activity)
    }

    /* остальные колбэки нам не нужны */
    override fun onActivityPaused(a: Activity)  {}
    override fun onActivityCreated(a: Activity, b: Bundle?) {}
    override fun onActivityStarted(a: Activity)  {}
    override fun onActivityStopped(a: Activity)  {}
    override fun onActivitySaveInstanceState(a: Activity, b: Bundle) {}
    override fun onActivityDestroyed(a: Activity) {}
}