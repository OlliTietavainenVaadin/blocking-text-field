package org.vaadin.addons.client;

import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.VConsole;

public class BlockingUtils {

    public static boolean isValueValid(String newText, boolean doLengthCheck, boolean isDeleting, TextBoxBase text, ValidationState state) {

        if (doLengthCheck && !BlockingUtils.withinLengthBounds(newText, text, state.minCharacterCount, state.maxCharacterCount)) {
            return false;
        }
        if (state.allAllowed) {
            return true;
        }
        // length check passed -> deleting is okay
        if (isDeleting) {
            return true;
        }
        // using looping because GWT regex support is not great
        // if alphanumerics are not allowed and one is found, not valid

        // if alphanumerics are not allowed but limited special characters are:
        if (!BlockingUtils.containsOnlyFromList(newText, state.combinedAllowedCharacters)) {
            return false;
        } else {
            //VConsole.log(newText + " is valid, only characters from " + allowedCharacters);
        }

        return true;
    }

    public static void cancelKey(DomEvent event) {
        event.preventDefault();
    }

    public static boolean containsOnlyFromList(String text, String listToCheck) {
        for (int i = 0; i < text.length(); i++) {
            if (listToCheck.indexOf(text.charAt(i)) < 0) {
                return false;
            }
        }
        return true;
    }

    public static String removeDisallowedCharacters(String original, String allowed) {
        StringBuilder result = new StringBuilder("");
        for (int i = 0; i < original.length(); i++) {
            if (allowed.contains("" + original.charAt(i))) {
                result.append(original.charAt(i));
            }
        }
        return result.toString();
    }

    public static void handlePaste(TextBoxBase textBox, final String pasteContent, ValidationState state) {
        if (pasteContent == null || "".equals(pasteContent)) {
            VConsole.log("Detected paste event, but received no paste content from clipboard");
            return;
        }
        String textGoingToBody = pasteContent;
        if (!state.allAllowed) {
            if (!containsOnlyFromList(textGoingToBody, state.combinedAllowedCharacters)) {
                textGoingToBody = removeDisallowedCharacters(textGoingToBody, state.combinedAllowedCharacters);
            }
        }
        String newText = afterInsertion(textGoingToBody, textBox);
        if (withinLengthBounds(newText, textBox, state.minCharacterCount, state.maxCharacterCount)) {
            textBox.setText(newText);
        }
    }

    public static String afterInsertion(String textGoingToBody, TextBoxBase textBox) {
        StringBuilder builder = new StringBuilder(textBox.getValue());

        // remove selected
        if (textBox.getSelectionLength() > 0) {
            builder.delete(textBox.getCursorPos(), textBox.getCursorPos() + textBox.getSelectionLength());
        }
        builder.insert(textBox.getCursorPos(), textGoingToBody);

        return builder.toString();
    }

    public static boolean isControlKey(int keyCode) {
        BrowserInfo browser = BrowserInfo.get();
        // Firefox handles left/right etc. differently
        if (browser.isFirefox()) {
            switch (keyCode) {
            case KeyCodes.KEY_LEFT:
            case KeyCodes.KEY_RIGHT:
            case KeyCodes.KEY_END:
            case KeyCodes.KEY_DELETE: {
                    return true;
                }
            }
        }
        switch (keyCode) {
        case KeyCodes.KEY_BACKSPACE:
        case KeyCodes.KEY_TAB:
        case KeyCodes.KEY_ENTER:
        case KeyCodes.KEY_ESCAPE: {
                return true;
            }
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

    public static boolean isFireFoxKeyboardCopyPaste(KeyPressEvent event) {
        if (BrowserInfo.get().isFirefox() && event.isControlKeyDown()) {
            return Character.toString(event.getCharCode()).toLowerCase().equals("c") || Character.toString(event.getCharCode()).toLowerCase().equals("v");
        }
        return false;
    }
}
