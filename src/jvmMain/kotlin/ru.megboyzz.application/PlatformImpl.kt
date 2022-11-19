package ru.megboyzz.application

import entities.Element
import entities.Folder
import entities.GameLanguage
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.InputStream
import java.nio.charset.Charset
import java.util.*

//Реализация для ПК
class PlatformImpl(language: GameLanguage) : PlatformAPI {

    private var language = language


    private val File.size
    get() =
        if (!exists()) 0
        else if(isFile)
            length()
        else
            walkTopDown().filter { it.isFile }.map { it.length() }.sum().toLong()

    // Может быть потребуется вынести расширения для объектов класса Файл в отдельный файл
    // Но это уже совсем другая история)))))
    private val File.dateOfChange get() = Instant.fromEpochMilliseconds(lastModified())

    private val File.sizeInKb get() = size / 1024
    private val File.sizeInMb get() = sizeInKb / 1024
    private val File.sizeInGb get() = sizeInMb / 1024

    override fun openFile(path: String, basePath: String) : File {
        val file = File(basePath + path)
        if(!file.exists()) throw FileNotFoundException()
        return file
    }

    override fun getFile(path: String, basePath: String): Folder {
        val file = File(basePath + path)
        if(!file.exists()) throw FileNotFoundException()
        val subFolders = emptyList<String>().toMutableList()
        val subFiles = emptyList<String>().toMutableList()

        for (subFile in file.listFiles()!!)
            if(subFile.isFile)
                subFiles.add(subFile.name)
            else
                subFolders.add(subFile.name)


        return Folder(subFolders, subFiles)
    }

    override fun removeFile(path: String, basePath: String) {
        val file = File(basePath + path)
        if(!file.exists()) throw FileNotFoundException()

        if(file.isFile)
            file.delete()
        else
            file.deleteRecursively()
    }

    override fun shutdown() {
        TODO("Not yet implemented")
    }

    override fun getNextLogLine(): String{
        return ""
    }

    override fun getInfo(path: String, basePath: String): Element {
        val file = File(basePath + path)
        return Element(
            file.name,
            basePath,
            path,
            file.size,
            file.dateOfChange
        )

    }

    //TODO реализовать язык
    override fun setLanguage(language: GameLanguage) {
        //Так как на пк язык не нужен так то, просто будем дергать переменную)
        this.language = language
    }

    //TODO реализовать язык
    override fun getLanguage(): GameLanguage {
        return language
    }

    override fun isFile(path: String, basePath: String) : Boolean {
        return File(basePath + path).isFile
    }

    override fun createFile(path: String, basePath: String) {
        val file = File(basePath + path)
        if(file.exists()) throw Exception()
        file.createNewFile()
    }

    override fun saveFile(path: String, basePath: String, fileInputStream: InputStream) {
        val file = File(basePath + path)
        if(!file.exists()) throw FileNotFoundException()
        val fos = FileOutputStream(file)
        fileInputStream.copyTo(fos)
        fos.write(byteArrayOf())
    }
}