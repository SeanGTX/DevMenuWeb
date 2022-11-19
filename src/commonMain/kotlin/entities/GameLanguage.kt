package entities

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = GameLanguageTypeSerializer::class)
enum class GameLanguageType(val lang: String) {
    System("sys"),
    Chinese("cn"),
    Dutch("nl"),
    English("en"),
    French("fr"),
    Deutsch("de"),
    Italian("it"),
    Japanese("ja"),
    Korean("kr"),
    Portuguese("br"),
    Russian("ru"),
    Spanish("es");

    val invoke = GameLanguage(this)

    override fun toString(): String {
        return lang
    }

    companion object {
        fun findByKey(lang: String): GameLanguageType {
            return GameLanguageType.values().find { it.lang == lang } ?: System
        }

        //Метод создания языка из переменной
        fun invoke(lang: String): GameLanguage{
            return GameLanguageType.values().find { it.lang == lang }?.invoke ?: System.invoke
        }
    }
}

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = GameLanguageType::class)
private object GameLanguageTypeSerializer : KSerializer<GameLanguageType> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("GameLanguageType", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: GameLanguageType) {
        encoder.encodeString(value.lang)
    }

    override fun deserialize(decoder: Decoder): GameLanguageType {
        return try {
            val key = decoder.decodeString()
            GameLanguageType.findByKey(key)
        } catch (e: IllegalArgumentException) {
            GameLanguageType.System
        }
    }
}

@Serializable
class GameLanguage(private val lang: GameLanguageType){
    override fun toString(): String {
        return lang.toString()
    }
}
