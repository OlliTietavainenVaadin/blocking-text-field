package org.vaadin.addons.client;

import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.TextBoxBase;

public class BlockingKeyPressHandler implements KeyPressHandler {

    private boolean readOnly;
    private boolean enabled;
    private TextBoxBase text;
    private ValidationState state;

    public BlockingKeyPressHandler(boolean readOnly, boolean enabled, TextBoxBase text, ValidationState state) {
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
