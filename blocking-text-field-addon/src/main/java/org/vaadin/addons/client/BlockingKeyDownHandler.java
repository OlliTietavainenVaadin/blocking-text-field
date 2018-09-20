package org.vaadin.addons.client;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.TextBoxBase;

public class BlockingKeyDownHandler implements KeyDownHandler {

    private TextBoxBase text;
    private ValidationState state;

    public BlockingKeyDownHandler(TextBoxBase text, ValidationState state) {
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
            modified = modified.delete(getCursorPos(), getCursorPos() + text.getSelectionLength()).insert(getCursorPos(), (char) event.getNativeKeyCode());
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
