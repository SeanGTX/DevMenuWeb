package entities

import kotlinx.serialization.Serializable


@Serializable
data class SystemUsage(
    var CPUsage: Int,
    var RAMUsage: Int,
    var DiskUsage: Int
)
