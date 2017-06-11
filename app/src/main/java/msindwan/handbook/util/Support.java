/*
 * Copyright (C) 2017 Mayank Sindwani
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package msindwan.handbook.util;

import android.content.res.ColorStateList;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatButton;
import android.widget.Button;

import java.util.HashMap;

/**
 * Support:
 * Defines a utility class for android support-related helper methods.
 */
public class Support {

    /**
     * Sets the background tint for a button.
     *
     * @param button The button to tint.
     * @param tint The color state list to use.
     */
    public static void setButtonTint(Button button, ColorStateList tint) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP && button instanceof AppCompatButton) {
            ((AppCompatButton) button).setSupportBackgroundTintList(tint);
        } else {
            ViewCompat.setBackgroundTintList(button, tint);
        }
    }

    /**
     * Support wrapper for tts.
     *
     * @param tts The text-to-speech interface.
     * @param text The text to recite.
     * @param queue The speak queue flag.
     * @param utteranceId The utterance ID.
     */
    public static void speak(TextToSpeech tts, String text, int queue, String utteranceId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(text, queue, null, utteranceId);
        } else {
            HashMap<String, String> params = new HashMap<>();
            params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId);
            //noinspection deprecation
            tts.speak(text, queue, params);
        }
    }

    /**
     * Support wrapper for tts
     *
     * @param tts The text-to-speech interface.
     * @param text The text to recite.
     * @param queue The speak queue flag.
     */
    public static void speak(TextToSpeech tts, String text, int queue) {
        Support.speak(tts, text, queue, null);
    }
}
