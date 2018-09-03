package org.vaadin.addons.client;

import com.vaadin.shared.ui.textfield.AbstractTextFieldState;

public class BlockingTextFieldState extends AbstractTextFieldState {

    public int minCharacterCount = -1;
    public int maxCharacterCount = -1;
    public boolean allAllowed = true;
    public boolean alphaNumericAllowed = true;
    public boolean specialCharactersAllowed = true;
    public String allowedCharacters = null;

}