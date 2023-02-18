package `in`.digistorm.aksharam.language

import `in`.digistorm.aksharam.config.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class LanguageViewModel @Inject constructor(
    private val repo: LanguageRepository
) : BaseViewModel() {

    fun isLanguageInitialised(): Boolean {
        return false
    }

    val supportedLanguage = flow { emit(repo.getSupportLanguages()) }.asScopedLiveState()
}