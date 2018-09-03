package org.vaadin.addons.demo;

import org.vaadin.addons.BlockingTextField;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Theme("demo")
@Title("BlockingTextField Add-on Demo")
@SuppressWarnings("serial")
public class DemoUI extends UI
{

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = DemoUI.class)
    public static class Servlet extends VaadinServlet {
    }

    @Override
    protected void init(VaadinRequest request) {


        final VerticalLayout layout = new VerticalLayout();
        layout.setStyleName("demoContentLayout");
        layout.setSizeFull();

        final BlockingTextField minmax = new BlockingTextField();
        minmax.setCaption("Min 2, max 4 characters, all allowed");
        minmax.setValue("12!");
        minmax.setMinCharacterCount(2);
        minmax.setMaxCharacterCount(4);
        layout.addComponent(minmax);

        final BlockingTextField alphanumOnly = new BlockingTextField();
        alphanumOnly.setCaption("Alphanumeric only");
        alphanumOnly.setAllowedInputTypes(false, true, false);
        alphanumOnly.setValue("A23b");
        layout.addComponent(alphanumOnly);

        final BlockingTextField specialCharactersOnly = new BlockingTextField();
        specialCharactersOnly.setCaption("Limited special characters only: +-#.,<>|;:_'*");
        specialCharactersOnly.setAllowedInputTypes(false, false, true);
        specialCharactersOnly.setValue("<>+-;:");
        layout.addComponent(specialCharactersOnly);

        final BlockingTextField specialAlphanumMax = new BlockingTextField();
        specialAlphanumMax.setCaption("Limited special characters and alphanumerics, max 5 ");
        specialAlphanumMax.setAllowedInputTypes(false, true, true);
        specialAlphanumMax.setMaxCharacterCount(5);
        specialAlphanumMax.setValue("<>+A1");
        layout.addComponent(specialAlphanumMax);

        final BlockingTextField tooFewCharacters = new BlockingTextField();
        tooFewCharacters.setCaption("Initially too few characters, minimum 3");
        tooFewCharacters.setAllowedInputTypes(true, true, true);
        tooFewCharacters.setMinCharacterCount(3);

        layout.addComponent(tooFewCharacters);

        final BlockingTextField tooManyCharacters = new BlockingTextField();
        tooManyCharacters.setCaption("Initially too many character, maximum 2");
        tooManyCharacters.setAllowedInputTypes(true, true, true);
        tooManyCharacters.setValue("1234");
        tooManyCharacters.setMaxCharacterCount(2);

        layout.addComponent(tooManyCharacters);

        final BlockingTextField customAllowedCharacters = new BlockingTextField();
        customAllowedCharacters.setCaption("Allowed characters: [a-zA-Z0-9äöüßÄÖÜ.,-+/#<>|;:_'*] and Space");
        customAllowedCharacters.setAllowedInputTypes(false, false, false);
        String alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        customAllowedCharacters.setAllowedCharacters(alpha + alpha.toLowerCase() + "ÖÄÜöäüß0123456789.,-+/#<>|;:_'* ");
        layout.addComponent(customAllowedCharacters);

        setContent(layout);
    }
}
