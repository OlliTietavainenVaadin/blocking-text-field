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
    private String allowedCharacters;

    private KeyPressHandler keyPressHandler = new KeyPressHandler() {

        @Override
        public void onKeyPress(KeyPressEvent event) {
            if (isReadOnly() || !isEnabled()) {
                return;
            }
            if (isCopyOrPasteEvent(event)) {
                cancelKey();
                return;
            }

            int keyCode = event.getNativeEvent().getKeyCode();
            if (isControlKey(keyCode)) {
                // treat any control key normally; delete, backspace etc. are handled in keyDownHandler
                return;
            }
            String newText = valueAfterKeyPress(event.getCharCode());
            if (!isValueValid(newText)) {
                cancelKey();
            }

        }
    };

    private KeyDownHandler keyDownHandler = new KeyDownHandler() {
        @Override
        public void onKeyDown(KeyDownEvent event) {
            // check if keystroke combination would affect validity by deletion / addition
            int keyCode = event.getNativeEvent().getKeyCode();
            if ((keyCode == KeyCodes.KEY_SHIFT) || (keyCode == KeyCodes.KEY_ALT) || (keyCode == KeyCodes.KEY_CTRL)) {
                return;
            }
            StringBuilder modified = new StringBuilder(getText());
            if ((keyCode == KeyCodes.KEY_DELETE)) {
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
                // cut from keyboard: delete selected text
                modified = modified.delete(getCursorPos(), getCursorPos() + getSelectionLength());

            } else if (getSelectionLength() > 0 && (!event.isAnyModifierKeyDown())) {
                // type a character when there is a selection: replace selected text
                modified = modified.delete(getCursorPos(), getCursorPos() + getSelectionLength()).insert(getCursorPos(), (char) event.getNativeKeyCode());
            } else {
                // no selection -> should be normal keypress
                //VConsole.log("Got character " + (char) keyCode + ", code: " + keyCode + ", charcode" + event.getNativeEvent().getCharCode() + ", as char" +
                //    (char) event.getNativeEvent().getCharCode() + "not handling it in onKeyDown");
            }

            // check validity of modified string
            if (!isValueValid(modified.toString())) {
                cancelKey();
                return;
            }

        }
    };

    public BlockingTextFieldWidget() {
        alphanum = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        alphanum += alphanum.toLowerCase();
        alphanum += "1234567890";
        limitedSpecialCharacters = "-+#.,<>|;:_'*";
        setStyleName("blocking-text-field");
        addKeyPressHandler(keyPressHandler);
        addKeyDownHandler(keyDownHandler);
        sinkEvents(Event.ONPASTE);
    }

    private boolean isCopyOrPasteEvent(KeyPressEvent evt) {
        if (evt.isControlKeyDown()) {
            return Character.toString(evt.getCharCode()).toLowerCase().equals("c") || Character.toString(evt.getCharCode()).toLowerCase().equals("v");
        }
        return false;
    }

    @Override
    public void onBrowserEvent(Event event) {
        if (DOM.eventGetType(event) == Event.ONPASTE) {
            event.preventDefault();
        } else {
            super.onBrowserEvent(event);
        }
    }

    private boolean isValueValid(String newText) {
        if (withinLengthBounds(newText)) {
            return false;
        }
        if (allAllowed) {
            return true;
        }
        else if (!alphaNumericAllowed && !specialCharactersAllowed) {
            return false;
        }
        // using looping because GWT regex support is not great
        // if alphanumerics are not allowed and one is found, not valid


        // if alphanumerics are not allowed but limited special characters are:
        if (!containsOnlyFromList(newText, allowedCharacters)) {
            return false;
        } else {
            //VConsole.log(newText + " is valid, only characters from " + allowedCharacters);
        }

        return true;
    }

    private boolean containsOnlyFromList(String text, String listToCheck) {
        for (int i = 0; i < text.length(); i++) {
            if (listToCheck.indexOf(text.charAt(i)) < 0) {
                return false;
            }
        }
        return true;
    }

    private boolean withinLengthBounds(String newText) {
        if (minCharacterCount >= 0) {
            if (newText.length() < minCharacterCount) {
                return true;
            }
        }
        if (maxCharacterCount >= 0) {
            if (newText.length() > maxCharacterCount) {
                return true;
            }
        }
        return false;
    }

    private String valueAfterKeyPress(char charCode) {
        int index = getCursorPos();
        String previousText = getText();

        if (getSelectionLength() > 0) {
            return previousText.substring(0, index) + charCode + previousText.substring(index + getSelectionLength(), previousText.length());
        } else {
            return previousText.substring(0, index) + charCode + previousText.substring(index, previousText.length());
        }
    }

    private boolean isControlKey(int keyCode) {
        switch (keyCode) {
        case KeyCodes.KEY_RIGHT:
        case KeyCodes.KEY_BACKSPACE:
        case KeyCodes.KEY_TAB:
        case KeyCodes.KEY_ENTER:
        case KeyCodes.KEY_ESCAPE:
            return true;
        }

        return false;
    }

    public void updateAllowedCharactersList() {
        String temp = "";
        if (specialCharactersAllowed) {
            temp += limitedSpecialCharacters;
        }

        if (alphaNumericAllowed) {
            temp += alphanum;
        }

       allowedCharacters = temp;
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