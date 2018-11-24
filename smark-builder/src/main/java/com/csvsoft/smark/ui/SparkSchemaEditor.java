package com.csvsoft.smark.ui;

import com.csvsoft.smark.ui.model.SparkSQLSchemaSuggestor;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import org.vaadin.aceeditor.AceEditor;
import org.vaadin.aceeditor.AceMode;
import org.vaadin.aceeditor.SuggestionExtension;

public class SparkSchemaEditor extends VerticalLayout {

    String schemaText;
    public SparkSchemaEditor(String schemaText){
        this.schemaText = schemaText;
        init();
    }
    private void init(){
        HorizontalLayout hl = new HorizontalLayout();
        Button saveButton = new Button("Save");
        hl.addComponent(saveButton);

        Button cancelButton = new Button("Cancel");
        hl.addComponent(cancelButton);

        this.addComponent(hl);
        AceEditor ace = new AceEditor();
        ace.setMode(AceMode.text);
        ace.setValue(schemaText);
        SparkSQLSchemaSuggestor suggestor = new SparkSQLSchemaSuggestor();
        new SuggestionExtension(suggestor).extend(ace);
        ace.setSizeFull();
        ace.setShowGutter(true);
        addComponentsAndExpand(ace);

        this.setSizeFull();

    }
}
