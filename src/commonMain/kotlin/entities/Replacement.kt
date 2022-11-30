package entities

import kotlinx.serialization.Serializable

@Serializable
data class Replacement(
    val ID: Int,
    val what: String,
    val to: String
)