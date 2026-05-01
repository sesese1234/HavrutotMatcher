package com.havrutot.models

import kotlinx.serialization.Serializable

@Serializable
data class Student(
    val id: String,
    val name: String,
    val isLocked: Boolean = false,
    val blacklist: List<String> = emptyList()
)
