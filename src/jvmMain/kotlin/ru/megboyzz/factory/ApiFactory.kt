package ru.megboyzz.factory

import ru.megboyzz.api.FileAPI
import ru.megboyzz.api.OptionsAPI
import ru.megboyzz.api.PlatformAPI
import ru.megboyzz.api.ReplacementAPI

//Базовая фабрика
interface ApiFactory {
    fun createFileAPI(): FileAPI
    fun createOptionsAPI(): OptionsAPI
    fun createPlatformAPI(): PlatformAPI
    fun createReplacementsAPI(): ReplacementAPI
}