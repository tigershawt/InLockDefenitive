package com.jetbrains.inlock.data

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val manufacturer: String = "",
    val manufacturerName: String = "",
    val category: String = "",
    val imageUrl: String = "",
    val createdAt: Long = 0,
    val properties: Map<String, String> = mapOf(),
    val currentOwner: String = "",
    val isRegisteredOnBlockchain: Boolean = false
)