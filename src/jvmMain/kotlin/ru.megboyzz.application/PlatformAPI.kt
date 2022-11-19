package ru.megboyzz.application

import entities.Element
import entities.Folder
import entities.GameLanguage
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream

//Интерфейс предоставляющий реализацию методов API
//на конкретной платформе(ПК - для отладки, Android - для использования)
//TODO разделить этот интефейс на отдельные части для не опущения загромаждения
// файла с итерфейсом, а в следствии и файла реализующего этот интерфейс
interface PlatformAPI {
    // basePath - показывает откуда идти (EXTERNAL, INTERNAL)
    fun openFile(path: String, basePath: String) : File

    fun getFile(path: String, basePath: String): Folder

    fun removeFile(path: String, basePath: String)

    fun createFile(path: String, basePath: String)

    fun saveFile(path: String, basePath: String, fileInputStream: InputStream)

    fun isFile(path: String, basePath: String) : Boolean

    fun shutdown()

    fun getNextLogLine(): String

    fun getInfo(path: String, basePath: String): Element

    fun setLanguage(language: GameLanguage)

    fun getLanguage() : GameLanguage

}