package entities

import kotlinx.serialization.Serializable

@Serializable
data class SVMW(
    val name: String,
    val description: String,
)
