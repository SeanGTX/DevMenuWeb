package ru.megboyzz.api

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

    fun shutdown()

    fun getNextLogLine(): String

}