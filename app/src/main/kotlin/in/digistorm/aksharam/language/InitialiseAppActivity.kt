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

package `in`.digistorm.aksharam.language

import `in`.digistorm.aksharam.InitActivityBinding
import `in`.digistorm.aksharam.R
import `in`.digistorm.aksharam.activities.main.MainActivity
import `in`.digistorm.aksharam.config.state.observe
import `in`.digistorm.aksharam.config.ui.MultiChoiceAdapter
import `in`.digistorm.aksharam.config.ui.MultiChoiceViewHolder
import `in`.digistorm.aksharam.databinding.LangInitItemBinding
import `in`.digistorm.aksharam.util.logDebug
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class InitialiseAppActivity : AppCompatActivity() {

    private val vm: LanguageViewModel by viewModels()
    private lateinit var binding: InitActivityBinding
    private lateinit var adapter: MultiChoiceAdapter<SupportedLanguage, LangInitItemBinding>

    private fun showNoInternetDialog() {
        logDebug("NoInternetDialog", "Showing NoInternetDialog.")
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage(R.string.could_not_download_file)
            .setTitle(R.string.no_internet)
            .setCancelable(false)
            .setNegativeButton(R.string.exit) { dialog: DialogInterface, _: Int ->
                logDebug("NoInternetDialog", "Exit button was clicked")
                dialog.dismiss()
                finish()
            }
            .setPositiveButton(R.string.retry_download) { _: DialogInterface?, _: Int -> }
            .setOnDismissListener {
                logDebug(
                    "NoInternetDialog",
                    "Dialog dismissed!"
                )
            }
        val dialog1 = dialogBuilder.create()
        dialog1.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (vm.isLanguageInitialised()) {
            navigateToHome()
            finish()
            return
        }

        binding = InitActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.vm = vm
        binding.lifecycleOwner = this
        adapter = MultiChoiceAdapter(R.layout.item_init_lang_select) {
            MultiChoiceViewHolder(it)
        }
        binding.languageRV.adapter = adapter

        binding.onProceed = {
            navigateToHome()
        }

        vm.supportedLanguage.observe(this)
            .onSuccess { adapter.updateItems(it); Timber.d("updated-items" + it!![0].language) }
            .onError { Timber.e(it); showNoInternetDialog() }
    }

    private fun navigateToHome() {
        Intent(this, MainActivity::class.java).run {
            startActivity(this)
        }
    }
}
