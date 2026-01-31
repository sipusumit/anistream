package `in`.sipusumit.aniapi.core

/**
 * Represents the language / translation mode
 * for anime content.
 */
enum class LanguageMode(string: String) {

    /** Subtitled (Japanese audio + subs) */
    SUB("sub"),

    /** Dubbed (localized audio) */
    DUB("dub"),

    /** Raw (original broadcast, no subs) */
    RAW("raw");

    /**
     * Returns lowercase value expected by APIs.
     */
    val apiValue: String
        get() = name.lowercase()
}