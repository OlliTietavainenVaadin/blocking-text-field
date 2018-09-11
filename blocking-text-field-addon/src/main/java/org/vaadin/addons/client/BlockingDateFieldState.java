package org.vaadin.addons.client;

import com.vaadin.shared.ui.datefield.PopupDateFieldState;

public class BlockingDateFieldState extends PopupDateFieldState {

    public int minCharacterCount = -1;
    public int maxCharacterCount = -1;
    public boolean allAllowed = false;
    public boolean alphaNumericAllowed = false;
    public boolean specialCharactersAllowed = false;
    public String allowedCharacters = null;
}
