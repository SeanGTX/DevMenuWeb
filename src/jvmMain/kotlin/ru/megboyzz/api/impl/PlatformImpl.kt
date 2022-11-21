package ru.megboyzz.api.impl

import entities.Element
import entities.Folder
import entities.GameLanguage
import kotlinx.datetime.Instant
import ru.megboyzz.api.PlatformAPI
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.InputStream

//Реализация для ПК
class PlatformImpl(language: GameLanguage) : PlatformAPI {

    private var language = language



    override fun shutdown() {
        TODO("Not yet implemented")
    }

    override fun getNextLogLine(): String{
        return ""
    }

    //TODO реализовать язык
    fun setLanguage(language: GameLanguage) {
        //Так как на пк язык не нужен так то, просто будем дергать переменную)
        this.language = language
    }

    //TODO реализовать язык
    fun getLanguage(): GameLanguage {
        return language
    }
}