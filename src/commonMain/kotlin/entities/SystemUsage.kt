package entities

import kotlinx.serialization.Serializable


//Отложено на неопр срок, так как нет инормативности данных
@Serializable
data class SystemUsage(
    var CPUsage: Int,
    var RAMUsage: Int,
    var DiskUsage: Int
)
