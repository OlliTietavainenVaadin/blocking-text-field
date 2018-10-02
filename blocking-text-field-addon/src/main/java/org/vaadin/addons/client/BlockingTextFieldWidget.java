package org.vaadin.addons.client;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.vaadin.client.ui.VTextField;

public class BlockingTextFieldWidget extends VTextField {

    private ValidationState state;
    private String alphanum;
    private String limitedSpecialCharacters;

    public BlockingTextFieldWidget() {
        this.state = new ValidationState();
        alphanum = "ABCDEFGHIJKLMNOPQRSTUVWXYZÖÄÜ";
        alphanum += alphanum.toLowerCase() + "ß";
        alphanum += "1234567890";
        limitedSpecialCharacters = "-+#.,<>|;:_'*";
        setStyleName("blocking-text-field");
        addKeyPressHandler(new BlockingKeyPressHandler(isReadOnly(), isEnabled(), this, state));
        addKeyDownHandler(new BlockingKeyDownHandler(this, state));
        sinkEvents(Event.ONPASTE);
    }

    public native String getPasteContent(NativeEvent event)/*-{
        var clipboardData, pastedData;
        clipboardData = event.clipboardData || window.clipboardData;
        pastedData = clipboardData.getData('Text');
        return pastedData;
    }-*/;

    @Override
    public void onBrowserEvent(Event event) {
        if (DOM.eventGetType(event) == Event.ONPASTE) {
            event.preventDefault();
            String pasteContent = getPasteContent(event);
            BlockingUtils.handlePaste(this, pasteContent, state);
        } else {
            super.onBrowserEvent(event);
        }
    }

    public void updateAllowedCharactersList() {
        String temp = "";
        if (state.specialCharactersAllowed) {
            temp += limitedSpecialCharacters;
        }

        if (state.alphaNumericAllowed) {
            temp += alphanum;
        }
        if (state.allowedCharacters != null) {
            temp += state.allowedCharacters;
        }

        state.combinedAllowedCharacters = temp;
    }

    public void setMinCharacterCount(int minCharacterCount) {
        state.minCharacterCount = minCharacterCount;
    }

    public void setMaxCharacterCount(int maxCharacterCount) {
        state.maxCharacterCount = maxCharacterCount;
    }

    public void setAllowedCharacters(String allowedCharacters) {
        state.allowedCharacters = allowedCharacters;
    }

    public void setAllAllowed(boolean allAllowed) {
        state.allAllowed = allAllowed;
    }

    public void setAlphaNumericAllowed(boolean alphaNumericAllowed) {
        state.alphaNumericAllowed = alphaNumericAllowed;
    }

    public void setSpecialCharactersAllowed(boolean specialCharactersAllowed) {
        state.specialCharactersAllowed = specialCharactersAllowed;
    }

}