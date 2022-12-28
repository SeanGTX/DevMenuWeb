package entities

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class SVMW(
    val name: String,
    val description: String,
    val dateOfCreate: Instant
)
