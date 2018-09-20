package org.vaadin.addons.client;

import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.vaadin.client.BrowserInfo;

public class BlockingUtils {

    public static class BlockingKeyPressHandler implements KeyPressHandler {

        private boolean readOnly;
        private boolean enabled;
        private TextBox text;
        private ValidationState state;
        public BlockingKeyPressHandler(boolean readOnly, boolean enabled, TextBox text, ValidationState state) {
            this.readOnly = readOnly;
            this.enabled = enabled;
            this.text = text;
            this.state = state;
        }

        @Override
        public void onKeyPress(KeyPressEvent event) {
            if (readOnly || !enabled) {
                return;
            }
            if (BlockingUtils.isCopyOrPasteEvent(event)) {
                BlockingUtils.cancelKey(event);
                return;
            }

            int keyCode = event.getNativeEvent().getKeyCode();
            if (BlockingUtils.isControlKey(keyCode)) {
                // treat any control key normally;
                // character deletion via delete, backspace etc. are handled in keyDownHandler
                return;
            }
            String newText = BlockingUtils.valueAfterKeyPress(event.getCharCode(), text);
            boolean isDeleting = false;
            if (newText.length() < text.getText().length()) {
                isDeleting = true;
            }
            if (!BlockingUtils.isValueValid(newText, true, isDeleting, text, state)) {
                //VConsole.log("onKeyPress: " + newText + "is not valid, canceling");
                BlockingUtils.cancelKey(event);
            }
        }
    }

    public static class BlockingKeyDownHandler implements KeyDownHandler {

        private TextBox text;
        private ValidationState state;

        public BlockingKeyDownHandler(TextBox text, ValidationState state) {
            this.text = text;
            this.state = state;
        }

        public int getCursorPos() {
            return text.getCursorPos();
        }

        public String getText() {
            return text.getText();
        }

        @Override
        public void onKeyDown(KeyDownEvent event) {
            // check if keystroke combination would affect validity by deletion / addition
            int keyCode = event.getNativeEvent().getKeyCode();

            if (BlockingUtils.isIgnorableOnKeyDown(keyCode)) {
                //VConsole.log("Ignorable on keydown, no action");
                return;
            }
            boolean doLengthCheck = false;
            boolean isDeleting = false;
            StringBuilder modified = new StringBuilder(getText());
            if ((keyCode == KeyCodes.KEY_DELETE)) {
                doLengthCheck = true;
                isDeleting = true;
                // delete one character or selection
                if (getCursorPos() == getText().length()) {
                    // pressed delete at the end -> does nothing
                    return;
                }
                if (text.getSelectionLength() > 0) {
                    modified = modified.delete(getCursorPos(), getCursorPos() + text.getSelectionLength());
                } else {
                    modified = modified.deleteCharAt(getCursorPos());
                }
            } else if (keyCode == KeyCodes.KEY_BACKSPACE) {
                isDeleting = true;
                doLengthCheck = true;
                // backspace: delete previous character or selected text
                if (getText() == null || getText().length() == 0) {
                    return;
                }
                if (text.getSelectionLength() > 0) {
                    modified = modified.delete(getCursorPos(), getCursorPos() + text.getSelectionLength());
                } else {
                    modified = modified.deleteCharAt(getCursorPos() - 1);
                }
            } else if ((keyCode == KeyCodes.KEY_X && event.isControlKeyDown() && (text.getSelectionLength() > 0))) {
                doLengthCheck = true;
                isDeleting = true;
                modified = modified.delete(getCursorPos(), getCursorPos() + text.getSelectionLength());

            } else if (text.getSelectionLength() > 0 && (!event.isAnyModifierKeyDown())) {
                doLengthCheck = true;
                // type a character when there is a selection: replace selected text
                modified = modified.delete(getCursorPos(), getCursorPos() + text.getSelectionLength())
                    .insert(getCursorPos(), (char) event.getNativeKeyCode());
            } else {
                // no selection -> should be normal keypress
                //VConsole.log("Got character " + (char) keyCode + ", code: " + keyCode + ", charcode" + event.getNativeEvent().getCharCode() + ", as char" +
                //    (char) event.getNativeEvent().getCharCode() + "not handling it in onKeyDown");
            }

            // check validity of modified string
            String mod = modified.toString();
            if (!BlockingUtils.isValueValid(mod, doLengthCheck, isDeleting, text, state)) {
                //VConsole.log("onKeyDown, " + mod + " is not valid -> canceling");
                BlockingUtils.cancelKey(event);
                return;
            }
        }
    }

    public static boolean isValueValid(String newText, boolean doLengthCheck, boolean isDeleting,
        TextBox text, ValidationState state) {

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

    public static boolean isCopyOrPasteEvent(KeyPressEvent evt) {
        if (evt.isControlKeyDown()) {
            return Character.toString(evt.getCharCode()).toLowerCase().equals("c") || Character.toString(evt.getCharCode()).toLowerCase().equals("v");
        }
        return false;
    }

    public static boolean isControlKey(int keyCode) {
        BrowserInfo browser = BrowserInfo.get();
        // Firefox handles left/right etc. differently
        if (browser.isFirefox()) {
            switch (keyCode) {
            case KeyCodes.KEY_LEFT:
            case KeyCodes.KEY_RIGHT:
            case KeyCodes.KEY_HOME:
            case KeyCodes.KEY_END:
            case KeyCodes.KEY_DELETE:
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
