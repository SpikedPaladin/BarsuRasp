package me.paladin.barsurasp

import android.app.Application
import android.util.Log
import com.yandex.mobile.ads.common.MobileAds

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Locator.initWith(this)
        MobileAds.initialize(this) {
            Log.d("YandexMobileAds", "SDK initialized")
        }
    }
}