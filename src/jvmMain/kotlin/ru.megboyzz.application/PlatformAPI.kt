package ru.megboyzz.application

import entities.Element
import entities.Folder

//Интерфейс предоставляющий реализацию методов API
//на конкретной платформе(ПК - для отладки, Android - для использования)
interface PlatformAPI {

    fun open(path: String, basePath: String)

    fun getFile(path: String, basePath: String): Folder

    fun removeFile(path: String, basePath: String)

    fun shutdown()

    fun getNextLogLine(): String

    fun getInfo(path: String, basePath: String): Element

}