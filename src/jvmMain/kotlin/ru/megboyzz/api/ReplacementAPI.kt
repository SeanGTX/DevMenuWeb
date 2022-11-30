package ru.megboyzz.api

import entities.Replacement

interface ReplacementAPI {

    //При создании замены мы отправляем
    //полную сущность замены файла
    fun addReplacement(replacement: Replacement)

    //Когда мы хотим воостановить замену,
    //мы смотрим на флаг в информации, заменен ли файл или нет
    //и если реально заменен то позволяем пользователю воостановить файл
    fun recoverReplacementByID(replacementId: Int)

    fun getAllReplacements(): List<Replacement>

    fun recoverAllReplacements()
}