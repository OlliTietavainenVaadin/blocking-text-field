package org.vaadin.addons;

import com.vaadin.ui.TextField;
import org.vaadin.addons.client.BlockingTextFieldState;

public class BlockingTextField extends TextField {

    public BlockingTextField() {

    }

    @Override
    protected BlockingTextFieldState getState() {
        return (BlockingTextFieldState) super.getState();
    }

    public void setMinCharacterCount(int minCharacterCount) {
        getState().minCharacterCount = minCharacterCount;
    }

    public void setMaxCharacterCount(int maxCharacterCount) {
        getState().maxCharacterCount = maxCharacterCount;
    }

    @Override
    public void setValue(String newValue) throws ReadOnlyException {
        if (newValue == null) {
            super.setValue(newValue);
            return;
        }
        if (getState().maxCharacterCount >= 0 && newValue.length() > getState().maxCharacterCount) {
            throw new IllegalArgumentException("Cannot set value longer than maxCharacterCount when maxCharacterCount is defined");
        }
        if (getState().minCharacterCount >= 0 && newValue.length() < getState().minCharacterCount) {
            throw new IllegalArgumentException("Cannot set value shorter than minCharacterCount when minCharacterCount is defined");
        }
        super.setValue(newValue);
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
}
