package pl.kazoroo.tavernFarkle.core.data.local

import kotlin.reflect.KClass

enum class UserDataKey(val type: KClass<*>) {
    COINS(String::class),
    IS_SOUND_ENABLED(Boolean::class),
    IS_MUSIC_ENABLED(Boolean::class),
    IS_FIRST_LAUNCH(Boolean::class)
}