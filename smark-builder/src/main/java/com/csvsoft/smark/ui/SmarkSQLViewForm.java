package com.csvsoft.smark.ui;

import com.csvsoft.codegen.service.ISQLFactory;
import com.csvsoft.codegen.service.SparkSQLFactory;
import com.csvsoft.smark.config.*;
import com.csvsoft.smark.core.SmarkAppConfig;
import com.csvsoft.smark.core.SmarkAppRunner;
import com.csvsoft.smark.core.TaskletContext;
import com.vaadin.data.Binder;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.*;
import org.vaadin.aceeditor.AceEditor;
import scala.Option;
import scala.Some;
import scala.collection.Iterator;
import scala.util.Failure;
import scala.util.Try;

import java.util.Map;

public class SmarkSQLViewForm extends SmarkForm {

    protected Binder<SQLViewPair> binder = new Binder<>();
    protected SmarkAppBuilderUI builderUI;
    protected SmarkTaskSQLSpec sqlSpec;
    protected SmarkAppSpec appSpec;
    protected SQLViewPair sqlViewPair;
    SQLSheet sqlSheet;

    public SmarkSQLViewForm() {
        super();
    }

    public SmarkSQLViewForm(SmarkAppBuilderUI builderUI, SQLViewPair sqlViewPair, SmarkTaskSQLSpec sqlSpec, SmarkAppSpec appSpec) {
        this.builderUI = builderUI;
        this.sqlSpec = sqlSpec;
        this.appSpec = appSpec;
        this.sqlViewPair = sqlViewPair;
        binder.setBean(sqlViewPair);
        initUI();
    }

    public void initUI() {
        addButtons();
        TextField viewName = new TextField("View Name");
        viewName.setWidth("30%");
        viewName.setDescription("Registered Temporary View Name");
        viewName.setRequiredIndicatorVisible(true);
        addComponent(viewName);
        binder.forField(viewName)
                .asRequired("View name is required")
                .bind(SQLViewPair::getView, SQLViewPair::setView);


        String modeDesc = "Data Frame Cache Options, it will allow future actions runs faster, if the data set if referenced mltople times";
        NativeSelect<String> persistMode =
                new NativeSelect<>("Persist Mode");
        persistMode.setItems("MEMORY_AND_DISK", "MEMORY_ONLY", "DISK_ONLY", "MEMORY_ONLY_SER", "MEMORY_AND_DISK_SER", "OFF_HEAP");

        persistMode.setDescription(modeDesc, ContentMode.HTML);
        binder.forField(persistMode)
                .bind(SQLViewPair::getPersistMode, SQLViewPair::setPersistMode);

        addComponent(persistMode);

        Option<TaskletContext> taskletContext = SmarkAppRunner.getTaskletContext(appSpec, sqlSpec, sqlViewPair);
        Map<String, Object> varMap = null;
        if (taskletContext.isDefined()) {
            varMap = taskletContext.get().getVarMap();
        }


       // SmarkAppRunner.refreshSession(builderUI.getSparkSession(), appSpec.getDebugRunId(), sqlSpec, new Some<BaseSQLPair>(sqlViewPair),appSpec);
//        sqlSheet.refreshTables();

//        SparkSQLFactory sparkSQLFactory = new SparkSQLFactory(builderUI.getSparkSession());
        int sqlOrder = sqlViewPair.getOrder();
        sqlOrder = sqlOrder ==0? 0:sqlOrder -1;
        String runTo = String.valueOf(sqlSpec.getOrder()) + "." + sqlOrder;
        ISQLFactory sparkSQLFactory = builderUI.getSparkSQLFactory(this.appSpec, runTo);

        sqlSheet = new SQLSheet(sparkSQLFactory,varMap);
        AceEditor ace = sqlSheet.getEditor();
        ace.setDescription("Spark Query SQL ");
        ace.setRequiredIndicatorVisible(true);


        binder.forField(ace)
                //.asRequired("Spark SQL is required")
                .bind(SQLViewPair::getSql, SQLViewPair::setSql);
        this.setSizeFull();

        Button.ClickListener clickListener = new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
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
                .bind(SQLViewPair::getSql, SQLViewPair::setSql);


    }

    private void addButtons() {
        // Save cancel buttons
        Button saveButton = new Button("Save", event -> {
            if (binder.validate().isOk()) {
                //MyBackend.updatePersonInDatabase(person);
                SQLViewPair sqlViewPair = binder.getBean();
                boolean existingSQLView = sqlSpec.hasSQLViewById(sqlViewPair.getSqlViewPairId());

                if (!existingSQLView) {
                    sqlSpec.addSQLView(sqlViewPair);
                }

               // builderUI.getSmarkAppSpecService().saveSmarkAppSpec(builderUI.getUserCredential(), this.appSpec);
                builderUI.saveAppSpec(this.appSpec);
                builderUI.smackAppTree.refreshTreeItemById(sqlSpec.getSmarkTaskId());
                //builderUI.showDeskTop();
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
