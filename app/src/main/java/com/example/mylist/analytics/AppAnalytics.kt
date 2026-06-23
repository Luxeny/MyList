package com.example.mylist.analytics

import android.app.Application
import android.util.Log
import com.example.mylist.BuildConfig
import io.appmetrica.analytics.AppMetrica
import io.appmetrica.analytics.AppMetricaConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppAnalytics @Inject constructor() {

    private val isEnabled: Boolean
        get() = BuildConfig.APPMETRICA_API_KEY.isNotBlank()

    fun initialize(application: Application) {
        val apiKey = BuildConfig.APPMETRICA_API_KEY
        if (apiKey.isBlank()) {
            Log.w(TAG, "APPMETRICA_API_KEY пустой. Сделайте Gradle Sync и пересоберите приложение.")
            return
        }

        val configBuilder = AppMetricaConfig.newConfigBuilder(apiKey)
        if (BuildConfig.DEBUG) {
            configBuilder.withLogs()
        }

        AppMetrica.activate(application, configBuilder.build())
        AppMetrica.reportEvent("app_opened")
        AppMetrica.sendEventsBuffer()

        Log.d(TAG, "AppMetrica активирована")
    }

    fun logCategoryCreated(categoryName: String) {
        reportEvent("category_created", mapOf("name" to categoryName))
    }

    fun logCategoryUpdated() {
        reportEvent("category_updated")
    }

    fun logCategoryDeleted() {
        reportEvent("category_deleted")
    }

    fun logItemsScreenOpened(categoryId: Long) {
        reportEvent("items_screen_opened", mapOf("category_id" to categoryId.toString()))
    }

    fun logItemCreated(status: String) {
        reportEvent("item_created", mapOf("status" to status))
    }

    fun logItemUpdated() {
        reportEvent("item_updated")
    }

    fun logItemDeleted() {
        reportEvent("item_deleted")
    }

    fun logItemStatusChanged(status: String) {
        reportEvent("item_status_changed", mapOf("status" to status))
    }

    fun logHandledError(message: String, error: Throwable) {
        if (!isEnabled) return
        AppMetrica.reportError(message, error)
        AppMetrica.sendEventsBuffer()
        Log.d(TAG, "Отправлена ошибка: $message")
    }

    private fun reportEvent(name: String, params: Map<String, String> = emptyMap()) {
        if (!isEnabled) return
        if (params.isEmpty()) {
            AppMetrica.reportEvent(name)
        } else {
            AppMetrica.reportEvent(name, params)
        }
        AppMetrica.sendEventsBuffer()
        Log.d(TAG, "Отправлено событие $name: $params")
    }

    companion object {
        private const val TAG = "AppAnalytics"
    }
}
