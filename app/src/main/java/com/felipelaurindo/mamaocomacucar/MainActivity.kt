package com.felipelaurindo.mamaocomacucar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.felipelaurindo.mamaocomacucar.ui.theme.MamaoComAcucarTheme

import org.osmdroid.config.Configuration
import android.content.Context
import com.google.android.gms.ads.MobileAds

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val osmConfig = Configuration.getInstance()
        osmConfig.load(this, getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
        osmConfig.userAgentValue = packageName
        // Otimizações de desempenho e cache do OSMDroid:
        // 1. Aumenta cache em memória de 9 (padrão) para 128 tiles para evitar recargas constantes ao arrastar
        osmConfig.cacheMapTileCount = 128.toShort()
        osmConfig.cacheMapTileOvershoot = 32.toShort()
        // 2. Aumenta threads de download e de leitura do disco para carregamento paralelo e instantâneo
        osmConfig.tileDownloadThreads = 6.toShort()
        osmConfig.tileFileSystemThreads = 6.toShort()
        osmConfig.tileDownloadMaxQueueSize = 100.toShort()
        osmConfig.tileFileSystemMaxQueueSize = 100.toShort()
        // 3. Estende validade do cache em disco para 30 dias (evita checagens HTTP desnecessárias) e expande limite de armazenamento
        osmConfig.expirationExtendedDuration = 1000L * 60 * 60 * 24 * 30L
        osmConfig.tileFileSystemCacheMaxBytes = 300L * 1024 * 1024L
        osmConfig.tileFileSystemCacheTrimBytes = 250L * 1024 * 1024L

        // Inicialização do Google Mobile Ads (AdMob)
        MobileAds.initialize(this) {}

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                scrim = android.graphics.Color.TRANSPARENT,
                darkScrim = android.graphics.Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.light(
                scrim = android.graphics.Color.TRANSPARENT,
                darkScrim = android.graphics.Color.TRANSPARENT
            )
        )
        setContent {
            MamaoComAcucarTheme {
                MamaoApp()
            }
        }
    }
}