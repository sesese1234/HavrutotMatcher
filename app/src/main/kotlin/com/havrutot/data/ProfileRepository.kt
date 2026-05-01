package com.havrutot.data

import com.havrutot.models.Profile
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

object ProfileRepository {
    private val wrapperDir = System.getenv("WRAPPER_DIR") ?: System.getProperty("user.dir")
    private val profileFile = File(wrapperDir, "profile1.profile")
    private val json = Json { ignoreUnknownKeys = true }

    fun loadProfile(): Profile {
        if (!profileFile.exists()) return Profile()
        return try {
            val encryptedData = profileFile.readText()
            val decryptedJson = CryptoUtils.decrypt(encryptedData)
            json.decodeFromString<Profile>(decryptedJson)
        } catch (e: Exception) {
            Profile()
        }
    }

    fun saveProfile(profile: Profile) {
        val jsonString = json.encodeToString(profile)
        val encryptedData = CryptoUtils.encrypt(jsonString)
        profileFile.writeText(encryptedData)
    }
}
