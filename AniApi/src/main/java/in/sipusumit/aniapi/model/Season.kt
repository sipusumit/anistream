package `in`.sipusumit.aniapi.model

import android.annotation.SuppressLint
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


abstract class CaseInsensitiveEnumSerializer<T : Enum<T>>(
    private val values: Array<T>
) : KSerializer<T> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("CaseInsensitiveEnum", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): T {
        val value = decoder.decodeString()
        return values.firstOrNull {
            it.name.equals(value, ignoreCase = true)
        } ?: error("Unknown enum value: $value")
    }

    override fun serialize(encoder: Encoder, value: T) {
        encoder.encodeString(value.name)
    }
}

/**
 * Represents an anime release season.
 */
@SuppressLint("UnsafeOptInUsageError")
@Serializable()
data class Season(
    val year: Int,
    val quarter: SeasonQuarter
)

object SeasonQuarterSerializer :
    CaseInsensitiveEnumSerializer<SeasonQuarter>(SeasonQuarter.entries.toTypedArray())

/**
 * Anime season quarter.
 */
@Serializable(SeasonQuarterSerializer::class)
enum class SeasonQuarter {
    WINTER,
    SPRING,
    SUMMER,
    FALL
}