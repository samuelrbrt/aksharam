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

package in.digistorm.aksharam;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

// This class is responsible for the actual transliteration
public class Transliterator {

    // The JSON mapping used to transliterate
    private final JSONObject langData;
    private final String logTag = "Transliterator";

    public Transliterator(JSONObject langData) {
        this.langData = langData;
    }

    // Transliterate the input string using the mapping and return the transliterated string
    // str is the string that needs to be converted
    // targetLanguage is the language to which the string needs to be converted
    public String transliterate(String str, String targetLanguage) {
       StringBuilder out = new StringBuilder();
       String index;

       // Process the string character by character
       for (char ch: str.toCharArray()) {
           index = "" + ch;
           // + "." + targetLanguage;
           Log.d(logTag, "Looking for index " + index);
           try {
               if (langData.has(index))
                   out = out.append(langData.optJSONObject(index)
                                            .getJSONArray(targetLanguage)
                                            .getString(0));
               else
                   out = out.append(ch);
           } catch (JSONException e) {
               Log.d(logTag, "Error while looking up transliteration data");
               e.printStackTrace();
           }
       }
       Log.d(logTag, "Constructed string: " + out.toString());
       return out.toString();
    }
}
