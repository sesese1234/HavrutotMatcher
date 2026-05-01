package com.havrutot.data

import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import java.util.Base64

object CryptoUtils {
    private const val ALGORITHM = "AES"
    private val key = SecretKeySpec("H@vrutotS3cr3tK!".toByteArray(), ALGORITHM)

    fun hashString(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(input.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    fun encrypt(data: String): String {
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val encrypted = cipher.doFinal(data.toByteArray())
        return Base64.getEncoder().encodeToString(encrypted)
    }

    fun decrypt(data: String): String {
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, key)
        val decrypted = cipher.doFinal(Base64.getDecoder().decode(data))
        return String(decrypted)
    }
}
