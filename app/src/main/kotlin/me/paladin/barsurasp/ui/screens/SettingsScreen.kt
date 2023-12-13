package me.paladin.barsurasp.ui.screens

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat.startActivity
import com.yandex.mobile.ads.common.AdError
import com.yandex.mobile.ads.common.AdRequestConfiguration
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import com.yandex.mobile.ads.rewarded.Reward
import com.yandex.mobile.ads.rewarded.RewardedAd
import com.yandex.mobile.ads.rewarded.RewardedAdEventListener
import com.yandex.mobile.ads.rewarded.RewardedAdLoadListener
import com.yandex.mobile.ads.rewarded.RewardedAdLoader
import me.paladin.barsurasp.R
import me.paladin.barsurasp.models.AppTheme
import me.paladin.barsurasp.ui.components.AdminBottomSheet
import me.paladin.barsurasp.ui.components.settings.ChooseRow
import me.paladin.barsurasp.ui.components.settings.ClickableRow
import me.paladin.barsurasp.ui.components.settings.SwitchRow
import me.paladin.barsurasp.ui.viewmodels.SettingsViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    backAction: () -> Unit
) {
    val context = LocalContext.current
    val theme by viewModel.theme.collectAsState()
    val monet by viewModel.monet.collectAsState()
    val adCounter by viewModel.adCounter.collectAsState()
    val showBuses by viewModel.showBuses.collectAsState()
    var showAdmin by remember { mutableStateOf(false) }
    val adLoader = RewardedAdLoader(context)

    adLoader.setAdLoadListener(
        object : RewardedAdLoadListener {
            override fun onAdLoaded(ad: RewardedAd) {
                ad.apply {
                    setAdEventListener(
                        object : RewardedAdEventListener {
                            override fun onRewarded(reward: Reward) {
                                viewModel.adWatched()
                            }

                            override fun onAdShown() {}
                            override fun onAdFailedToShow(error: AdError) {}
                            override fun onAdDismissed() {}
                            override fun onAdClicked() {}
                            override fun onAdImpression(data: ImpressionData?) {}
                        }
                    )
                    show(context as Activity)
                }
            }

            override fun onAdFailedToLoad(error: AdRequestError) {}
        }
    )

    Scaffold(
        topBar = { SettingsToolbar(backAction) }
    ) { paddingValues ->
        Column(Modifier.padding(paddingValues)) {
            ChooseRow(
                strings = listOf("Дневная", "Ночная", "Автоматически"),
                title = { Text(text = "Тема приложения") },
                subtitle = {
                    Text(
                        text = "Текущая тема приложения"
                    )
                },
                chosenIndex = when (theme) {
                    AppTheme.DAY -> 0
                    AppTheme.NIGHT -> 1
                    AppTheme.AUTO -> 2
                },
                onChoose = {
                    viewModel.changeTheme(
                        when (it) {
                            0 -> AppTheme.DAY
                            1 -> AppTheme.NIGHT
                            else -> AppTheme.AUTO
                        }
                    )
                }
            )
            SwitchRow(
                checked = monet,
                title = { Text(text = "Динамические цвета") },
                subtitle = { Text(text = "Только для Android 12+") },
                onCheckedChange = { viewModel.changeMonet(!monet) }
            )

            SwitchRow(
                checked = showBuses,
                title = { Text("Расписание автобусов") },
                subtitle = { Text("Отображение расписания автобусов на главном экране") },
                onCheckedChange = { viewModel.setShowBuses(!showBuses) }
            )

            ClickableRow(
                title = {
                    if (adCounter < 5)
                        Text(text = "Отключить рекламу")
                    else
                        Text(text = "Реклама отключена")
                },
                subtitle = {
                    if (adCounter == 0)
                        Text(text = "Посмотри 5 рекламных роликов чтобы отключить рекламу навсегда")
                    else if (adCounter < 5)
                        Text(text = "Осталось ещё ${5 - adCounter}")
                    else
                        Text(text = "Спасибо за поддержку!")
                },
                onClick = {
                    Toast.makeText(context, "Загрузка ролика...", Toast.LENGTH_SHORT).show()
                    adLoader.loadAd(AdRequestConfiguration.Builder("R-M-3361745-2").build())
                }
            )

            ClickableRow(
                title = { Text(text = "Обновить виджеты") },
                subtitle = { Text(text = "Перезагрузить все установленные виджеты") },
                onClick = { viewModel.updateWidgets(context) }
            )

            ClickableRow(
                title = { Text(text = "Telegram-Бот") },
                subtitle = { Text(text = "Бот с расписанием занятий") },
                onClick = {
                    startActivity(
                        context,
                        Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/BarsuRaspBot")),
                        null
                    )
                }
            )

            ClickableRow(
                title = { Text(text = "Поддержка") },
                subtitle = { Text(text = "Вопросы и предложения по улучшению приложения") },
                onClick = {
                    startActivity(
                        context,
                        Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/PaladinDev")),
                        null
                    )
                },
                onLongClick = {
                    showAdmin = true
                }
            )
        }

        if (showAdmin)
            AdminBottomSheet(doneCallback = {
                for (i in 0..5) viewModel.adWatched()
            }) {
                showAdmin = false
            }
    }
}

@Composable
private fun SettingsToolbar(backAction: (() -> Unit)? = null) {
    TopAppBar(
        title = {
            Text(text = stringResource(R.string.settings_title))
        },
        navigationIcon = {
            if (backAction != null) {
                IconButton(onClick = backAction) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = stringResource(R.string.description_back)
                    )
                }
            }
        }
    )
}