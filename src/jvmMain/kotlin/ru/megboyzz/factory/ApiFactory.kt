package ru.megboyzz.factory

import ru.megboyzz.api.*

//Базовая фабрика
interface ApiFactory {
    fun createFileAPI(): FileAPI
    fun createOptionsAPI(): OptionsAPI
    fun createPlatformAPI(): PlatformAPI
    fun createReplacementsAPI(): ReplacementAPI
    fun createSvmwAPI(): SvmwAPI
}