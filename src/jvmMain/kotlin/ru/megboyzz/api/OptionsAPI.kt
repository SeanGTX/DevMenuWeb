package ru.megboyzz.api

import entities.GameLanguage
import entities.SaveTrackingInfo
import java.io.File
import java.io.InputStream

interface OptionsAPI {
    fun setLanguage(language: GameLanguage)

    fun getLanguage() : GameLanguage

    fun setSaveTrackingInfo(info: SaveTrackingInfo)
    fun getSaveTrackingInfo(): SaveTrackingInfo

    /**
     * В сеттере используется InputStream потому что мы отправляем не ссылки на файлы а содержимое файла
     */
    fun setSaveFile(save: InputStream)

    fun getSaveFile(): File

}