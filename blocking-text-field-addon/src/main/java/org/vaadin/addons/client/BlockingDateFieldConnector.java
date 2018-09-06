package org.vaadin.addons.client;

import org.vaadin.addons.BlockingDateField;

import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.TextBox;
import com.vaadin.client.VConsole;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.datefield.DateFieldConnector;
import com.vaadin.shared.ui.Connect;

@Connect(BlockingDateField.class)
public class BlockingDateFieldConnector extends DateFieldConnector {

    private int maxCharacterCount;
    private int minCharacterCount;
    private boolean allAllowed;
    private boolean alphaNumericAllowed;
    private boolean specialCharactersAllowed;
    private String allowedCharacters;
    private String combinedAllowedCharacters;

    public String getText() {
        return getWidget().text.getText();
    }

    public int getCursorPos() {
        return getWidget().text.getCursorPos();
    }

    @Override
    protected void init() {
        super.init();
        KeyDownHandler keyDownHandler = new KeyDownHandler() {
            @Override
            public void onKeyDown(KeyDownEvent event) {
                // TODO: getWidget().text.getSelectionLength() -> variable
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
                    if (getWidget().text.getSelectionLength() > 0) {
                        modified = modified.delete(getCursorPos(), getCursorPos() + getWidget().text.getSelectionLength());
                    } else {
                        modified = modified.deleteCharAt(getCursorPos());
                    }
                } else if (keyCode == KeyCodes.KEY_BACKSPACE) {
                    doLengthCheck = true;
                    // backspace: delete previous character or selected text
                    if (getText() == null || getText().length() == 0) {
                        return;
                    }
                    if (getWidget().text.getSelectionLength() > 0) {
                        modified = modified.delete(getCursorPos(), getCursorPos() + getWidget().text.getSelectionLength());
                    } else {
                        modified = modified.deleteCharAt(getCursorPos() - 1);
                    }
                } else if ((keyCode == KeyCodes.KEY_X && event.isControlKeyDown() && (getWidget().text.getSelectionLength() > 0))) {
                    doLengthCheck = true;
                    modified = modified.delete(getCursorPos(), getCursorPos() + getWidget().text.getSelectionLength());

                } else if (getWidget().text.getSelectionLength() > 0 && (!event.isAnyModifierKeyDown())) {
                    doLengthCheck = true;
                    // type a character when there is a selection: replace selected text
                    modified = modified.delete(getCursorPos(), getCursorPos() + getWidget().text.getSelectionLength()).insert(getCursorPos(), (char) event.getNativeKeyCode());
                } else {
                    // no selection -> should be normal keypress
                    //VConsole.log("Got character " + (char) keyCode + ", code: " + keyCode + ", charcode" + event.getNativeEvent().getCharCode() + ", as char" +
                    //    (char) event.getNativeEvent().getCharCode() + "not handling it in onKeyDown");
                }

                // check validity of modified string
                if (!isValueValid(modified.toString(), doLengthCheck)) {
                    //VConsole.log("onKeyDown, " + modified.toString() + " is not valid -> canceling");
                    cancelKey(event);
                    return;
                }
            }
        };
        KeyPressHandler keyPressHandler = new KeyPressHandler() {
            @Override
            public void onKeyPress(KeyPressEvent event) {
                if (isReadOnly() || !isEnabled()) {
                    return;
                }
                if (BlockingUtils.isCopyOrPasteEvent(event)) {
                    cancelKey(event);
                    return;
                }

                int keyCode = event.getNativeEvent().getKeyCode();
                if (BlockingUtils.isControlKey(keyCode)) {
                    // treat any control key normally; delete, backspace etc. are handled in keyDownHandler
                    return;
                }
                String newText = BlockingUtils.valueAfterKeyPress(event.getCharCode(), getWidget().text);
                if (!isValueValid(newText, true)) {
                    //VConsole.log("onKeyPress: " + newText + "is not valid, canceling");
                    cancelKey(event);
                }
            }
        };
        getWidget().addDomHandler(keyDownHandler, KeyDownEvent.getType());
        getWidget().addDomHandler(keyPressHandler, KeyPressEvent.getType());
    }

    private boolean isValueValid(String newText, boolean doLengthCheck) {
        if (doLengthCheck && !BlockingUtils.withinLengthBounds(newText, getWidget().text, minCharacterCount, maxCharacterCount)) {
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

    private void cancelKey(DomEvent event) {
        event.preventDefault();
    }

    @Override
    public BlockingDateFieldState getState() {
        return (BlockingDateFieldState) super.getState();
    }

    // Whenever the state changes in the server-side, this method is called
    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        maxCharacterCount = getState().maxCharacterCount;
        minCharacterCount = getState().minCharacterCount;
        allAllowed = getState().allAllowed;
        alphaNumericAllowed = getState().alphaNumericAllowed;
        specialCharactersAllowed = getState().specialCharactersAllowed;
        allowedCharacters = getState().allowedCharacters;
        //getWidget().updateAllowedCharactersList();
    }

}
