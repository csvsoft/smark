package com.csvsoft.smark.ui;

import com.csvsoft.codegen.service.ISQLFactory;
import com.csvsoft.codegen.service.SparkSQLFactory;
import com.csvsoft.smark.config.BaseSQLPair;
import com.csvsoft.smark.config.SmarkAppSpec;
import com.csvsoft.smark.config.SmarkTaskReadCSVSpec;
import com.csvsoft.smark.config.SmarkTaskReadJDBCSpec;
import com.csvsoft.smark.core.SmarkAppRunner;
import com.csvsoft.smark.ui.model.TaskletTypeItem;
import com.csvsoft.smark.util.ScalaLang;
import com.vaadin.data.Binder;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.*;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import scala.None;
import scala.Some;

import java.io.File;
import java.util.Map;

public class DefaultReadCSVTaskForm extends BaseTaskForm<SmarkTaskReadCSVSpec> {


    public DefaultReadCSVTaskForm(SmarkTaskReadCSVSpec smarkTaskReadCSVSpec, SmarkAppSpec smarkAppSpec, SmarkAppBuilderUI builderUI) {
        super(smarkTaskReadCSVSpec, smarkAppSpec, builderUI);
    }

    @Override
    protected void initUI() {
        commonUI();
        TextField fileName = new TextField("File Name");
        fileName.setWidth("50%");
        fileName.setDescription("location of files. Similar to Spark can accept standard Hadoop globbing expressions. CSV File name, the file name could contain variables defined in application configuration properties");
        fileName.setRequiredIndicatorVisible(true);
        addComponent(fileName);
        binder.forField(fileName)
                .asRequired("File name is required")
                .bind(SmarkTaskReadCSVSpec::getFileName, SmarkTaskReadCSVSpec::setFileName);

        TextField charset = new TextField("Character Set");
        charset.setDescription("Valid Java supported character set being used by the text file");
        charset.setRequiredIndicatorVisible(true);
        addComponent(charset);
        binder.forField(charset)
                .asRequired("Character set is required to parse the CSV file")
                .bind(SmarkTaskReadCSVSpec::getCharset, SmarkTaskReadCSVSpec::setCharset);

        TextField comment = new TextField("Comment character");
        comment.setMaxLength(1);
        comment.setDescription("Skip lines beginning with this character. Default is \"#\". Disable comments by setting this to null.");
        comment.setRequiredIndicatorVisible(true);
        addComponent(comment);
        binder.forField(comment)
                .asRequired("Comment character")
                .bind(SmarkTaskReadCSVSpec::getComment, SmarkTaskReadCSVSpec::setComment);


        TextField viewName = new TextField("View Name");
        viewName.setDescription("Spark View Name ");
        viewName.setRequiredIndicatorVisible(true);
        addComponent(viewName);
        binder.forField(viewName)
                .asRequired("View name is required")
                .bind(SmarkTaskReadCSVSpec::getViewName, SmarkTaskReadCSVSpec::setViewName);

        String headerDesc = "When set to true the first line of files will be used to name columns and will not be included in data. All types will be assumed string. Default value is false.";
        RadioButtonGroup headerGroup = new RadioButtonGroup("Header");
        headerGroup.setItems("True", "False");
        headerGroup.setValue(this.smarkTaskSpec.getHeader());
        headerGroup.setDescription(headerDesc);
        headerGroup.setRequiredIndicatorVisible(true);
        addComponent(headerGroup);


        String modeDesc = "Determines the parsing mode. By default it is PERMISSIVE. Possible values are:\n" +
                "<ul><li>PERMISSIVE: tries to parse all lines: nulls are inserted for missing tokens and extra tokens are ignored.</li>" +
                "<li>DROPMALFORMED: drops lines which have fewer or more tokens than expected or tokens which do not match the schema</li>" +
                "<li>FAILFAST: aborts with a RuntimeException if encounters any malformed line </li>" +
                "</ul>";
        NativeSelect<String> readCSVMode =
                new NativeSelect<>("CSV Parse Mode");
        readCSVMode.setItems("PERMISSIVE", "DROPMALFORMED", "FAILFAST");
        readCSVMode.setDescription(modeDesc, ContentMode.HTML);
        binder.forField(readCSVMode)
                .bind(SmarkTaskReadCSVSpec::getCsvReadMode, SmarkTaskReadCSVSpec::setCsvReadMode);

        addComponent(readCSVMode);

        TextField delimter = new TextField("Delimiter");
        delimter.setMaxLength(1);
        delimter.setDescription("Field Delimiter, by default is comma,");
        delimter.setRequiredIndicatorVisible(true);
        addComponent(delimter);
        binder.forField(delimter)
                .asRequired("Delimiter is required")
                .bind(SmarkTaskReadCSVSpec::getDelimiter, SmarkTaskReadCSVSpec::setDelimiter);

        String quoteDesc = "By default the quote character is \", but can be set to any character. Delimiters inside quotes are ignored";
        TextField quoteChar = new TextField("Quote Char");
        quoteChar.setDescription(quoteDesc);

        quoteChar.setMaxLength(1);
        quoteChar.setDescription(quoteDesc);
        quoteChar.setRequiredIndicatorVisible(true);
        addComponent(quoteChar);
        binder.forField(quoteChar)
                .asRequired("Quote Char is required")
                .bind(SmarkTaskReadCSVSpec::getQuoteChar, SmarkTaskReadCSVSpec::setQuoteChar);

        String escapeDesc = "By default the quote character is \", but can be set to any character. Delimiters inside quotes are ignored";
        TextField escapeChar = new TextField("Escape Char");
        escapeChar.setDescription(escapeDesc);
        escapeChar.setMaxLength(1);
        escapeChar.setRequiredIndicatorVisible(true);
        addComponent(escapeChar);
        binder.forField(escapeChar)
                .asRequired("Escape Char is required")
                .bind(SmarkTaskReadCSVSpec::getEscapeChar, SmarkTaskReadCSVSpec::setEscapeChar);

        String dateFormatDesc = " specifies a string that indicates the date format to use when reading dates or timestamps. Custom date formats follow the formats at java.text.SimpleDateFormat. This applies to both DateType and TimestampType. By default, it is null which means trying to parse times and date by java.sql.Timestamp.valueOf() and java.sql.Date.valueOf()";

        TextField dateFormat = new TextField("Date format pattern");
        dateFormat.setDescription(dateFormatDesc);
        addComponent(dateFormat);
        binder.forField(dateFormat)

                .bind(SmarkTaskReadCSVSpec::getDateFormat, SmarkTaskReadCSVSpec::setDateFormat);

        // Save cancel buttons
        Button saveButton = new Button("Save", event -> {
            if (binder.validate().isOk()) {
                //MyBackend.updatePersonInDatabase(person);
                SmarkTaskReadCSVSpec taskSpec = binder.getBean();
                taskSpec.setHeader((String) headerGroup.getValue());
                if (this.smarkAppSpec.getTaskSpecById(taskSpec.getSmarkTaskId()) == null) {
                    this.smarkAppSpec.addSmarkTask(binder.getBean());
                } else {
                }
                if (builderUI.saveAppSpec(this.smarkAppSpec)) {
                    builderUI.smackAppTree.refresh(smarkAppSpec);
                    builderUI.showDeskTop();
                }
            }
        });

        if(builderUI.getUserCredential().isReadOnly()){
            saveButton.setEnabled(false);
        }
        Button cancelButton = new Button("Cancel", event -> {
            builderUI.showDeskTop();
        });
        Button previewButton = new Button("Preview CSV", event -> {
            if (binder.validate().isOk()) {
                SmarkTaskReadCSVSpec taskReadCSVSpec = binder.getBean();
                previewCSV(taskReadCSVSpec);
            }

        });
        HorizontalLayout hl = new HorizontalLayout();
        hl.addComponent(cancelButton);
        hl.addComponent(saveButton);

        hl.addComponent(previewButton);
        addComponent(hl);

        fileName.addValueChangeListener(e -> {
                    File file = new File(fileName.getValue());
                    if (file.exists()) {
                        previewButton.setEnabled(true);
                    } else {
                        previewButton.setEnabled(false);
                    }
                }
        );
        this.setMargin(true);
    }

