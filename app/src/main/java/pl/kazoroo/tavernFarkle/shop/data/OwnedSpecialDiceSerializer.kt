package pl.kazoroo.tavernFarkle.shop.data

import androidx.datastore.core.Serializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import pl.kazoroo.tavernFarkle.shop.data.model.OwnedSpecialDice
import java.io.InputStream
import java.io.OutputStream

object OwnedSpecialDiceSerializer: Serializer<List<OwnedSpecialDice>> {
    override val defaultValue: List<OwnedSpecialDice>
        get() = emptyList()

    override suspend fun readFrom(input: InputStream): List<OwnedSpecialDice> {
        return try {
            Json.decodeFromString(
                deserializer = ListSerializer(OwnedSpecialDice.serializer()),
                string = input.readBytes().decodeToString()
            )
        } catch (e: SerializationException) {
            e.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: List<OwnedSpecialDice>, output: OutputStream) {
        output.write(
            Json.encodeToString(
                serializer = ListSerializer(OwnedSpecialDice.serializer()),
                value = t
            ).encodeToByteArray()
        )
    }
}