package me.paladin.barsurasp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.yandex.mobile.ads.banner.BannerAdSize
import com.yandex.mobile.ads.banner.BannerAdView
import com.yandex.mobile.ads.common.AdRequest
import me.paladin.barsurasp.models.AppTheme
import me.paladin.barsurasp.ui.theme.BarsuRaspTheme
import me.paladin.barsurasp.ui.viewmodels.BusPathViewModel
import me.paladin.barsurasp.ui.viewmodels.BusesViewModel
import me.paladin.barsurasp.ui.viewmodels.MainViewModel
import me.paladin.barsurasp.ui.viewmodels.SettingsViewModel
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    private val settingsViewModel by viewModels<SettingsViewModel>()
    private val busPathViewModel by viewModels<BusPathViewModel>()
    private val busesViewModel by viewModels<BusesViewModel>()
    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val theme by settingsViewModel.theme.collectAsState()
            val monet by settingsViewModel.monet.collectAsState()
            val adCounter by settingsViewModel.adCounter.collectAsState()

            val darkTheme = when (theme) {
                AppTheme.DAY -> false
                AppTheme.NIGHT -> true
                else -> isSystemInDarkTheme()
            }

            BarsuRaspTheme(darkTheme, monet) {
                Column {
                    MainNavGraph(
                        mainViewModel,
                        busesViewModel,
                        busPathViewModel,
                        settingsViewModel,
                        modifier = Modifier.weight(1F)
                    )

                    if (adCounter < 5)
                        BannerAd()
                }
            }
        }
    }
}

@Composable
fun BannerAd() {
    AndroidView(
        factory = { context ->
            BannerAdView(context).apply {
                setAdUnitId("R-M-3361745-1")
                var adWidthPixels = width
                if (adWidthPixels == 0) {
                    // If the ad hasn't been laid out, default to the full screen width
                    adWidthPixels = resources.displayMetrics.widthPixels
                }
                val adWidth = (adWidthPixels / resources.displayMetrics.density).roundToInt()

                setAdSize(BannerAdSize.stickySize(context, adWidth))
                val request = AdRequest.Builder().build()
                loadAd(request)
            }
        }
    )
}