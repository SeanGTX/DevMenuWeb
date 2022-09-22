package ru.megboyzz.application

import entities.Element
import entities.Folder
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.io.File
import java.io.FileNotFoundException
import java.util.*

//Реализация для ПК
class PlatformImpl : PlatformAPI {

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

    override fun open(path: String, basePath: String) {
        TODO("Not yet implemented")
    }

    override fun getFile(path: String, basePath: String): Folder {
        val file = File(basePath + path);
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


}