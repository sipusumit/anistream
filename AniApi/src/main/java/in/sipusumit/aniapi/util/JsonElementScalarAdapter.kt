package `in`.sipusumit.aniapi.util

import com.apollographql.apollo.annotations.ApolloInternal
import com.apollographql.apollo.api.Adapter
import com.apollographql.apollo.api.CustomScalarAdapters
import com.apollographql.apollo.api.json.JsonReader
import com.apollographql.apollo.api.json.JsonWriter
import com.apollographql.apollo.api.json.readAny
import com.apollographql.apollo.api.json.writeAny
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonArray

object JsonElementScalarAdapter : Adapter<JsonElement> {

    @OptIn(ApolloInternal::class)
    override fun fromJson(reader: JsonReader, customScalarAdapters: CustomScalarAdapters): JsonElement {
        return reader.readAny().toJsonElement()
    }

    override fun toJson(writer: JsonWriter, customScalarAdapters: CustomScalarAdapters, value: JsonElement) {
        writer.writeAny(value)
    }
}

private fun Any?.toJsonElement(): JsonElement = when (this) {
    null -> JsonNull
    is Boolean -> JsonPrimitive(this)
    is Number -> JsonPrimitive(this)
    is String -> JsonPrimitive(this)
    is Map<*, *> -> JsonObject(
        this.entries.associate { it.key.toString() to it.value.toJsonElement() }
    )
    is List<*> -> JsonArray(this.map { it.toJsonElement() })
    else -> JsonPrimitive(this.toString())
}
