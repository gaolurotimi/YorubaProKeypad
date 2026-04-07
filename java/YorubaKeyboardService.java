package com.yourubapro.keypad;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.view.inputmethod.InputConnection;
import android.view.View;
import java.util.HashMap;

public class YorubaKeyboardService extends InputMethodService implements KeyboardView.OnKeyboardActionListener {

    private KeyboardView kv;
    private Keyboard keyboard;
    private HashMap<String, String[]> toneMap = new HashMap<>();

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize the Do-Re-Mi Logic
        // Format: { "letter": ["do", "re", "mi"] }
        toneMap.put("a", new String[]{"à", "a", "á"});
        toneMap.put("e", new String[]{"è", "e", "é"});
        toneMap.put("ẹ", new String[]{"ẹ̀", "ẹ", "ẹ́"});
        toneMap.put("i", new String[]{"ì", "i", "í"});
        toneMap.put("o", new String[]{"ò", "o", "ó"});
        toneMap.put("ọ", new String[]{"ọ̀", "ọ", "ọ́"});
        toneMap.put("u", new String[]{"ù", "u", "ú"});
    }

    @Override
    public View onCreateInputView() {
        kv = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard_view, null);
        keyboard = new Keyboard(this, R.xml.keyboard_layout);
        kv.setKeyboard(keyboard);
        kv.setOnKeyboardActionListener(this);
        return kv;
    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) return;

        switch (primaryCode) {
            case 1001: // "DO" Tone Key (\)
                applyTone(0);
                break;
            case 1002: // "RE" Tone Key (-)
                applyTone(1);
                break;
            case 1003: // "MI" Tone Key (/)
                applyTone(2);
                break;
            case Keyboard.KEYCODE_DELETE:
                ic.deleteSurroundingText(1, 0);
                break;
            default:
                char code = (char) primaryCode;
                ic.commitText(String.valueOf(code), 1);
        }
    }

    private void applyTone(int toneIndex) {
        InputConnection ic = getCurrentInputConnection();
        // Get the last character typed
        CharSequence lastChar = ic.getTextBeforeCursor(1, 0);
        if (lastChar != null && toneMap.containsKey(lastChar.toString())) {
            // Delete the plain vowel
            ic.deleteSurroundingText(1, 0);
            // Replace with the tone-marked version from our map
            String markedVowel = toneMap.get(lastChar.toString())[toneIndex];
            ic.commitText(markedVowel, 1);
        }
    }

    // Required Boilerplate
    @Override public void onPress(int primaryCode) {}
    @Override public void onRelease(int primaryCode) {}
    @Override public void onText(CharSequence text) {}
    @Override public void swipeDown() {}
    @Override public void swipeLeft() {}
    @Override public void swipeRight() {}
    @Override public void swipeUp() {}
}