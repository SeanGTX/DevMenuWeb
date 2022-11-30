package entities

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Folder(
    val folders: List<String>,
    val files: List<String>
)


//Класс описывающий Элемент файловой системы, при запросе информации о нем
@Serializable
data class Element(
    val name: String,
    val basePath: String,
    val path: String,
    val size: Long,
    val dateOfChange: Instant,
    val replacementId: Int //Указывает на то что файл заменен или нет(-1 -> нет замены)
)
