package com.felipelaurindo.mamaocomacucar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.felipelaurindo.mamaocomacucar.R
import com.felipelaurindo.mamaocomacucar.ui.theme.Stone200
import com.felipelaurindo.mamaocomacucar.ui.theme.Stone400
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

/**
 * Banner AdMob com tamanho fixo (50.dp) pré-alocado.
 *
 * Garante que:
 * 1. O layout NUNCA sofra salto (Zero CLS - Cumulative Layout Shift), prevenindo cliques acidentais.
 * 2. O ciclo de vida do AdView (pause, resume, destroy) seja respeitado para evitar vazamento de memória.
 * 3. Em modo de inspeção (Preview do Android Studio), renderize um mock sem quebrar.
 */
@Composable
fun AdMobBanner(
    modifier: Modifier = Modifier,
    adUnitId: String = stringResource(id = R.string.admob_banner_unit_id)
) {
    // Tratamento para Previews do Compose
    if (LocalInspectionMode.current) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            HorizontalDivider(modifier = Modifier.align(Alignment.TopCenter), color = Stone200)
            Text(
                text = "Anúncio Parceiro (Preview)",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = Stone400
            )
        }
        return
    }

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Instancia o AdView de forma memorizada
    val adView = remember {
        AdView(context).apply {
            setAdSize(AdSize.BANNER)
            this.adUnitId = adUnitId
            loadAd(AdRequest.Builder().build())
        }
    }

    // Vincula o ciclo de vida do AdView ao ciclo de vida da tela
    DisposableEffect(lifecycleOwner, adView) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> adView.pause()
                Lifecycle.Event.ON_RESUME -> adView.resume()
                Lifecycle.Event.ON_DESTROY -> adView.destroy()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            adView.destroy()
        }
    }

    // Container fixo de 50.dp
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {


        // Texto de fundo sutil caso o anúncio demore para carregar
        Text(
            text = "Anúncio",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
            color = Stone400
        )

        // Visualização nativa do anúncio
        AndroidView(
            factory = { adView },
            modifier = Modifier.fillMaxSize()
        )
    }
}
