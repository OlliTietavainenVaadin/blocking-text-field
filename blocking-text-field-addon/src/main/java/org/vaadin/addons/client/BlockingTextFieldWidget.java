package org.vaadin.addons.client;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.vaadin.client.ui.VTextField;

public class BlockingTextFieldWidget extends VTextField {

    private int minCharacterCount = -1;
    private int maxCharacterCount = -1;
    private boolean allAllowed = true;
    private boolean alphaNumericAllowed = true;
    private boolean specialCharactersAllowed = true;
    private String alphanum;
    private String limitedSpecialCharacters;
    private String combinedAllowedCharacters;
    private String allowedCharacters = null;

    private KeyPressHandler keyPressHandler = new KeyPressHandler() {

        @Override
        public void onKeyPress(KeyPressEvent event) {
            if (isReadOnly() || !isEnabled()) {
                //VConsole.log("onKeyPress: readonly / not enabled, ignoring");
                return;
            }
            if (BlockingUtils.isCopyOrPasteEvent(event)) {
                //VConsole.log("onKeyPress: is paste event, canceling");
                cancelKey();
                return;
            }

            int keyCode = event.getNativeEvent().getKeyCode();
            if (BlockingUtils.isControlKey(keyCode)) {
                // treat any control key normally; delete, backspace etc. are handled in keyDownHandler
                return;
            }
            String newText = BlockingUtils.valueAfterKeyPress(event.getCharCode(), BlockingTextFieldWidget.this);
            if (!isValueValid(newText, true)) {
                //VConsole.log("onKeyPress: " + newText + "is not valid, canceling");
                cancelKey();
            }

        }
    };

    private KeyDownHandler keyDownHandler = new KeyDownHandler() {
        @Override
        public void onKeyDown(KeyDownEvent event) {
            // check if keystroke combination would affect validity by deletion / addition
            int keyCode = event.getNativeEvent().getKeyCode();
            if (BlockingUtils.isIgnorableOnKeyDown(keyCode)) {
                //VConsole.log("Ignorable on keydown, no action");
                return;
            }
            boolean doLengthCheck = false;
            StringBuilder modified = new StringBuilder(getText());
            if ((keyCode == KeyCodes.KEY_DELETE)) {
                doLengthCheck = true;
                // delete one character or selection
                if (getCursorPos() == getText().length()) {
                    // pressed delete at the end -> does nothing
                    return;
                }
                if (getSelectionLength() > 0) {
                    modified = modified.delete(getCursorPos(), getCursorPos() + getSelectionLength());
                } else {
                    modified = modified.deleteCharAt(getCursorPos());
                }
            } else if (keyCode == KeyCodes.KEY_BACKSPACE) {
                doLengthCheck = true;
                // backspace: delete previous character or selected text
                if (getText() == null || getText().length() == 0) {
                    return;
                }
                if (getSelectionLength() > 0) {
                    modified = modified.delete(getCursorPos(), getCursorPos() + getSelectionLength());
                } else {
                    modified = modified.deleteCharAt(getCursorPos() - 1);
                }
            } else if ((keyCode == KeyCodes.KEY_X && event.isControlKeyDown() && (getSelectionLength() > 0))) {
                doLengthCheck = true;
                modified = modified.delete(getCursorPos(), getCursorPos() + getSelectionLength());

            } else if (getSelectionLength() > 0 && (!event.isAnyModifierKeyDown())) {
                doLengthCheck = true;
                // type a character when there is a selection: replace selected text
                modified = modified.delete(getCursorPos(), getCursorPos() + getSelectionLength()).insert(getCursorPos(), (char) event.getNativeKeyCode());
            } else {
                // no selection -> should be normal keypress
                //VConsole.log("Got character " + (char) keyCode + ", code: " + keyCode + ", charcode" + event.getNativeEvent().getCharCode() + ", as char" +
                //    (char) event.getNativeEvent().getCharCode() + "not handling it in onKeyDown");
            }

            // check validity of modified string
            if (!isValueValid(modified.toString(), doLengthCheck)) {
                //VConsole.log("onKeyDown, " + modified.toString() + " is not valid -> canceling");
                cancelKey();
                return;
            }

        }
    };

    public BlockingTextFieldWidget() {
        alphanum = "ABCDEFGHIJKLMNOPQRSTUVWXYZÖÄÜ";
        alphanum += alphanum.toLowerCase() + "ß";
        alphanum += "1234567890";
        limitedSpecialCharacters = "-+#.,<>|;:_'*";
        setStyleName("blocking-text-field");
        addKeyPressHandler(keyPressHandler);
        addKeyDownHandler(keyDownHandler);
        sinkEvents(Event.ONPASTE);
    }

    @Override
    public void onBrowserEvent(Event event) {
        if (DOM.eventGetType(event) == Event.ONPASTE) {
            event.preventDefault();
        } else {
            super.onBrowserEvent(event);
        }
    }

    private boolean isValueValid(String newText, boolean doLengthCheck) {
        if (doLengthCheck && !BlockingUtils.withinLengthBounds(newText, this, minCharacterCount, maxCharacterCount)) {
            return false;
        }
        if (allAllowed) {
            return true;
        }
        // using looping because GWT regex support is not great
        // if alphanumerics are not allowed and one is found, not valid

        // if alphanumerics are not allowed but limited special characters are:
        if (!BlockingUtils.containsOnlyFromList(newText, combinedAllowedCharacters)) {
            return false;
        } else {
            //VConsole.log(newText + " is valid, only characters from " + allowedCharacters);
        }

        return true;
    }

    public void updateAllowedCharactersList() {
        String temp = "";
        if (specialCharactersAllowed) {
            temp += limitedSpecialCharacters;
        }

        if (alphaNumericAllowed) {
            temp += alphanum;
        }
        if (allowedCharacters != null) {
            temp += allowedCharacters;
        }

        combinedAllowedCharacters = temp;
    }

    public int getMinCharacterCount() {
        return minCharacterCount;
    }

    public void setMinCharacterCount(int minCharacterCount) {
        this.minCharacterCount = minCharacterCount;
    }

    public int getMaxCharacterCount() {
        return maxCharacterCount;
    }

    public void setMaxCharacterCount(int maxCharacterCount) {
        this.maxCharacterCount = maxCharacterCount;
    }

    public void setAllowedCharacters(String allowedCharacters) {
        this.allowedCharacters = allowedCharacters;
    }

    public void setAllAllowed(boolean allAllowed) {
        this.allAllowed = allAllowed;
    }

    public void setAlphaNumericAllowed(boolean alphaNumericAllowed) {
        this.alphaNumericAllowed = alphaNumericAllowed;
    }

    public void setSpecialCharactersAllowed(boolean specialCharactersAllowed) {
        this.specialCharactersAllowed = specialCharactersAllowed;
    }
}