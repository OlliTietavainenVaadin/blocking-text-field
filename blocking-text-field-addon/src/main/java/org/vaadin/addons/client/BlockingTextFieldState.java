package org.vaadin.addons.client;

import com.vaadin.shared.ui.textfield.AbstractTextFieldState;

public class BlockingTextFieldState extends AbstractTextFieldState {

    public int minCharacterCount = -1;
    public int maxCharacterCount = -1;
    public boolean allAllowed = false;
    public boolean alphaNumericAllowed = false;
    public boolean specialCharactersAllowed = false;
    public String allowedCharacters = null;

}