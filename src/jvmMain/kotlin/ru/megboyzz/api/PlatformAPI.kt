package ru.megboyzz.api

import entities.Element
import entities.Folder
import entities.GameLanguage
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
interface PlatformAPI {

    fun shutdown()

    fun getNextLogLine(): String

}