package com.csvsoft.smark.ui;

import com.csvsoft.codegen.service.ISQLFactory;
import com.csvsoft.codegen.service.JDBCSQLFactory;
import com.csvsoft.codegen.service.SparkSQLFactory;
import com.csvsoft.smark.config.*;
import com.csvsoft.smark.util.JDBCConstants;
import com.csvsoft.smark.util.JDBCUtils;
import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.data.converter.StringToLongConverter;
import com.vaadin.ui.*;
import org.vaadin.aceeditor.AceEditor;
import org.vaadin.aceeditor.AceMode;
import org.vaadin.ui.NumberField;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class SmarkTaskReadJDBCForm extends BaseTaskForm<SmarkTaskReadJDBCSpec>{

    AceEditor sqleditor;
    public SmarkTaskReadJDBCForm(SmarkTaskReadJDBCSpec smarkTaskSpec, SmarkAppSpec smarkAppSpec, SmarkAppBuilderUI builderUI) {
        super(smarkTaskSpec, smarkAppSpec, builderUI);
    }

    @Override
    protected void initUI() {
        Button editSQLButton = new Button("Edit Retrieve SQL");
        SmarkTaskJDBCTaskFormHelper.addButtons(this,binder,this.smarkAppSpec,builderUI,editSQLButton);

        SmarkTaskJDBCTaskFormHelper.buildJDBCCommonFields(this.smarkAppSpec,this,binder,builderUI);
        String fetchSizeDesc="The JDBC fetch size, which determines how many rows to fetch per round trip. This can help performance on JDBC drivers which default to low fetch size (eg. Oracle with 10 rows). This option applies only to reading.";
        NumberField fetchSize = new NumberField("Fetch Size");
        fetchSize.setDecimalAllowed(false);
        fetchSize.setMinValue(10);
        fetchSize.setWidth("50%");
        fetchSize.setDescription(fetchSizeDesc);
        fetchSize.setRequiredIndicatorVisible(true);
        addComponent(fetchSize);
        binder.forField(fetchSize)
                .asRequired("Fetch size is required")
                .withConverter(new StringToLongConverter("Must enter a long"))
                .bind(SmarkTaskReadJDBCSpec::getFetchsize,SmarkTaskReadJDBCSpec::setFetchsize);

        String customSchemaDesc = "The custom schema to use for reading data from JDBC connectors. For example, \"id DECIMAL(38, 0), name STRING\". You can also specify partial fields, and the others use the default type mapping. For example, \"id DECIMAL(38, 0)\". The column names should be identical to the corresponding column names of JDBC table. Users can specify the corresponding data types of Spark SQL instead of using the defaults. This option applies only to reading.";

        AceEditor customSchema = new AceEditor();
        customSchema.setCaption("Custom Schema");
        customSchema.setDescription(customSchemaDesc);
        customSchema.setMode(AceMode.sql);

        customSchema.setWidth("100%");
        customSchema.setShowGutter(false);
        addComponent(customSchema);
        binder.forField(customSchema)
                .bind(SmarkTaskReadJDBCSpec::getCustomSchema, SmarkTaskReadJDBCSpec::setCustomSchema);


        sqleditor = new AceEditor();
        sqleditor.setCaption("SQL");
        sqleditor.setReadOnly(true);
        sqleditor.setMode(AceMode.sql);

        sqleditor.setWidth("100%");
        sqleditor.setShowGutter(false);
        addComponent(sqleditor);
        binder.forField(sqleditor)
                .bind(SmarkTaskReadJDBCSpec::getSql, SmarkTaskReadJDBCSpec::setSql);

        editSQLButton.addClickListener(event -> {
                    if( binder.validate().isOk()){
                        SmarkTaskReadJDBCSpec taskSpec = binder.getBean();
                        SmarkTaskJDBCTaskFormHelper.exploreDB(taskSpec,builderUI);
                    }
                }
        );

    }




}