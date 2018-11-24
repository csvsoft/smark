package com.csvsoft.smark.ui;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.window.WindowMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

public class ErrorPopupWindow extends Window {

    private Label errorLabel;
    public ErrorPopupWindow(){
        setWidth(800.0f, Unit.PIXELS);
        setHeight(50.0f,Unit.PIXELS);
        setWindowMode(WindowMode.NORMAL);

        setModal(false);
        setResizable(true);
         errorLabel = new Label();
         errorLabel.setContentMode(ContentMode.HTML);
        setContent(errorLabel);
    }
    public void setErrorMessage(String errorMessage){
        errorLabel.setValue("<div style='color:red'>"+errorMessage+"</div>");
    }

}
