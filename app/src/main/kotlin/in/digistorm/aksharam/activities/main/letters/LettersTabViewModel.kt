/*
 * Copyright (c) 2022 Alan M Varghese <alan@digistorm.in>
 *
 * This files is part of Aksharam, a script teaching app for Indic
 * languages.
 *
 * Aksharam is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Aksharam is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even teh implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package `in`.digistorm.aksharam.activities.main.letters

import `in`.digistorm.aksharam.activities.main.models.AksharamViewModel
import android.app.Application

import android.content.Context
import android.widget.ArrayAdapter
import androidx.databinding.Bindable
import androidx.databinding.BindingAdapter
import androidx.lifecycle.*
import androidx.navigation.NavDirections
import androidx.recyclerview.widget.RecyclerView
import `in`.digistorm.aksharam.BR
import `in`.digistorm.aksharam.activities.main.TabbedViewsDirections
import `in`.digistorm.aksharam.util.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LettersTabViewModel: ViewModel() {
    private val logTag = javaClass.simpleName

    private lateinit var aksharamViewModel: AksharamViewModel
    private lateinit var languageDataFetcher: (String) -> Language
    private lateinit var setConvertTo: (String) -> Unit
    var downloadedLanguages: MutableLiveData<ArrayList<String>> = MutableLiveData(ArrayList())

    var _languageSelected: MutableLiveData<String> = MutableLiveData()
        private set
    var languageSelected: String
        get() {
            if(_languageSelected.value == null) {
                _languageSelected.value = downloadedLanguages.value?.first()
            }
            return _languageSelected.value!!
        }
        set(value) {
            logDebug(logTag, "Language live data set to value: $value")
            _languageSelected.value = value
        }

    var language: LiveData<Language> = _languageSelected.map { lang ->
        logDebug(logTag, "Fetching data for $lang")
        val language: Language = languageDataFetcher(lang)
        aksharamViewModel.language.value = language
        language
    }

    var targetLanguageList: LiveData<ArrayList<String>> = language.map { language ->
        logDebug(logTag, "Transforming ${language.language} to a live data of target languages")
        targetLanguage = language.supportedLanguagesForTransliteration.first()
        setConvertTo(targetLanguage)
        language.supportedLanguagesForTransliteration
    }

    // The target language string as displayed by lettersTabTransSpinner
    var targetLanguageLiveData: MutableLiveData<String> = MutableLiveData()
    var targetLanguage: String
        get() {
            return targetLanguageLiveData.value!!
        }
        set(value) {
            logDebug(logTag, "Setting target language to $value")
            targetLanguageLiveData.value = value
        }

    val lettersCategoryWise: LiveData<List<Map<String, ArrayList<Pair<String, String>>>>> = language.map {language ->
        logDebug(logTag, "Generating letters category wise")
        val categories = mutableListOf<Map<String, ArrayList<Pair<String, String>>>>()
        // [{"vowels": ["a", "e",...]}, {"consonants":: ["b", "c", "d",...]}, ...]
        language.lettersCategoryWise.forEach { (category, letters) ->
            val transliteratedLetterPairs: ArrayList<Pair<String, String>> = ArrayList()
            letters.forEach { letter ->
                transliteratedLetterPairs.add(letter to transliterate(letter, targetLanguage, language))
            }
            categories.add(mapOf(category to transliteratedLetterPairs))
        }
        logDebug(logTag, "Category list created: $categories")
        // categoryListAdapter.submitList(categories as List<Map<String, List<Pair<String, String>>>>?)
        categories
    }
    var categoryListAdapter: LetterCategoryAdapter = LetterCategoryAdapter(::letterOnLongClickAction)

    private fun letterOnLongClickAction(letter: String): NavDirections {
        logDebug(logTag, "$letter long clicked!")

        val category = language.value?.getLetterDefinition(letter)?.type!!.replaceFirstChar {
            if(it.isLowerCase())
                it.titlecase()
            else
                it.toString()
        }
        return TabbedViewsDirections.actionTabbedViewsFragmentToLetterInfoFragment(
            letter = letter,
            category = category,
            targetLanguage = targetLanguage,
        )
    }

    fun initialise(
        activityViewModel: AksharamViewModel,
        downloadedLanguages: ArrayList<String>,
        languageDataFetcher: (String) -> Language,
        setConvertTo: (String) -> Unit,
    ) {
        logDebug(logTag, "Initialising...")
        this.aksharamViewModel = activityViewModel
        this.languageDataFetcher = languageDataFetcher
        this.downloadedLanguages.value = downloadedLanguages
        this.setConvertTo = setConvertTo

        languageSelected = downloadedLanguages.first()
        logDebug(logTag, "Done initialising.")
        viewModelScope.launch {
            delay(3000)
            logDebug(logTag, lettersCategoryWise.value.toString())
        }
    }
}