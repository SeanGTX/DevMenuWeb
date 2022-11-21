package ru.megboyzz.api.impl

import entities.Element
import entities.Folder
import kotlinx.datetime.Instant
import ru.megboyzz.api.FileAPI
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.InputStream

class FileImpl: FileAPI {

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

    override fun openFile(path: String) : File {
        val file = File(path)
        if(!file.exists()) throw FileNotFoundException()
        return file
    }

    override fun getFile(path: String): Folder {
        val file = File(path)
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

    override fun removeFile(path: String) {
        val file = File(path)
        if(!file.exists()) throw FileNotFoundException()

        if(file.isFile)
            file.delete()
        else
            file.deleteRecursively()
    }


    override fun getInfo(path: String): Element {
        val file = File(path)
        return Element(
            file.name,
            file.parent, //Это не совсем basePath
            path,
            file.size,
            file.dateOfChange
        )

    }

    override fun isFile(path: String) : Boolean {
        return File(path).isFile
    }

    override fun createFile(path: String) {
        val file = File(path)
        if(file.exists()) throw Exception()
        file.createNewFile()
    }

    override fun saveFile(path: String, fileInputStream: InputStream) {
        val file = File(path)
        if(!file.exists()) throw FileNotFoundException()
        val fos = FileOutputStream(file)
        fileInputStream.copyTo(fos)
        fos.write(byteArrayOf())
    }
}