    private void previewCSV(SmarkTaskReadCSVSpec taskReadCSVSpec) {
        SparkSession spark = builderUI.getSparkSession();
        SmarkAppRunner.refreshSession(spark, smarkAppSpec.getDebugRunId(), taskReadCSVSpec, ScalaLang.none(), this.smarkAppSpec);

        int runTo =  (taskReadCSVSpec.getOrder() == 0) ? 0 : taskReadCSVSpec.getOrder();
        ISQLFactory sqlFactory = builderUI.getSparkSQLFactory(this.smarkAppSpec, String.valueOf(runTo));


        Map<String, String> csvConf = taskReadCSVSpec.getCsvConf();
        Dataset<Row> csvDF = spark.read().options(csvConf).csv(taskReadCSVSpec.getFileName());
        csvDF.createOrReplaceTempView(taskReadCSVSpec.getViewName());

        String viewName = taskReadCSVSpec.getViewName();
        String sql = "select * from " + viewName;

        SQLSheet sqlSheet = new SQLSheet(sqlFactory, sql);
        Window popWindow = builderUI.getPopWindow();
        popWindow.setCaption("Preview CSV:" + taskReadCSVSpec.getFileName());
        popWindow.setSizeFull();
        popWindow.setContent(sqlSheet);
        builderUI.addWindow(popWindow);

    }
}
