package com.csvsoft.smark.ui;

import com.csvsoft.codegen.service.ISQLFactory;
import com.csvsoft.codegen.service.SparkSQLFactory;
import com.csvsoft.smark.config.SmarkAppSpec;
import com.csvsoft.smark.config.SmarkTaskBaseCSVSpec;
import com.csvsoft.smark.config.SmarkTaskBaseCSVSpec;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.*;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.io.File;
import java.util.Map;

public class SmarkTaskBaseCSVForm extends BaseTaskForm<SmarkTaskBaseCSVSpec> {


    public SmarkTaskBaseCSVForm(SmarkTaskBaseCSVSpec smarkTaskBaseCSVSpec, SmarkAppSpec smarkAppSpec, SmarkAppBuilderUI builderUI) {
        super(smarkTaskBaseCSVSpec,smarkAppSpec,builderUI);
    }

    @Override
    protected void initUI() {

        TextField fileName = new TextField("File Name");
        fileName.setWidth("50%");
        fileName.setDescription("location of files. Similar to Spark can accept standard Hadoop globbing expressions. CSV File name, the file name could contain variables defined in application configuration properties");
        fileName.setRequiredIndicatorVisible(true);
        addComponent(fileName);
        binder.forField(fileName)
                .asRequired("File name is required")
                .bind(SmarkTaskBaseCSVSpec::getFileName, SmarkTaskBaseCSVSpec::setFileName);

        TextField charset = new TextField("Character Set");
        charset.setDescription("Valid Java supported character set being used by the text file");
        charset.setRequiredIndicatorVisible(true);
        addComponent(charset);
        binder.forField(charset)
                .asRequired("Character set is required to parse the CSV file")
                .bind(SmarkTaskBaseCSVSpec::getCharset, SmarkTaskBaseCSVSpec::setCharset);

        String headerDesc = "When set to true the first line of files will be used to name columns and will not be included in data. All types will be assumed string. Default value is false.";
        RadioButtonGroup headerGroup = new RadioButtonGroup("Header");
        headerGroup.setItems("True", "False");
        headerGroup.setValue(this.smarkTaskSpec.getHeader());
        headerGroup.setDescription(headerDesc);
        headerGroup.setRequiredIndicatorVisible(true);
        addComponent(headerGroup);


        TextField delimter = new TextField("Delimiter");
        delimter.setMaxLength(1);
        delimter.setDescription("Field Delimiter, by default is comma,");
        delimter.setRequiredIndicatorVisible(true);
        addComponent(delimter);
        binder.forField(delimter)
                .asRequired("Delimiter is required")
                .bind(SmarkTaskBaseCSVSpec::getDelimiter, SmarkTaskBaseCSVSpec::setDelimiter);

        String quoteDesc = "By default the quote character is \", but can be set to any character. Delimiters inside quotes are ignored";
        TextField quoteChar = new TextField("Quote Char");
        quoteChar.setDescription(quoteDesc);

        quoteChar.setMaxLength(1);
        quoteChar.setDescription(quoteDesc);
        quoteChar.setRequiredIndicatorVisible(true);
        addComponent(quoteChar);
        binder.forField(quoteChar)
                .asRequired("Quote Char is required")
                .bind(SmarkTaskBaseCSVSpec::getQuoteChar, SmarkTaskBaseCSVSpec::setQuoteChar);

        String escapeDesc = "By default the quote character is \", but can be set to any character. Delimiters inside quotes are ignored";
        TextField escapeChar = new TextField("Escape Char");
        escapeChar.setDescription(escapeDesc);
        escapeChar.setMaxLength(1);
        escapeChar.setRequiredIndicatorVisible(true);
        addComponent(escapeChar);
        binder.forField(escapeChar)
                .asRequired("Escape Char is required")
                .bind(SmarkTaskBaseCSVSpec::getEscapeChar, SmarkTaskBaseCSVSpec::setEscapeChar);

        String dateFormatDesc = "Specifies a string that indicates the date format to use when reading dates or timestamps. Custom date formats follow the formats at java.text.SimpleDateFormat. This applies to both DateType and TimestampType. By default, it is null which means trying to parse times and date by java.sql.Timestamp.valueOf() and java.sql.Date.valueOf()";

        TextField dateFormat = new TextField("Date format pattern");
        dateFormat.setDescription(dateFormatDesc);
        addComponent(dateFormat);
        binder.forField(dateFormat)

                .bind(SmarkTaskBaseCSVSpec::getDateFormat, SmarkTaskBaseCSVSpec::setDateFormat);


        this.setMargin(true);
    }


}

