package com.havrutot.models

import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val students: MutableList<Student> = mutableListOf(),
    val history: HavrutaHistory = HavrutaHistory(),
    var passwordHash: String? = null,
    var isListVisible: Boolean = true,
    var accentColorHex: String = "#BB86FC"
)
