package entities

import kotlinx.serialization.Serializable

@Serializable
data class SaveTrackingInfo(
    val enabled: Boolean,
    val trackingTime: Int,
    val fullPathToTracking: String
    )