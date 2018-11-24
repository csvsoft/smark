package com.csvsoft.smark.ui;

import com.csvsoft.codegen.service.ISQLFactory;
import com.csvsoft.codegen.service.SparkSQLFactory;
import com.csvsoft.smark.config.SmarkAppSpec;
import com.csvsoft.smark.config.SmarkTaskSaveCSVSpec;
import com.csvsoft.smark.core.SmarkAppRunner;
import com.csvsoft.smark.util.SaveModeUtils;
import com.csvsoft.smark.util.ScalaLang;
import com.vaadin.ui.*;
import org.vaadin.aceeditor.AceEditor;

public class SmarkTaskSaveCSVForm extends BaseTaskForm<SmarkTaskSaveCSVSpec> {

    private RadioButtonGroup headerGroup;
    public SmarkTaskSaveCSVForm(SmarkTaskSaveCSVSpec smarkTaskSaveCSVSpec, SmarkAppSpec smarkAppSpec, SmarkAppBuilderUI builderUI) {
       super(smarkTaskSaveCSVSpec,smarkAppSpec,builderUI);
    }

    @Override
    protected void initUI() {
        addButtons();
        commonUI();

        TextField fileName = new TextField("File Name");
        fileName.setDescription("Fully qualified file path to save");
        addComponent(fileName);
        binder.forField(fileName)
                .asRequired("File name is required")
                .bind(SmarkTaskSaveCSVSpec::getFileName, SmarkTaskSaveCSVSpec::setFileName);

        NativeSelect<String> saveMode =
                new NativeSelect<>("Save Mode");
        saveMode.setItems(SaveModeUtils.getSaveModes());
        // saveMode.setDescription(isolationLevelDesc);
        addComponent(saveMode);
        binder.forField(saveMode)
                // .asRequired("Batch size is required")
                // .withConverter(new StringToIntegerConverter("Must enter an integer"))
                .bind(SmarkTaskSaveCSVSpec::getSaveMode,SmarkTaskSaveCSVSpec::setSaveMode);

        String headerDesc = "Ouput headers or not";
         headerGroup = new RadioButtonGroup("Header");
        headerGroup.setItems("True", "False");
        headerGroup.setValue(this.smarkTaskSpec.getHeader());
        headerGroup.setDescription(headerDesc);
        headerGroup.setRequiredIndicatorVisible(true);
        addComponent(headerGroup);


        String dateFormatDesc = "Specifies a string that indicates the date format to use when reading dates or timestamps. Custom date formats follow the formats at java.text.SimpleDateFormat. This applies to both DateType and TimestampType. By default, it is null which means trying to parse times and date by java.sql.Timestamp.valueOf() and java.sql.Date.valueOf()";

        TextField dateFormat = new TextField("Date format pattern");
        dateFormat.setDescription(dateFormatDesc);
        addComponent(dateFormat);
        binder.forField(dateFormat)
                .bind(SmarkTaskSaveCSVSpec::getDateFormat, SmarkTaskSaveCSVSpec::setDateFormat);

        NativeSelect<String> compressionCode =
                new NativeSelect<>("Select Compression code");
        compressionCode.setItems("","bzip2", "gzip", "lz4","snappy");

        compressionCode.addValueChangeListener(e->
        {

        });
        addComponent(compressionCode);
        binder.forField(compressionCode)
                .bind(SmarkTaskSaveCSVSpec::getCodec, SmarkTaskSaveCSVSpec::setCodec);

        //SparkSQLFactory sparkSQLFactory = new SparkSQLFactory(builderUI.getSparkSession());
        int runTo =  (this.smarkTaskSpec.getOrder() == 0) ? 0 : smarkTaskSpec.getOrder();
        ISQLFactory sqlFactory = builderUI.getSparkSQLFactory(this.smarkAppSpec, String.valueOf(runTo));


        SQLSheet sqlSheet = new SQLSheet(sqlFactory);
        AceEditor ace = sqlSheet.getEditor();
        ace.setDescription("Spark Query SQL ");
        ace.setRequiredIndicatorVisible(true);


        binder.forField(ace)
                .asRequired("Spark SQL is required")
                .bind(SmarkTaskSaveCSVSpec::getSql, SmarkTaskSaveCSVSpec::setSql);
        this.setSizeFull();

        Button.ClickListener clickListener = new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
               // SmarkAppRunner.refreshSession(builderUI.getSparkSession(), smarkAppSpec.getDebugRunId(), smarkTaskSpec, ScalaLang.none(),smarkAppSpec);

                Window popWindow = builderUI.getPopWindow();
                popWindow.setContent(sqlSheet);
                builderUI.addWindow(popWindow);

            }
        };
        SQLEditField sqlEditField = new SQLEditField(clickListener, "Spark SQL");
        sqlEditField.setDescription("The Spark SQL statement to generate data to be saved");
        ace.addBlurListener(e -> {
            sqlEditField.setValue(ace.getValue());
        });


        addComponent(sqlEditField);
        binder.forField(sqlEditField)
                .asRequired("Spark SQL is required")
                .bind(SmarkTaskSaveCSVSpec::getSql, SmarkTaskSaveCSVSpec::setSql);
        this.setMargin(true);
    }

    private void addButtons(){
        // Save cancel buttons
        Button saveButton = new Button("Save", event -> {
            if (binder.validate().isOk()) {

                SmarkTaskSaveCSVSpec taskSpec = binder.getBean();
                taskSpec.setHeader((String) headerGroup.getValue());
                if (this.smarkAppSpec.getTaskSpecById(taskSpec.getSmarkTaskId()) == null) {
                    this.smarkAppSpec.addSmarkTask(binder.getBean());
                } else {
                }
                //builderUI.getSmarkAppSpecService().saveSmarkAppSpec(builderUI.getUserCredential(), this.smarkAppSpec);
                builderUI.saveAppSpec(this.smarkAppSpec);
                builderUI.smackAppTree.refresh(smarkAppSpec);
                //builderUI.showDeskTop();
            }
        });

        if(this.builderUI.getUserCredential().isReadOnly()){
            saveButton.setEnabled(false);
        }

        Button cancelButton = new Button("Cancel", event -> {
            builderUI.showDeskTop();
        });

        HorizontalLayout hl = new HorizontalLayout();
        hl.addComponent(cancelButton);
        hl.addComponent(saveButton);
        addComponent(hl);
    }


}
