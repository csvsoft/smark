package com.csvsoft.smark.ui;

import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.aceeditor.AceEditor;
import org.vaadin.aceeditor.AceMode;

public class SQLEditField extends CustomField<String> {

    Label sqlLabel;
    AceEditor sqlEditor;
    Button.ClickListener editSQLListener;
    Button editSQLButton;
    public SQLEditField(Button.ClickListener editSQLListener,String caption){
        this.editSQLListener =editSQLListener;
        sqlLabel=new Label();
        sqlEditor = new AceEditor();
        sqlEditor.setMode(AceMode.sql);
        sqlEditor.setReadOnly(true);
        sqlEditor.setShowPrintMargin(false);
        sqlEditor.setShowGutter(false);
        this.setCaption(caption);
        this.setSizeFull();
    }

    public void disableEdit(){
        this.editSQLButton.setEnabled(false);
    }
    public void enableEdit(){
        this.editSQLButton.setEnabled(true);
    }
    @Override
    protected Component initContent() {
        VerticalLayout vl = new VerticalLayout( );
        editSQLButton = new Button("Edit SQL");
        editSQLButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        editSQLButton.addClickListener(editSQLListener);
        vl.addComponent(editSQLButton);

        //sqlEditor.setSizeFull();
        sqlEditor.setWidth("100%");
        sqlEditor.setHeight("100%");
        vl.addComponentsAndExpand(sqlEditor);
        vl.setMargin(false);
        vl.setSpacing(false);
        vl.setWidth("100%");
        vl.setHeight(500,Unit.PIXELS);

        sqlEditor.addContextClickListener(e->{
           // e.getMouseEventDetails().isDoubleClick();
        });
        return vl;
    }

    @Override
    protected void doSetValue(String s) {
        sqlLabel.setValue(s);
        sqlEditor.setValue(s);
        sqlEditor.setHeight(300,Unit.PIXELS);

    }

    @Override
    public String getValue() {
        return sqlEditor.getValue();

    }
}
