package `in`.digistorm.aksharam.activities.main.practice

import `in`.digistorm.aksharam.util.logDebug
import java.lang.StringBuilder
import java.util.*

fun generatePracticeString(viewModel: PracticeTabViewModel): String {
    val logTag = "generatePracticeString"
    // TODO: Better error handling
    if (viewModel.practiceType.value?.isEmpty() != false) {
        logDebug(logTag, "Practice type is not known. Cannot generate practice string.")
        return ""
    }

    logDebug(logTag, "Generating practice text.")
    // get all letters of current language, category-wise
    val vowels = viewModel.getLanguageData().vowels
    val consonants = viewModel.getLanguageData().consonants
    val ligatures = viewModel.getLanguageData().ligatures
    val signs = viewModel.getLanguageData().diacritics
    val chillu = viewModel.getLanguageData().chillu
    val random = Random()
    var practiceString = StringBuilder()

    // Special variable to hold the Virama.
    // Useful to detect chillu letters in Malayalam
    val virama = viewModel.getLanguageData().virama
    when (viewModel.practiceType.value?.lowercase()) {
        // Let's construct a made-up word in current language
        // First letter can be a vowel or consonant (not a sign)
        // Second letter onwards can be a vowel sign or consonant (not a vowel)
        "random words" -> {
            var i = 0
            while (i < 5) {
                // What should be the length of a word?
                val wordLength = random.nextInt(6) + 3 // length is 3 to 6 + 3
                logDebug(logTag, "Constructing word of length $wordLength")

                // Choose the first character. Vowel or consonant
                if (random.nextBoolean()) practiceString.append(vowels[random.nextInt(vowels.size)]) else {
                    practiceString.append(consonants[random.nextInt(consonants.size)])
                }
                var j = 1
                while (j < wordLength) {
                    var nextChar: String
                    val prevChar = practiceString.substring(
                        practiceString.length - 1,
                        practiceString.length
                    )

                    // 20% chance that the next character is a joint letter
                    if (random.nextInt(100) < 21 && prevChar != virama) {
                        // for malayalam, there is also a chance the next character is a chillu
                        nextChar =
                            if (viewModel.getLanguage().equals("malayalam", ignoreCase = true)) {
                                if (random.nextInt(100) < 31)
                                    chillu[random.nextInt(chillu.size)]
                                else  // Since it's malayalam, we can just get one of the ligatures at random
                                    ligatures[random.nextInt(ligatures.size)]
                            } else  // construct ligature
                                consonants[random.nextInt(consonants.size)] + virama + consonants[random.nextInt(consonants.size)]
                    } else if (vowels.contains(prevChar) || signs.contains(prevChar)) {
                        // ...next char must be a consonant
                        nextChar = consonants[random.nextInt(consonants.size)]
                    } else {
                        // if previous character was a consonant, next character can be a
                        // consonant or a sign
                        val randomChoice = if (random.nextBoolean()) consonants else signs
                        do {
                            nextChar = randomChoice[random.nextInt(randomChoice.size)]
                        } while (prevChar == virama && nextChar == virama)
                    }
                    practiceString.append(nextChar)
                    j++
                }
                practiceString.append(" ")
                i++
            }
        }
        "random ligatures" -> {
            var i = 0
            while (i < 10) {

                // choose a random consonant
                practiceString.append(consonants[random.nextInt(consonants.size)])
                    .append(virama)
                    .append(consonants[random.nextInt(consonants.size)])
                practiceString.append(" ")
                i++
            }
        }
        "signs" -> {
            // predecessor is the consonant or ligature to combine the sign with
            var predecessor = ""
            var i = 0
            while (i < 10) {
                when (random.nextInt(2)) {
                    0 -> predecessor = consonants[random.nextInt(consonants.size)]
                    1 -> {
                        val letter = ligatures[random.nextInt(ligatures.size)]
                        logDebug(logTag, "letter chosen: $letter")

                        // Following for ligatures that have combining rules.
                        // Certain consonants (right now, only ರ್ in Kannada), form different types of
                        // ligatures before and after a consonant.
                        val isCombineAfter = viewModel.getLanguageData()
                            .getLetterDefinition(letter)?.shouldCombineAfter() ?: false
                        val isCombineBefore = viewModel.getLanguageData()
                            .getLetterDefinition(letter)?.shouldCombineBefore() ?: false
                        val base = viewModel.getLanguageData().getLetterDefinition(letter)?.base

                        // Find the base of this ligature, if any.
                        // "base" of a ligature is the actual consonant used for combining with
                        // the vowel sign
                        predecessor = if (base == null || base.isEmpty()) letter else base
                        logDebug(logTag, "base: $predecessor")
                        if (isCombineAfter && isCombineBefore) {
                            // If letter can be combined before and after another letter,
                            // do one at random.
                            predecessor =
                                if (random.nextBoolean()) consonants[random.nextInt(consonants.size)] +
                                        virama + predecessor else predecessor + virama + consonants[random.nextInt(
                                    consonants.size
                                )]
                        } else if (isCombineAfter) {
                            predecessor = consonants[random.nextInt(consonants.size)] + virama + predecessor
                        } else if (isCombineBefore) {
                            predecessor = predecessor + virama + consonants[random.nextInt(
                                consonants.size
                            )]
                        }
                    }
                }
                // what happens if sign selected is a virama?
                val sign: String = signs[random.nextInt(signs.size)]
                practiceString.append(predecessor).append(sign).append(" ")
                i++
            }
        }
        "ligatures" -> {
            var i = 0
            while (i < 10) {
                val ligature = ligatures[random.nextInt(ligatures.size)]
                logDebug(logTag, "Ligature obtained: $ligature")
                // nextChar is base char if a base exists in the data file.
                // if there is no base in the data file, nextChar equals ligature (variable above)
                val nextChar = viewModel.getLanguageData().getLetterDefinition(ligature)?.base ?: ligature

                // get the rules for combining this letter if such rule exists
                val isCombineAfter = viewModel.getLanguageData()
                    .getLetterDefinition(ligature)?.shouldCombineAfter() ?: false
                val isCombineBefore = viewModel.getLanguageData()
                    .getLetterDefinition(ligature)?.shouldCombineBefore() ?: false
                if (isCombineAfter && isCombineBefore) {
                    // randomly select either combineBefore or combineAfter
                    when (random.nextInt(2)) {
                        0 -> practiceString.append(consonants[random.nextInt(consonants.size)])
                            .append(virama).append(nextChar).append(" ")
                        1 -> practiceString.append(nextChar).append(virama).append(
                            consonants[random.nextInt(consonants.size)]
                        ).append(" ")
                    }
                } else if (isCombineAfter) {
                    practiceString.append(consonants[random.nextInt(consonants.size)])
                        .append(virama)
                        .append(nextChar).append(" ")
                } else if (isCombineBefore) {
                    practiceString.append(nextChar).append(virama).append(nextChar)
                        .append(consonants[random.nextInt(consonants.size)])
                } else {
                    practiceString.append(nextChar).append(" ")
                }
                i++
            }
        }
        "vowels" -> {
            var i = 0
            while (i < 10) {
                practiceString.append(vowels[random.nextInt(vowels.size)]).append(" ")
                i++
            }
        }
        "consonants" -> {
            var i = 0
            while (i < 10) {
                practiceString.append(consonants[random.nextInt(consonants.size)]).append(" ")
                i++
            }
        }
        "chillu" -> {
            var i = 0
            while (i < 10) {
                practiceString.append(chillu[random.nextInt(chillu.size)]).append(" ")
                i++
            }
        }
    }

    // strip the last " " from practiceString
    practiceString = StringBuilder(
        practiceString.substring(
            0,
            practiceString.length - 1
        )
    )
    return practiceString.toString()
}
