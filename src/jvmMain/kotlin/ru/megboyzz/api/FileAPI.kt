package ru.megboyzz.api

import entities.Element
import entities.Folder
import java.io.File
import java.io.InputStream

interface FileAPI {
    
    fun openFile(path: String) : File

    fun getFile(path: String): Folder

    fun removeFile(path: String)

    fun createFile(path: String)

    fun saveFile(path: String, fileInputStream: InputStream)

    fun isFile(path: String) : Boolean
    
    fun getInfo(path: String): Element
}