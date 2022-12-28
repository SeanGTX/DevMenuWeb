package ru.megboyzz.api

import entities.SVMW
import java.io.File
import java.io.InputStream

interface SvmwAPI {

    //Получение информации о svmw
    fun getInfoAboutSvmw(inputStream: InputStream): SVMW

    //Загрузить новый svmw в сохранение
    fun setSVMWasSaveFile(inputStream: InputStream)

    //Получить текущий загруженный в игру svmw
    fun getLoadedSVMW(): File

    //Создать новый svmw из текущего файла сохранения
    fun createSVMWfromCurrentSave(data: SVMW): File

    //Получить информацию о текущем загруженном svmw
    fun getInfoAboutLoadedSVMW(): SVMW

    //Очистка кеша svmw
    //P.S. так как ktor нормально работает именно с ссылками на файлы, стоит создать кеш для svmw
    // файлов и от туда их брать, менеджить это будет конечное устройство
    fun clearSVMWcache()

}