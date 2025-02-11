package in.digistorm.aksharam.activities.main.letters;

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

import android.annotation.SuppressLint;
import android.graphics.Point;
import android.os.Bundle;
import android.text.Html;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.gridlayout.widget.GridLayout;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.Map;

import in.digistorm.aksharam.R;
import in.digistorm.aksharam.util.AutoAdjustingTextView;
import in.digistorm.aksharam.util.Log;
import in.digistorm.aksharam.util.Transliterator;

public class LetterInfoFragment extends Fragment {
    private final String logTag = getClass().getSimpleName();

    private String currentLetter = "";

    private LettersTabViewModel viewModel;

    public LetterInfoFragment() {
        super();
    }

    public static LetterInfoFragment newInstance(String letter) {
        LetterInfoFragment letterInfoFragment = new LetterInfoFragment();

        Bundle args = new Bundle();
        args.putString("letter", letter);
        letterInfoFragment.setArguments(args);

        return letterInfoFragment;
    }

    // Set up the LetterInfo dialog
    @SuppressLint("SetTextI18n")
    private void setUp(View v, LayoutInflater inflater) {
        if(getArguments() != null)
            currentLetter = getArguments().getString("letter");
        else {
            Log.d(logTag, "Null arguments in Setup(View, LayoutInflater)");
            return ;
        }
        Log.d(logTag, "Showing info dialog for: " + currentLetter);
        Transliterator tr = viewModel.getTransliterator();
        setText(v.findViewById(R.id.letterInfoHeadingTV), currentLetter);
        setText(v.findViewById(R.id.letterInfoTransliteratedHeadingTV),
                tr.transliterate(currentLetter, viewModel.getTargetLanguage()));

        Map<String, Map<String, String>> letterExamples = viewModel.getTransliterator().getLanguage()
                .getLetterDefinition(currentLetter).getExamples();

        // We pack the examples into the WordAndMeaning Layout in letter_info.xml layout file
        LinearLayout letterInfoWordAndMeaningLL =
                 v.findViewById(R.id.letterInfoWordAndMeaningLL);

        // If there are no examples, hide this section
        if (letterExamples == null || letterExamples.toString().equals("")) {
            v.findViewById(R.id.letterInfoWordsTV).setVisibility(View.GONE);
            v.findViewById(R.id.letterInfoMeaningTV).setVisibility(View.GONE);
        } else {
            for (Map.Entry<String, Map<String, String>> word: letterExamples.entrySet()) {
                // Don't attach to root. If attached, we wouldn't be able to find the TextView
                // ID's below
                View wordsAndMeaningView = inflater.inflate(R.layout.word_and_meaning,
                        letterInfoWordAndMeaningLL, false);

                int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 4,
                        getResources().getDisplayMetrics());
                wordsAndMeaningView.setPadding(px, px, px, px);
                String meaning = word.getValue().get(
                        viewModel.getTransliterator().getLanguage().getLanguageCode(viewModel.getTargetLanguage()));
                Log.d(logTag, "targetLanguage: " + viewModel.getTargetLanguage() + "; Word: " + word.getKey() + "; meaning: " + meaning);
                setText(wordsAndMeaningView.findViewById(R.id.wordAndMeaningWordTV), word.getKey());
                setText(wordsAndMeaningView.findViewById(R.id.wordAndMeaningMeaningTV), meaning);
                setText(wordsAndMeaningView.findViewById(R.id.wordAndMeaningTransliterationTV),
                        viewModel.getTransliterator().transliterate(word.getKey(),
                                viewModel.getTargetLanguage()));
                letterInfoWordAndMeaningLL.addView(wordsAndMeaningView);
            }
        }

        // Check if extra info exists for this letter
        Map<String, String> letterInfo = viewModel.getTransliterator()
                .getLanguage().getLetterDefinition(currentLetter).getInfo();
        TextView letterInfoInfoTV = v.findViewById(R.id.letterInfoInfoTV);
        if(letterInfo == null || letterInfo.isEmpty()) {
            Log.d(logTag, "No additional info for " + currentLetter
                    + " was found. Hiding UI element.");
            letterInfoInfoTV.setVisibility(View.GONE);
        }
        else {
            letterInfoInfoTV.setText(Html.fromHtml(letterInfo.get("en")));
        }

