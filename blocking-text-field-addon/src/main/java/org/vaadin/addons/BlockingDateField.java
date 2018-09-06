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
}
