package me.paladin.barsurasp.ui.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
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
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import me.paladin.barsurasp.R
import me.paladin.barsurasp.models.AppTheme
import me.paladin.barsurasp.ui.components.settings.ChooseRow
import me.paladin.barsurasp.ui.components.settings.ClickableRow
import me.paladin.barsurasp.ui.components.settings.GroupTitle
import me.paladin.barsurasp.ui.components.settings.SwitchRow
import me.paladin.barsurasp.ui.components.sheets.AdminSheet
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

    Scaffold(
        topBar = { SettingsToolbar(backAction) }
    ) { paddingValues ->
        Column(Modifier.padding(paddingValues)) {
            GroupTitle("Интерфейс")
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

            GroupTitle("Разное")
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
                    showAd(context) { viewModel.adWatched() }
                }
            )

            ClickableRow(
                title = { Text(text = "Обновить виджеты") },
                subtitle = { Text(text = "Перезагрузить все установленные виджеты") },
                onClick = { viewModel.updateWidgets(context) }
            )

            GroupTitle("О приложении")
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
                title = { Text(text = "Поделиться") },
                subtitle = { Text(text = "Рассказать другим о приложении") },
                onClick = {
                    startActivity(
                        context,
                        Intent.createChooser(Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "Android Приложение Расписание БарГУ\nhttps://play.google.com/store/apps/details?id=me.paladin.barsurasp\n\nTelegram-Бот Расписание БарГУ\nhttps://t.me/BarsuRaspBot"
                            )
                            type = "text/plain"
                        }, null),
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

        AdminSheet(
            visible = showAdmin,
            onAccess = { repeat(5) { viewModel.adWatched() } },
            onDismiss = { showAdmin = false }
        )
    }
}

private fun showAd(context: Context, onReward: () -> Unit) {
    RewardedAd.load(context, "ca-app-pub-3940256099942544/5224354917", AdRequest.Builder().build(), object : RewardedAdLoadCallback() {
        override fun onAdFailedToLoad(adError: LoadAdError) {
            Log.d("AdMob", adError.toString())
        }

        override fun onAdLoaded(ad: RewardedAd) {
            ad.show(context as Activity) { onReward() }
        }
    })
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