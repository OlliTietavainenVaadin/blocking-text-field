package org.vaadin.addons;

import org.vaadin.addons.client.BlockingDateFieldState;

import com.vaadin.ui.DateField;

public class BlockingDateField extends DateField {


    public void setMinCharacterCount(int minCharacterCount) {
        getState().minCharacterCount = minCharacterCount;
    }

    public void setMaxCharacterCount(int maxCharacterCount) {
        getState().maxCharacterCount = maxCharacterCount;
    }

    @Override
    protected BlockingDateFieldState getState() {
        return (BlockingDateFieldState) super.getState();
    }

   /**
     * Set allowed input types. Defaults to all.
     *
     * @param all
     *     Allow any type. If true, ignore other values.
     * @param alphanumeric
     *     Allow alphanumeric characters.
     * @param specialCharacters
     *     Allow characters <code>+-#.,&lt;&gt;|&amp;:_'*</code>
     */
    public void setAllowedInputTypes(boolean all, boolean alphanumeric, boolean specialCharacters) {
        getState().allAllowed = all;
        getState().alphaNumericAllowed = alphanumeric;
        getState().specialCharactersAllowed = specialCharacters;
    }

    public void setAllowedCharacters(String allowedCharacters) {
        getState().allowedCharacters = allowedCharacters;
    }

}
