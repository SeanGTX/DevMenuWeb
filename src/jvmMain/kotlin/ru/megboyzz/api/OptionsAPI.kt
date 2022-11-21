package ru.megboyzz.api

import entities.GameLanguage

interface OptionsAPI {
    fun setLanguage(language: GameLanguage)

    fun getLanguage() : GameLanguage
}