package com.csvsoft.smark.ui;

import com.vaadin.ui.*;

import java.util.function.Consumer;

public class ConfirmationPopup extends Window {

    private String caption;
    private String message;
    private Consumer<Boolean> resultConsumer;

    public ConfirmationPopup(String caption, Component content, String caption1, String message, Consumer<Boolean> resultConsumer) {
        super(caption, content);
        this.caption = caption1;
        this.message = message;
        this.resultConsumer = resultConsumer;
        //this.setCaption(caption);
       // init();
    }
    public ConfirmationPopup(){

    }
    private void init(){

        this.setCaption(caption);
        VerticalLayout vl = new VerticalLayout();
        vl.addComponent(new Label(message));

        HorizontalLayout hl = new HorizontalLayout();
        Button yesButton = new Button("Yes");
        Button cancelButton = new Button("Cancel");
        hl.setDefaultComponentAlignment(Alignment.MIDDLE_RIGHT);
        hl.addComponents(yesButton,cancelButton);
        vl.addComponent(hl);
        this.setContent(vl);
        this.setModal(true);

        yesButton.addClickListener(e->resultConsumer.accept(true));
        cancelButton.addClickListener(e->resultConsumer.accept(false));
    }

    public void show(UI parent){
        this.init();
        parent.addWindow(this);


    }

    @Override
    public String getCaption() {
        return caption;
    }

    @Override
    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Consumer<Boolean> getResultConsumer() {
        return resultConsumer;
    }

    public void setResultConsumer(Consumer<Boolean> resultConsumer) {
        this.resultConsumer = resultConsumer;
    }
}
