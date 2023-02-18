package `in`.digistorm.aksharam.language

data class SupportedLanguage(
    val name: String
) {

    val language: String
        get() = name.substring(0, 1).uppercase() + name.substring(1, name.length - ".json".length)
}
