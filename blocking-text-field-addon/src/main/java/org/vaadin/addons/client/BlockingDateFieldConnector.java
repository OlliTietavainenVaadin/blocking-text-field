package org.vaadin.addons.client;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.datefield.DateFieldConnector;
import com.vaadin.shared.ui.Connect;
import org.vaadin.addons.BlockingDateField;

@Connect(BlockingDateField.class)
public class BlockingDateFieldConnector extends DateFieldConnector {

    private ValidationState state;
    private String alphanum;
    private String limitedSpecialCharacters;

    public BlockingDateFieldConnector() {
        state = new ValidationState();
    }

    @Override
    protected void init() {
        super.init();

        alphanum = "ABCDEFGHIJKLMNOPQRSTUVWXYZÖÄÜ";
        alphanum += alphanum.toLowerCase() + "ß";
        alphanum += "1234567890";
        limitedSpecialCharacters = "-+#.,<>|;:_'*";
        KeyDownHandler keyDownHandler = new BlockingKeyDownHandler(getWidget().text, state);
        KeyPressHandler keyPressHandler = new BlockingKeyPressHandler(isReadOnly(), isEnabled(), getWidget().text, state);

        getWidget().addDomHandler(keyDownHandler, KeyDownEvent.getType());
        getWidget().addDomHandler(keyPressHandler, KeyPressEvent.getType());
        addPasteHandler(getWidget().text.getElement());
    }

    public void onPasteEvent(NativeEvent event) {
        String pasteContent = getPasteContent(event);
        BlockingUtils.handlePaste(getWidget().text, pasteContent, state);
        getWidget().updateBufferedValues();
        getWidget().sendBufferedValues();
    }

    public native void addPasteHandler(com.google.gwt.user.client.Element element)/*-{
        var that = this;
        element.onpaste = function (e) {
            e.preventDefault();
            that.@org.vaadin.addons.client.BlockingDateFieldConnector::onPasteEvent(*)(e)
        }

    }-*/;

    public native String getPasteContent(NativeEvent event)/*-{
        var clipboardData, pastedData;
        clipboardData = event.clipboardData || window.clipboardData;
        pastedData = clipboardData.getData('Text');
        return pastedData;
    }-*/;

    @Override
    public BlockingDateFieldState getState() {
        return (BlockingDateFieldState) super.getState();
    }

    // Whenever the state changes in the server-side, this method is called
    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        state.maxCharacterCount = getState().maxCharacterCount;
        state.minCharacterCount = getState().minCharacterCount;
        state.allAllowed = getState().allAllowed;
        state.alphaNumericAllowed = getState().alphaNumericAllowed;
        state.specialCharactersAllowed = getState().specialCharactersAllowed;
        state.allowedCharacters = getState().allowedCharacters;
        updateAllowedCharactersList();
    }

    private void updateAllowedCharactersList() {
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

}
