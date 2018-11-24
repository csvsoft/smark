package com.csvsoft.smark.ui;

import com.vaadin.server.Page;
import com.vaadin.shared.Position;
import com.vaadin.ui.*;
import eu.maxschuster.vaadin.autocompletetextfield.AutocompleteSuggestionProvider;
import eu.maxschuster.vaadin.autocompletetextfield.AutocompleteTextField;
import eu.maxschuster.vaadin.autocompletetextfield.provider.CollectionSuggestionProvider;
import eu.maxschuster.vaadin.autocompletetextfield.provider.MatchMode;
import eu.maxschuster.vaadin.autocompletetextfield.shared.ScrollBehavior;

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

public class AppSpecSearchForm extends VerticalLayout {

    private void init(){
        HorizontalLayout searchHL= new HorizontalLayout();
        TextField searchField = new TextField("Search App:");


    }

    private Component buildSearchBox(){
        Collection<String> theJavas = Arrays.asList(new String[] {
                "Java",
                "JavaScript",
                "Join Java",
                "JavaFX Script"
        });

        AutocompleteSuggestionProvider suggestionProvider = new CollectionSuggestionProvider(theJavas, MatchMode.CONTAINS, true, Locale.US);

        AutocompleteTextField field = new AutocompleteTextField();

// ===============================
// Available configuration options
// ===============================
        //(true); // Client side should cache suggestions
        field.setDelay(150); // Delay before sending a query to the server
        field.setItemAsHtml(false); // Suggestions contain html formating. If true, make sure that the html is save to use!
        field.setMinChars(3); // The required value length to trigger a query
        field.setScrollBehavior(ScrollBehavior.NONE); // The method that should be used to compensate scrolling of the page
        field.setSuggestionLimit(0); // The max amount of suggestions send to the client. If the limit is >= 0 no limit is applied

        field.setSuggestionProvider(suggestionProvider);
        field.addSelectListener(e -> {
            String text = "Text changed to: " + e.getSuggestion();
            Notification.show(text, Notification.Type.TRAY_NOTIFICATION);
        });
        field.addValueChangeListener(e -> {
            String text = "Value changed to: " + e.getValue();
            Notification notification = new Notification(
                    text, Notification.Type.TRAY_NOTIFICATION);
            notification.setPosition(Position.BOTTOM_LEFT);
            notification.show(Page.getCurrent());
        });
        return field;

    }
}
