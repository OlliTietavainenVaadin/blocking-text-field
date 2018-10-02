package org.vaadin.addons.client;

public class ValidationState {
    public int minCharacterCount = -1;
    public int maxCharacterCount = -1;
    public boolean allAllowed = false;
    public boolean alphaNumericAllowed = false;
    public boolean specialCharactersAllowed = false;
    public String allowedCharacters = null;
    public String combinedAllowedCharacters = "";
}
