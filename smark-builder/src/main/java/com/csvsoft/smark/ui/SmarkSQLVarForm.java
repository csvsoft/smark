package com.csvsoft.smark.ui;

import com.csvsoft.codegen.service.ISQLFactory;
import com.csvsoft.codegen.service.SparkSQLFactory;
import com.csvsoft.smark.config.BaseSQLPair;
import com.csvsoft.smark.config.SQLVarPair;
import com.csvsoft.smark.config.SmarkAppSpec;
import com.csvsoft.smark.config.SmarkTaskSQLSpec;
import com.csvsoft.smark.core.SmarkAppRunner;
import com.csvsoft.smark.core.TaskVar;
import com.csvsoft.smark.core.TaskletContext;
import com.vaadin.data.Binder;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.*;
import org.vaadin.aceeditor.AceEditor;
import scala.Option;
import scala.Some;

import java.util.Map;

public class SmarkSQLVarForm extends SmarkForm {

    protected Binder<SQLVarPair> binder = new Binder<>();
    protected SmarkAppBuilderUI builderUI;
    protected SmarkTaskSQLSpec sqlSpec;
    protected SmarkAppSpec appSpec;
    SQLSheet sqlSheet;
    SQLVarPair sQLVarPair;

    public SmarkSQLVarForm() {
        super();
    }

    public SmarkSQLVarForm(SmarkAppBuilderUI builderUI, SQLVarPair sQLVarPair, SmarkTaskSQLSpec sqlSpec, SmarkAppSpec appSpec) {
        this.builderUI = builderUI;
        this.sqlSpec = sqlSpec;
        this.appSpec = appSpec;
        this.sQLVarPair = sQLVarPair;
        binder.setBean(sQLVarPair);
        initUI();
    }

    public void initUI() {
        addButtons();
        TextField varName = new TextField("Variable Name");
        varName.setWidth("30%");
        varName.setDescription("Variable Name");
        varName.setRequiredIndicatorVisible(true);
        addComponent(varName);
        binder.forField(varName)
                .asRequired("Variable name is required")
                .bind(SQLVarPair::getVarialeName, SQLVarPair::setVarialeName);


        NativeSelect<String> dataType =
                new NativeSelect<>("Data Type");
        dataType.setItems(TaskVar.DATA_TYPES());

        dataType.setDescription("Variable data Types");
        binder.forField(dataType)
                .bind(SQLVarPair::getDataType, SQLVarPair::setDataType);

        addComponent(dataType);

        //Label sqlLabel= new Label();
        // binder.forField(sqlLabel)

        Option<TaskletContext> taskletContext = SmarkAppRunner.getTaskletContext(appSpec, sqlSpec, sQLVarPair);
        Map<String, Object> varMap = null;
        if (taskletContext.isDefined()) {
            varMap = taskletContext.get().getVarMap();
        }

        //SmarkAppRunner.refreshSession(builderUI.getSparkSession(), appSpec.getDebugRunId(), sqlSpec, new Some<BaseSQLPair>(sQLVarPair), appSpec);
        //SparkSQLFactory sparkSQLFactory = new SparkSQLFactory(builderUI.getSparkSession());
        int sqlOrder = sQLVarPair.getOrder();
        sqlOrder = sqlOrder ==0? 0:sqlOrder -1;
        String runTo = String.valueOf(sqlSpec.getOrder()) + "." + sqlOrder;
        ISQLFactory sparkSQLFactory = builderUI.getSparkSQLFactory(this.appSpec, runTo);

        sqlSheet = new SQLSheet(sparkSQLFactory, varMap);
        AceEditor ace = sqlSheet.getEditor();
        ace.setDescription("Spark Query SQL ");
        ace.setRequiredIndicatorVisible(true);


        binder.forField(ace)
                //.asRequired("Spark SQL is required")
                .bind(SQLVarPair::getSql, SQLVarPair::setSql);
        this.setSizeFull();

        Button.ClickListener clickListener = new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {

                sqlSheet.refreshTables();
                Window popWindow = builderUI.getPopWindow();
                popWindow.setCaption("Editing SQL");
                popWindow.setSizeFull();
                popWindow.setContent(sqlSheet);
                builderUI.addWindow(popWindow);

            }
        };
        SQLEditField sqlEditField = new SQLEditField(clickListener, "Spark SQL");
        sqlEditField.setDescription("The Spark SQL statement to generate the view");
        ace.addBlurListener(e -> {
            sqlEditField.setValue(ace.getValue());
        });


        addComponent(sqlEditField);
        binder.forField(sqlEditField)
                .asRequired("Spark SQL is required")
                .bind(SQLVarPair::getSql, SQLVarPair::setSql);


    }

    private void addButtons() {
        // Save cancel buttons
        Button saveButton = new Button("Save", event -> {
            if (binder.validate().isOk()) {
                //MyBackend.updatePersonInDatabase(person);
                SQLVarPair sQLVarPair = binder.getBean();
                boolean existingSQLView = sqlSpec.hasSQLViewById(sQLVarPair.getSqlViewPairId());

                if (!existingSQLView) {
                    sqlSpec.addSQLView(sQLVarPair);
                }

                // builderUI.getSmarkAppSpecService().saveSmarkAppSpec(builderUI.getUserCredential(), this.appSpec);
                builderUI.saveAppSpec(this.appSpec);
                builderUI.smackAppTree.refreshTreeItemById(sqlSpec.getSmarkTaskId());
                // builderUI.showDeskTop();
                // Notification.show("Saved SQL View");
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
