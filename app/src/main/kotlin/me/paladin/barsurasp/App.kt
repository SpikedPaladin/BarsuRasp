package me.paladin.barsurasp

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import com.yandex.mobile.ads.common.MobileAds
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import me.paladin.barsurasp.data.UserPreferencesRepository
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class App : Application() {

    init { application = this }

    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this) {
            Log.d("YandexMobileAds", "SDK initialized")
        }
    }

    companion object {
        lateinit var application: App
        val client = HttpClient(Android) {
            engine {
                sslManager = {
                    val trustAllCerts = arrayOf<TrustManager>(@SuppressLint("CustomX509TrustManager")
                    object : X509TrustManager {
                        override fun getAcceptedIssuers(): Array<out X509Certificate>? {
                            return null
                        }

                        @SuppressLint("TrustAllX509TrustManager")
                        @Throws(CertificateException::class)
                        override fun checkClientTrusted(arg0: Array<X509Certificate?>?, arg1: String?) {
                            // Not implemented
                        }

                        @SuppressLint("TrustAllX509TrustManager")
                        @Throws(CertificateException::class)
                        override fun checkServerTrusted(arg0: Array<X509Certificate?>?, arg1: String?) {
                            // Not implemented
                        }
                    })

                    try {
                        val sc = SSLContext.getInstance("TLS")
                        sc.init(null, trustAllCerts, SecureRandom())
                        it.sslSocketFactory = sc.socketFactory
                    } catch (e: KeyManagementException) {
                        e.printStackTrace()
                    } catch (e: NoSuchAlgorithmException) {
                        e.printStackTrace()
                    }
                }
            }
        }

        private val Context.dataStore by preferencesDataStore(
            name = "user_preferences",
            corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() }
        )

        val prefs by lazy {
            UserPreferencesRepository(application.dataStore)
        }

        fun getCacheDir() = application.cacheDir
    }
}