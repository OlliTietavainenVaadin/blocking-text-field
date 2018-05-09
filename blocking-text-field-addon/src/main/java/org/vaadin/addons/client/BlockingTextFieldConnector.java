package org.vaadin.addons.client;

import org.vaadin.addons.BlockingTextField;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.vaadin.client.MouseEventDetailsBuilder;
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.client.ui.textfield.TextFieldConnector;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.ui.Connect;

// Connector binds client-side widget class to server-side component class
// Connector lives in the client and the @Connect annotation specifies the
// corresponding server-side component
@Connect(BlockingTextField.class)
public class BlockingTextFieldConnector extends TextFieldConnector {

    public BlockingTextFieldConnector() {
        

    }

    @Override
    public BlockingTextFieldWidget getWidget() {
        return (BlockingTextFieldWidget) super.getWidget();
    }

    @Override
    public BlockingTextFieldState getState() {
        return (BlockingTextFieldState) super.getState();
    }

    // Whenever the state changes in the server-side, this method is called
    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        getWidget().setMaxCharacterCount(getState().maxCharacterCount);
        getWidget().setMinCharacterCount(getState().minCharacterCount);
        getWidget().setAllAllowed(getState().allAllowed);
        getWidget().setAlphaNumericAllowed(getState().alphaNumericAllowed);
        getWidget().setSpecialCharactersAllowed(getState().specialCharactersAllowed);
        getWidget().updateAllowedCharactersList();
    }
}
