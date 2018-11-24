package com.csvsoft.smark.ui;

import com.csvsoft.smark.config.*;
import com.csvsoft.smark.sevice.SparkCatalogProvider;
import com.csvsoft.smark.util.JDBCConstants;
import com.csvsoft.smark.util.SaveModeUtils;
import com.vaadin.data.converter.StringToBooleanConverter;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.ui.*;
import org.apache.spark.sql.SparkSession;
import org.vaadin.aceeditor.AceEditor;
import org.vaadin.aceeditor.AceMode;
import org.vaadin.ui.NumberField;

import java.util.List;

public class SmarkTaskSaveJDBCForm extends BaseTaskForm<SmarkTaskSaveJDBCSpec>{


    public SmarkTaskSaveJDBCForm(SmarkTaskSaveJDBCSpec smarkTaskSpec, SmarkAppSpec smarkAppSpec, SmarkAppBuilderUI builderUI) {
        super(smarkTaskSpec, smarkAppSpec, builderUI);
    }

    @Override
    protected void initUI() {
        Button exploreDBButton = new Button("Explore Database");
        SmarkTaskJDBCTaskFormHelper.addButtons(this,binder,this.smarkAppSpec,builderUI,exploreDBButton);

        SmarkTaskJDBCTaskFormHelper.buildJDBCCommonFields(this.smarkAppSpec,this,binder,builderUI);

        String isolationLevelDesc = "The transaction isolation level, which applies to current connection. It can be one of NONE, READ_COMMITTED, READ_UNCOMMITTED, REPEATABLE_READ, or SERIALIZABLE, corresponding to standard transaction isolation levels defined by JDBC's Connection object, with default of READ_UNCOMMITTED. This option applies only to writing. Please refer the documentation in java.sql.Connection.";


        NativeSelect<String> saveMode =
                new NativeSelect<>("Save Mode");
        saveMode.setItems(SaveModeUtils.getSaveModes());
       // saveMode.setDescription(isolationLevelDesc);
        addComponent(saveMode);
        binder.forField(saveMode)
                // .asRequired("Batch size is required")
                // .withConverter(new StringToIntegerConverter("Must enter an integer"))
                .bind(SmarkTaskSaveJDBCSpec::getSaveMode,SmarkTaskSaveJDBCSpec::setSaveMode);

        NativeSelect<String> isolationLevel =
                new NativeSelect<>("Transaction Isolation");
        isolationLevel.setItems(JDBCConstants.TRANSACTION_ISOLATION_LEVELS);
        isolationLevel.setDescription(isolationLevelDesc);
        addComponent(isolationLevel);
        binder.forField(isolationLevel)
               // .asRequired("Batch size is required")
               // .withConverter(new StringToIntegerConverter("Must enter an integer"))
                .bind(SmarkTaskSaveJDBCSpec::getIsolationLevel,SmarkTaskSaveJDBCSpec::setIsolationLevel);

        String truncateDesc = "When SaveMode.Overwrite is enabled, this option causes Spark to truncate an existing table instead of dropping and recreating it. This can be more efficient, and prevents the table metadata (e.g., indices) from being removed. However, it will not work in some cases, such as when the new data has a different schema. It defaults to false. ";
        RadioButtonGroup truncate = new RadioButtonGroup("Truncate");
        truncate.setItems("True", "False");
        truncate.setValue(this.smarkTaskSpec.isTruncate()?"True":"False");
        truncate.setDescription(truncateDesc);
        truncate.setRequiredIndicatorVisible(true);
        addComponent(truncate);

        binder.forField(truncate)
                .asRequired("Truncate is required")
                .withConverter(new StringToBooleanConverter("Not a valid boolean"));
               // .bind(SmarkTaskSaveJDBCSpec::getTruncate, SmarkTaskSaveJDBCSpec::setTruncate);

        TextField viewName = new TextField("Target Table");
        viewName.setDescription("Table name the data loading into");
        viewName.setRequiredIndicatorVisible(true);
        addComponent(viewName);
        binder.forField(viewName)
                .asRequired("View name is required")
                .bind(SmarkTaskSaveJDBCSpec::getTargetTable, SmarkTaskSaveJDBCSpec::setTargetTable);

        String creatTableOptDesc  = "This option allows setting of database-specific table and partition options when creating a table (e.g., CREATE TABLE t (name string) ENGINE=InnoDB.). ";
        TextField createTableOptions = new TextField("Create table options");
        createTableOptions.setDescription(creatTableOptDesc);

        addComponent(createTableOptions);
        binder.forField(createTableOptions)
                .bind(SmarkTaskSaveJDBCSpec::getCreateTableOptions, SmarkTaskSaveJDBCSpec::setCreateTableOptions);

        //createTableColumnTypes

        String createTableColTypeDesc = "The database column data types to use instead of the defaults, when creating the table. Data type information should be specified in the same format as CREATE TABLE columns syntax (e.g: \"name CHAR(64), comments VARCHAR(1024)\"). The specified types should be valid spark sql data types. This option applies only to writing.";
        AceEditor createTableColumnTypes = new AceEditor();
        createTableColumnTypes.setCaption("Create Table Column Types");
        createTableColumnTypes.setDescription(createTableColTypeDesc);
        createTableColumnTypes.setReadOnly(false);
        createTableColumnTypes.setMode(AceMode.sql);

        createTableColumnTypes.setWidth("100%");
        createTableColumnTypes.setShowGutter(true);
        addComponent(createTableColumnTypes);
        binder.forField(createTableColumnTypes)
                .bind(SmarkTaskSaveJDBCSpec::getCreateTableColumnTypes, SmarkTaskSaveJDBCSpec::setCreateTableColumnTypes);


        String numPartitionDesc = "The maximum number of partitions that can be used for parallelism in table reading and writing. This also determines the maximum number of concurrent JDBC connections. If the number of partitions to write exceeds this limit, we decrease it to this limit by calling coalesce(numPartitions) before writing.";
        NumberField numPartitions = new NumberField("Number of Partitions");
        numPartitions.setDecimalAllowed(false);
        numPartitions.setDescription(numPartitionDesc);
        numPartitions.setMinValue(1);
        numPartitions.setWidth("50%");

        numPartitions.setRequiredIndicatorVisible(true);
        addComponent(numPartitions);
        binder.forField(numPartitions)
                .asRequired("Number of partition is required")
                .withConverter(new StringToIntegerConverter("Must enter an integer"))
                .bind(SmarkTaskSaveJDBCSpec::getNumPartitions,SmarkTaskSaveJDBCSpec::setNumPartitions);


        String batchSizeDesc = "The JDBC batch size, which determines how many rows to insert per round trip. This can help performance on JDBC drivers. This option applies only to writing. It defaults to 1000.";
        NumberField batchSize = new NumberField("Batch Size");
        batchSize.setDecimalAllowed(false);
        batchSize.setMinValue(10);
        batchSize.setWidth("50%");
        batchSize.setDescription(batchSizeDesc);
        batchSize.setRequiredIndicatorVisible(true);
        addComponent(batchSize);
        binder.forField(batchSize)
                .asRequired("Batch size is required")
                .withConverter(new StringToIntegerConverter("Must enter an integer"))
                .bind(SmarkTaskSaveJDBCSpec::getBatchsize,SmarkTaskSaveJDBCSpec::setBatchsize);

        ComboBox<String> fromView = new ComboBox("Spark Table/View");
        fromView.setWidth("40%");
        List<String> sparkTables = getSparkTables();
        fromView.setItems(sparkTables);
        fromView.setNewItemHandler(e-> {
            String newTable =fromView.getValue();
            sparkTables.add(newTable);
            fromView.setItems(sparkTables);
            fromView.setSelectedItem(newTable);
        });
        fromView.setDescription("Spark table/view name to be loaded into database table.");
        fromView.setRequiredIndicatorVisible(true);
        addComponent(fromView);
        binder.forField(fromView)
                .asRequired("Driver Class is required")
                .bind(SmarkTaskSaveJDBCSpec::getFromView,SmarkTaskSaveJDBCSpec::setFromView);

        exploreDBButton.addClickListener(event -> {
                    if( binder.validate().isOk()){
                        SmarkTaskSaveJDBCSpec taskSpec = binder.getBean();
                        SmarkTaskJDBCTaskFormHelper.exploreDB(taskSpec,builderUI);
                    }
                }
        );

    }
    private List<String> getSparkTables(){
        SparkSession sparkSession = builderUI.getSparkSession();
        SparkCatalogProvider sparkCatalogProvider = new SparkCatalogProvider(sparkSession);
        return sparkCatalogProvider.getAllTables();

    }

}