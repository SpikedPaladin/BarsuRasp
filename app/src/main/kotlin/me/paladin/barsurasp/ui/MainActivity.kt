package me.paladin.barsurasp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import me.paladin.barsurasp.models.AppTheme
import me.paladin.barsurasp.ui.theme.BarsuRaspTheme
import me.paladin.barsurasp.ui.viewmodels.BusPathViewModel
import me.paladin.barsurasp.ui.viewmodels.BusesViewModel
import me.paladin.barsurasp.ui.viewmodels.MainViewModel
import me.paladin.barsurasp.ui.viewmodels.SettingsViewModel

class MainActivity : ComponentActivity() {
    private val settingsViewModel by viewModels<SettingsViewModel>()
    private val busPathViewModel by viewModels<BusPathViewModel>()
    private val busesViewModel by viewModels<BusesViewModel>()
    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                adUnitId = "ca-app-pub-7728699289076196/7524238972"
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}