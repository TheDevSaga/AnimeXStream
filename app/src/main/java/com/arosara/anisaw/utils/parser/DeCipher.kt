package com.arosara.anisaw.utils.parser

import android.os.Build
import com.arosara.anisaw.utils.constants.C
import java.net.URLDecoder
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object DeCipher {

    private const val CIPHER_TRANSFORMATION = "AES/CBC/PKCS5Padding"
    private const val ALGORITHM = "AES"

    fun encryptAes(text: String, key: String, iv: String): String {
        val ix = IvParameterSpec(iv.toByteArray())
        val cipher = Cipher.getInstance(CIPHER_TRANSFORMATION)
        val secretKey = SecretKeySpec(key.toByteArray(), ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ix)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Base64.getEncoder().encodeToString(cipher.doFinal(text.toByteArray() + C.GogoPadding))
        } else {
            android.util.Base64.encodeToString(
                cipher.doFinal(text.toByteArray() + C.GogoPadding),
                android.util.Base64.DEFAULT
            )
        }
    }

    fun decryptAES(encryptedText: String, key: String, iv: String): String {
        val ix = IvParameterSpec(iv.toByteArray())
        val cipher = Cipher.getInstance(CIPHER_TRANSFORMATION)
        val secretKey = SecretKeySpec(key.toByteArray(Charsets.UTF_8), ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ix)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String(cipher.doFinal(Base64.getDecoder().decode(encryptedText)))
        } else {
            String(
                cipher.doFinal(
                    android.util.Base64.decode(
                        encryptedText,
                        android.util.Base64.DEFAULT
                    )
                )
            )
        }
    }

    fun decode(url: String): String {
        return URLDecoder.decode(url, Charsets.UTF_8.name())
    }
}