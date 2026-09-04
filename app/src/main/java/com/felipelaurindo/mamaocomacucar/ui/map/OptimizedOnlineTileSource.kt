package com.felipelaurindo.mamaocomacucar.ui.map

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import org.osmdroid.tileprovider.BitmapPool
import org.osmdroid.tileprovider.ReusableBitmapDrawable
import org.osmdroid.tileprovider.tilesource.BitmapTileSourceBase
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import java.io.InputStream

/**
 * Fonte de tiles online otimizada para eficiência de memória e desempenho.
 *
 * Principais melhorias em relação ao BitmapTileSourceBase padrão:
 * 1. Define explicitamente `inSampleSize` calculado em `BitmapFactory.Options`, eliminando o alerta
 *    de desempenho e vazamento de recursos do Android Vitals / Google Play Console.
 * 2. Utiliza `Bitmap.Config.RGB_565` para imagens JPEG (satélite e relevo), reduzindo em 50%
 *    o consumo de memória RAM dos bitmaps em relação ao padrão ARGB_8888.
 * 3. Mantém integração com `BitmapPool` e `ReusableBitmapDrawable` do OSMDroid para reutilização
 *    eficiente de buffers de bitmap, minimizando pausas do Garbage Collector.
 */
abstract class OptimizedOnlineTileSource(
    aName: String,
    aZoomMinLevel: Int,
    aZoomMaxLevel: Int,
    aTileSizePixels: Int,
    aImageFilenameEnding: String,
    aBaseUrl: Array<String>
) : OnlineTileSourceBase(
    aName,
    aZoomMinLevel,
    aZoomMaxLevel,
    aTileSizePixels,
    aImageFilenameEnding,
    aBaseUrl
) {

    private val isJpeg = aImageFilenameEnding.equals(".jpg", ignoreCase = true) ||
            aImageFilenameEnding.equals(".jpeg", ignoreCase = true)

    @Throws(BitmapTileSourceBase.LowMemoryException::class)
    override fun getDrawable(aFilePath: String): Drawable? {
        try {
            val targetSize = tileSizePixels

            // 1. Inspeciona dimensões reais da imagem sem alocar bitmap na memória
            val optSize = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeFile(aFilePath, optSize)
            val realWidth = optSize.outWidth
            val realHeight = optSize.outHeight

            // Se o arquivo estiver corrompido ou inacessível
            if (realWidth <= 0 || realHeight <= 0) {
                return null
            }

            // 2. Configura opções de decodificação com reuso de memória
            val bitmapOptions = BitmapFactory.Options()
            BitmapPool.getInstance().applyReusableOptions(bitmapOptions, targetSize, targetSize)

            // 3. Define explicitamente inSampleSize (resolvendo o alerta do Google Play)
            bitmapOptions.inSampleSize = calculateInSampleSize(realWidth, realHeight, targetSize, targetSize)

            // 4. Se for JPEG (sem transparência), usa RGB_565 para economizar 50% de RAM
            if (isJpeg) {
                bitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565
            }

            // 5. Decodifica o arquivo de imagem
            val bitmap = BitmapFactory.decodeFile(aFilePath, bitmapOptions)
            return if (bitmap != null) {
                ReusableBitmapDrawable(bitmap)
            } else {
                null
            }
        } catch (e: OutOfMemoryError) {
            System.gc()
            throw BitmapTileSourceBase.LowMemoryException(e)
        } catch (t: Throwable) {
            return null
        }
    }

    @Throws(BitmapTileSourceBase.LowMemoryException::class)
    override fun getDrawable(aFileInputStream: InputStream): Drawable? {
        try {
            val targetSize = tileSizePixels
            var realWidth = targetSize
            var realHeight = targetSize

            // 1. Inspeciona dimensões reais da stream se suportar mark/reset
            if (aFileInputStream.markSupported()) {
                aFileInputStream.mark(1024 * 1024)
                val optSize = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                BitmapFactory.decodeStream(aFileInputStream, null, optSize)
                if (optSize.outWidth > 0 && optSize.outHeight > 0) {
                    realWidth = optSize.outWidth
                    realHeight = optSize.outHeight
                }
                aFileInputStream.reset()
            }

            // 2. Configura opções de decodificação com reuso de memória
            val bitmapOptions = BitmapFactory.Options()
            BitmapPool.getInstance().applyReusableOptions(bitmapOptions, targetSize, targetSize)

            // 3. Define explicitamente inSampleSize
            bitmapOptions.inSampleSize = calculateInSampleSize(realWidth, realHeight, targetSize, targetSize)

            // 4. Se for JPEG (sem transparência), usa RGB_565 para economizar 50% de RAM
            if (isJpeg) {
                bitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565
            }

            // 5. Decodifica a stream
            val bitmap = BitmapFactory.decodeStream(aFileInputStream, null, bitmapOptions)
            return if (bitmap != null) {
                ReusableBitmapDrawable(bitmap)
            } else {
                null
            }
        } catch (e: OutOfMemoryError) {
            System.gc()
            throw BitmapTileSourceBase.LowMemoryException(e)
        } catch (t: Throwable) {
            return null
        }
    }

    /**
     * Calcula o inSampleSize como potência de 2 para downsampling seguro de bitmaps.
     */
    private fun calculateInSampleSize(
        actualWidth: Int,
        actualHeight: Int,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        var inSampleSize = 1
        if (actualHeight > reqHeight || actualWidth > reqWidth) {
            val halfHeight = actualHeight / 2
            val halfWidth = actualWidth / 2
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }
}
