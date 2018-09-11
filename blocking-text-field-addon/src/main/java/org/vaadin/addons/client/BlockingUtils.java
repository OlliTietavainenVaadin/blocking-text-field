package org.vaadin.addons.client;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.vaadin.client.BrowserInfo;

public class BlockingUtils {

    public static boolean containsOnlyFromList(String text, String listToCheck) {
        for (int i = 0; i < text.length(); i++) {
            if (listToCheck.indexOf(text.charAt(i)) < 0) {
                return false;
            }
        }
        return true;
    }

    public static boolean isCopyOrPasteEvent(KeyPressEvent evt) {
        if (evt.isControlKeyDown()) {
            return Character.toString(evt.getCharCode()).toLowerCase().equals("c") || Character.toString(evt.getCharCode()).toLowerCase().equals("v");
        }
        return false;
    }

    public static boolean isControlKey(int keyCode) {
        BrowserInfo browser = BrowserInfo.get();
        // Firefox handles left/right differently
        if (browser.isFirefox()) {
            switch (keyCode) {
            case KeyCodes.KEY_LEFT:
            case KeyCodes.KEY_RIGHT:
            case KeyCodes.KEY_HOME:
            case KeyCodes.KEY_END:
                return true;
            }
        }
        switch (keyCode) {
        case KeyCodes.KEY_BACKSPACE:
        case KeyCodes.KEY_TAB:
        case KeyCodes.KEY_ENTER:
        case KeyCodes.KEY_ESCAPE:
            return true;
        }

        return false;
    }

    public static boolean withinLengthBounds(String newText, TextBoxBase text, int minCharacterCount, int maxCharacterCount) {
        String previous = text.getText() == null ? "" : text.getText();
        if (minCharacterCount >= 0) {
            // if new text would be shorter than minimum and length is not increasing, not within bounds
            if ((newText.length() < minCharacterCount) && (previous.length() >= newText.length())) {
                return false;
            }
        }
        if (maxCharacterCount >= 0) {
            // if new text would be longer than maximum and length is not decreasing, not within bounds
            if ((newText.length() > maxCharacterCount) && (previous.length() <= newText.length())) {
                return false;
            }
        }
        return true;
    }

    public static String valueAfterKeyPress(char charCode, TextBoxBase text) {
        int index = text.getCursorPos();
        String previousText = text.getText();

        if (text.getSelectionLength() > 0) {
            return previousText.substring(0, index) + charCode + previousText.substring(index + text.getSelectionLength(), previousText.length());
        } else {
            return previousText.substring(0, index) + charCode + previousText.substring(index, previousText.length());
        }
    }

    public static boolean isIgnorableOnKeyDown(int keyCode) {
        switch (keyCode) {
        case KeyCodes.KEY_SHIFT:
        case KeyCodes.KEY_ALT:
        case KeyCodes.KEY_CTRL:
        case KeyCodes.KEY_LEFT:
        case KeyCodes.KEY_RIGHT:
        case KeyCodes.KEY_UP:
        case KeyCodes.KEY_DOWN:
        case KeyCodes.KEY_TAB:
        case KeyCodes.KEY_ESCAPE:
        case KeyCodes.KEY_HOME:
        case KeyCodes.KEY_END:
            return true;
        }
        return false;
    }

}
