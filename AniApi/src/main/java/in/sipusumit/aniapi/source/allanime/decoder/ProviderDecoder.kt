package `in`.sipusumit.aniapi.source.allanime.decoder

internal object ProviderDecoder {
    /**
     * Decodes an obfuscated provider id returned by AllAnime.
     *
     * Input:  "175948514e4c4f57..."
     * Output: "apivtwo/clock.json?id=..."
     */
    fun decode(encoded: String): String {
        val cleaned = encoded.removePrefix("--")

        val chars = cleaned
            .chunked(2)
            .mapNotNull { hex ->
                HEX_MAP[hex.lowercase()]
            }

        val decoded = chars.joinToString("")
        return decoded.replace("/clock", "/clock.json")
    }
    private val HEX_MAP: Map<String, Char> = mapOf(
        "79" to 'A', "7a" to 'B', "7b" to 'C', "7c" to 'D', "7d" to 'E',
        "7e" to 'F', "7f" to 'G', "70" to 'H', "71" to 'I', "72" to 'J',
        "73" to 'K', "74" to 'L', "75" to 'M', "76" to 'N', "77" to 'O',
        "68" to 'P', "69" to 'Q', "6a" to 'R', "6b" to 'S', "6c" to 'T',
        "6d" to 'U', "6e" to 'V', "6f" to 'W', "60" to 'X', "61" to 'Y',
        "62" to 'Z',

        "59" to 'a', "5a" to 'b', "5b" to 'c', "5c" to 'd', "5d" to 'e',
        "5e" to 'f', "5f" to 'g', "50" to 'h', "51" to 'i', "52" to 'j',
        "53" to 'k', "54" to 'l', "55" to 'm', "56" to 'n', "57" to 'o',
        "48" to 'p', "49" to 'q', "4a" to 'r', "4b" to 's', "4c" to 't',
        "4d" to 'u', "4e" to 'v', "4f" to 'w', "40" to 'x', "41" to 'y',
        "42" to 'z',

        "08" to '0', "09" to '1', "0a" to '2', "0b" to '3', "0c" to '4',
        "0d" to '5', "0e" to '6', "0f" to '7', "00" to '8', "01" to '9',

        "15" to '-', "16" to '.', "67" to '_', "46" to '~',
        "02" to ':', "17" to '/', "07" to '?', "1b" to '#',
        "63" to '[', "65" to ']', "78" to '@', "19" to '!',
        "1c" to '$', "1e" to '&', "10" to '(', "11" to ')',
        "12" to '*', "13" to '+', "14" to ',', "03" to ';',
        "05" to '=', "1d" to '%'
    )
}
