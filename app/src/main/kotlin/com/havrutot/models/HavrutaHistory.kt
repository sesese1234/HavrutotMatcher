package com.havrutot.models

import kotlinx.serialization.Serializable

@Serializable
data class HavrutaHistory(
    val pastHashes: MutableSet<String> = mutableSetOf()
) {
    fun addHash(hash: String) { pastHashes.add(hash) }
    fun clear() { pastHashes.clear() }
}
