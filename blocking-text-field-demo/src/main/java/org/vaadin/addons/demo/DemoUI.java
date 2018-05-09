package org.vaadin.addons.demo;

import org.vaadin.addons.BlockingTextField;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
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



        setContent(layout);
    }
}
