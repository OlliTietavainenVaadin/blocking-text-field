package org.vaadin.addons.client;

import com.vaadin.shared.ui.datefield.LocalDateFieldState;

public class BlockingDateFieldState extends LocalDateFieldState {

    public int minCharacterCount = -1;
    public int maxCharacterCount = -1;
    public boolean allAllowed = false;
    public boolean alphaNumericAllowed = false;
    public boolean specialCharactersAllowed = false;
    public String allowedCharacters = null;
    public String combinedAllowedCharacters = "";
}