        // For consonants and ligatures, show examples of how they can combine with
        // vowel diacritics. For consonants, display possible ligatures with other
        // consonants if ligatures_auto_generatable
        String category = viewModel.getTransliterator()
                .getLanguage().getLetterDefinition(currentLetter)
                .getType();
        boolean showDiacriticExamples = true;
        if(category != null && !viewModel.getTransliterator()
                .getLanguage().getLetterDefinition(currentLetter).shouldExcludeCombiExamples()
                && (category.equalsIgnoreCase("consonants")
                || category.equalsIgnoreCase("ligatures"))) {
            displaySignConsonantCombinations(v, category);
            if(category.equalsIgnoreCase("consonants"))
                if(viewModel.getTransliterator().getLanguage().areLigaturesAutoGeneratable())
                    displayLigatures(v);
        }
        else
            showDiacriticExamples = false;

        // For a sign, display how it combines with each consonant
        if(category != null && (category.equalsIgnoreCase("signs")))
            displaySignConsonantCombinations(v, category);
        else if(!showDiacriticExamples) {
            v.findViewById(R.id.diacriticSelectorHintTV).setVisibility(View.GONE);
            v.findViewById(R.id.diacriticExamplesGL).setVisibility(View.GONE);
        }
    }

    // Lets try to combine current letter with all letters
    public void displayLigatures(View v) {
        /* ligatureAfterHintTV, ligaturesGLAfter, linearLayoutAfter, etc are all for
         * ligatures formed when currentLetter appears *after* the virama.
         * ligatureBeforeHintTV, ligaturesGLBefore, linearLayoutBefore, etc are all for
         * ligatures formed when currentLetter appears *before* the virama.
         * TODO: some way to reduce code duplication?
         */
        TextView ligatureAfterHintTV = v.findViewById(R.id.letterInfoLigaturesAfterTV);
        ligatureAfterHintTV.setVisibility(View.VISIBLE);
        TextView ligatureBeforeHintTV = v.findViewById(R.id.letterInfoLigaturesBeforeTV);
        ligatureBeforeHintTV.setVisibility(View.VISIBLE);

        ArrayList<String> consonants;
        consonants = viewModel.getTransliterator().getLanguage().getConsonants();
        String virama = viewModel.getTransliterator().getLanguage().getVirama();

        // v.findViewById(R.id.letterInfoLigaturesLL).setVisibility(View.VISIBLE);
        GridLayout ligaturesGLBefore = v.findViewById(R.id.letterInfoLigaturesBeforeGL);
        ligaturesGLBefore.removeAllViews();
        ligaturesGLBefore.setVisibility(View.VISIBLE);
        GridLayout ligaturesGLAfter = v.findViewById(R.id.letterInfoLigaturesAfterGL);
        ligaturesGLAfter.removeAllViews();
        ligaturesGLAfter.setVisibility(View.VISIBLE);

        ligatureBeforeHintTV.setText(getString(R.string.letter_info_ligature_consonant_before,
                currentLetter, currentLetter, virama));
        ligatureAfterHintTV.setText(getString(R.string.letter_info_ligature_consonant_after,
                currentLetter, virama, currentLetter));

        Point size = new Point();
        requireActivity().getWindowManager().getDefaultDisplay().getSize(size);
        int i = 0, cols = 5;
        for(String consonant: consonants) {
            String ligatureAfter = consonant + virama + currentLetter;
            String ligatureBefore = currentLetter + virama + consonant;

            // Can we use the same row, col specs for both GridLayouts?
            GridLayout.Spec rowSpec = GridLayout.spec(i / cols, GridLayout.CENTER);
            GridLayout.Spec colSpec = GridLayout.spec(i % cols, GridLayout.CENTER);

            // UI elements for ligatureBefore
            AutoAdjustingTextView textView = new AutoAdjustingTextView(requireContext());
            textView.setGravity(Gravity.CENTER);
            GridLayout.LayoutParams tvLayoutParams = new GridLayout.LayoutParams(rowSpec, colSpec);
            tvLayoutParams.width = size.x / 6;
            textView.setLayoutParams(tvLayoutParams);
            int px = getResources().getDimensionPixelSize(R.dimen.letter_grid_tv_margin);
            tvLayoutParams.setMargins(px, px, px, px);
            px = getResources().getDimensionPixelSize(R.dimen.letter_grid_tv_padding);
            textView.setPadding(px, px, px, px);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
            textView.setText(ligatureBefore);
            ligaturesGLBefore.addView(textView, tvLayoutParams);

            // UI elements for ligatureAfter
            textView = new AutoAdjustingTextView(requireContext());
            textView.setGravity(Gravity.CENTER);
            px = getResources().getDimensionPixelSize(R.dimen.letter_grid_tv_margin);
            tvLayoutParams.setMargins(px, px, px, px);
            textView.setLayoutParams(tvLayoutParams);
            px = getResources().getDimensionPixelSize(R.dimen.letter_grid_tv_padding);
            textView.setPadding(px, px, px, px);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            textView.setText(ligatureAfter);
            ligaturesGLAfter.addView(textView, tvLayoutParams);

            i++;
        }
    }

    @SuppressLint("SetTextI18n")
    public void displaySignConsonantCombinations(View v, String type) {
        TextView diacriticSelectorHintTV = v.findViewById(R.id.diacriticSelectorHintTV);
        ArrayList<String> items = null;
        // We need to display examples for a sign
        if(type.equalsIgnoreCase("signs")) {
            diacriticSelectorHintTV.setText(getString(R.string.consonants_with_diacritic,
                    currentLetter));
            items = viewModel.getTransliterator().getLanguage().getConsonants();
            items.addAll(viewModel.getTransliterator().getLanguage().getLigatures());
        }
        // we need to display examples for a consonant/ligature
        else if(type.equalsIgnoreCase("consonants")
                || type.equalsIgnoreCase("ligatures")) {
            diacriticSelectorHintTV.setText(getString(R.string.diacritics_with_consonant,
                    currentLetter));
            items = viewModel.getTransliterator().getLanguage().getDiacritics();
        }

        if(items == null)
            return;

        Log.d(logTag, "Items obtained: " + items);

        GridLayout diacriticExamplesGridLayout = v.findViewById(R.id.diacriticExamplesGL);
        diacriticExamplesGridLayout.removeAllViews();

        Point size = new Point();
        Display display = requireActivity().getWindowManager().getDefaultDisplay();
        display.getSize(size);
        int i = 0, cols = 5;
        for(String item: items) {
            GridLayout.Spec rowSpec = GridLayout.spec(i / cols, GridLayout.CENTER);
            GridLayout.Spec colSpec = GridLayout.spec(i % cols, GridLayout.CENTER);
            AutoAdjustingTextView textView = new AutoAdjustingTextView(requireContext());
            textView.setGravity(Gravity.CENTER);
            textView.setSingleLine();
            GridLayout.LayoutParams tvLayoutParams = new GridLayout.LayoutParams(rowSpec, colSpec);
            tvLayoutParams.width = size.x / 6;
            tvLayoutParams.height = getResources().getDimensionPixelSize(R.dimen.letter_grid_height);
            int px = getResources().getDimensionPixelSize(R.dimen.letter_grid_tv_padding);
            tvLayoutParams.setMargins(px, px, px, px);
            textView.setLayoutParams(tvLayoutParams);
            px = getResources().getDimensionPixelSize(R.dimen.letter_grid_tv_margin);
            textView.setPadding(px, px, px, px);
            if(type.equalsIgnoreCase("signs")
                    && !viewModel.getTransliterator().getLanguage()
                       .getLetterDefinition(currentLetter).shouldExcludeCombiExamples()) {
                textView.setText(item + currentLetter);
            }
            else if((type.equalsIgnoreCase("consonants")
                    || type.equalsIgnoreCase("ligatures"))
                    && !viewModel.getTransliterator().getLanguage()
                       .getLetterDefinition(currentLetter).shouldExcludeCombiExamples())
                textView.setText(currentLetter + item);

            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
            // Add the textView and its parent linear layout only if the textview has some content
            if(textView.getText() != null && !textView.getText().equals("")) {
                diacriticExamplesGridLayout.addView(textView, tvLayoutParams);
            }
            i++;
        }
    }

    // A wrapper for setText to add a space at the end, to work around clipping of long characters
    @SuppressLint("SetTextI18n")
    private void setText(TextView tv, String text) {
        String lastChar = text.substring(text.length() - 1);
        switch(lastChar) {
            // Hindi
            case "्":
            case "ँ":
            case "ॅ":
            case "ॉ":

            // Malayalam
            case "്":

            // Kannada
            case "ೃ":
                tv.setText(text + " ");
                break;

            default:
                tv.setText(text);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.letter_info, container, true);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(LettersTabViewModel.class);
        setUp(view, getLayoutInflater());
    }
}