package com.csvsoft.smark.ui;

import com.csvsoft.codegen.service.ISQLFactory;
import com.csvsoft.codegen.service.JDBCSQLFactory;
import com.csvsoft.smark.config.SmarkAppSpec;
import com.csvsoft.smark.config.SmarkTaskBaseJDBCSpec;
import com.csvsoft.smark.config.SmarkTaskBaseJDBCSpec;
import com.csvsoft.smark.core.builder.SmarkAppBuilder;
import com.csvsoft.smark.util.JDBCConstants;
import com.csvsoft.smark.util.JDBCUtils;
import com.vaadin.data.Binder;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.*;
import org.vaadin.aceeditor.AceEditor;
import org.vaadin.aceeditor.AceMode;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class SmarkTaskJDBCTaskFormHelper {

    public static  void buildJDBCCommonFields(SmarkAppSpec appSpec,BaseTaskForm<? extends SmarkTaskBaseJDBCSpec> form, Binder<? extends SmarkTaskBaseJDBCSpec> binder,SmarkAppBuilderUI builderUI) {
       // addButtons(form,binder,appSpec,builderUI);
        form.commonUI();
        TextField jdbcUrl = new TextField("JDBC URL");
        jdbcUrl.setWidth("50%");
        jdbcUrl.setDescription("JDBC connection URL");
        jdbcUrl.setRequiredIndicatorVisible(true);
        form.addComponent(jdbcUrl);
        binder.forField(jdbcUrl)
                .asRequired("JDBC URL is required")
                .bind(SmarkTaskBaseJDBCSpec::getJdbcurl, SmarkTaskBaseJDBCSpec::setJdbcurl);

        ComboBox<String> driverClass = new ComboBox("JDBC Driver");
        driverClass.setWidth("30%");
        List<String> driverClasses = new LinkedList<>();
        driverClasses.add(JDBCConstants.DRIVER_CLASS_H2);
        driverClasses.add(JDBCConstants.DRIVER_CLASS_MYSQL);
        driverClasses.add(JDBCConstants.DRIVER_CLASS_ORACLE);
        driverClass.setItems(driverClasses);
        driverClass.setDescription("JDBC driver class");
        driverClass.setRequiredIndicatorVisible(true);
        form.addComponent(driverClass);
        binder.forField(driverClass)
                .asRequired("Driver Class is required")
                .bind(SmarkTaskBaseJDBCSpec::getDriverClass,SmarkTaskBaseJDBCSpec::setDriverClass);

        //sessionInitStatement

        String sessionInitStatmentDesc = "After each database session is opened to the remote DB and before starting to read data, this option executes a custom SQL statement (or a PL/SQL block). Use this to implement session initialization code. Example: option(\"sessionInitStatement\", \"\"\"BEGIN execute immediate 'alter session set \"_serial_direct_read\"=true'; END;\"\"\")";
        AceEditor sessionInitStatement = new AceEditor();
        sessionInitStatement.setDescription(sessionInitStatmentDesc);
        sessionInitStatement.setCaption("Session init Statement");
        sessionInitStatement.setHeight(30, Sizeable.Unit.PIXELS);
        sessionInitStatement.setMode(AceMode.sql);
        sessionInitStatement.setWidth("100%");

        sessionInitStatement.setShowGutter(true);
        form.addComponent(sessionInitStatement);
        binder.forField(sessionInitStatement)
                .bind(SmarkTaskBaseJDBCSpec::getSessionInitStatement, SmarkTaskBaseJDBCSpec::setSessionInitStatement);



        AceEditor jdbcOptions = new AceEditor();
        jdbcOptions.setCaption("JDBC Options");
        jdbcOptions.setMode(AceMode.properties);
        jdbcOptions.setWidth("100%");
        jdbcOptions.setShowGutter(true);
        form.addComponent(jdbcOptions);
        binder.forField(jdbcOptions)
                .bind(SmarkTaskBaseJDBCSpec::getJdbcOpitons, SmarkTaskBaseJDBCSpec::setJdbcOpitons);
    }

    public static  void addButtons(BaseTaskForm<? extends SmarkTaskBaseJDBCSpec> form,Binder<? extends SmarkTaskBaseJDBCSpec> binder,SmarkAppSpec smarkAppSpec,SmarkAppBuilderUI builderUI,Button... buttons){
        // Save cancel buttons
        Button saveButton = new Button("Save", event -> {
            if (binder.validate().isOk()) {
                SmarkTaskBaseJDBCSpec taskSpec = binder.getBean();
                if (smarkAppSpec.getTaskSpecById(taskSpec.getSmarkTaskId()) == null) {
                    smarkAppSpec.addSmarkTask(taskSpec);
                } else {
                }
                //builderUI.getSmarkAppSpecService().saveSmarkAppSpec(builderUI.getUserCredential(), smarkAppSpec);
               builderUI.saveAppSpec(smarkAppSpec);
                builderUI.smackAppTree.refresh(smarkAppSpec);
                builderUI.showDeskTop();
            }
        });
        if(form.builderUI.getUserCredential().isReadOnly()){
            saveButton.setEnabled(false);
        }

        Button cancelButton = new Button("Cancel", event -> {
            builderUI.showDeskTop();
        });
        Button testConButton = new Button("Test Connection", event -> {
            if( binder.validate().isOk()){
                SmarkTaskBaseJDBCSpec taskSpec = binder.getBean();

                try {
                    Connection con =JDBCUtils.getConnectin(taskSpec);
                    Notification.show("Successfully connected.");
                } catch (ClassNotFoundException e) {
                    builderUI.showError(e.getMessage());
                } catch (SQLException e) {
                    builderUI.showError(e.getMessage());
                }
            }

        });

        HorizontalLayout hl = new HorizontalLayout();
        hl.addComponent(cancelButton);
        hl.addComponent(saveButton);
        hl.addComponent(testConButton);
        hl.addComponents(buttons);
        form.addComponent(hl);
    }

    public static  Connection getConnection(SmarkTaskBaseJDBCSpec taskSpec, SmarkAppBuilderUI builderUI){
        Connection con = null;
        try {
             con =JDBCUtils.getConnectin(taskSpec);
             Notification.show("Successfully connected.");
        } catch (ClassNotFoundException e) {
            builderUI.showError(e.getMessage());
        } catch (SQLException e) {
            builderUI.showError(e.getMessage());
        }
        return con;
    }
    public  static void exploreDB(SmarkTaskBaseJDBCSpec taskSpec, SmarkAppBuilderUI builderUI){
        Connection connection = getConnection(taskSpec,builderUI);
        if(connection == null){
            return;
        }

        ISQLFactory sqlFactory = new JDBCSQLFactory(connection,taskSpec.getDriverClass());
        SQLSheet sqlSheet = new SQLSheet(sqlFactory);

        Window popWindow = builderUI.getPopWindow();
        popWindow.setCaption("Exploring Database:" + taskSpec.getJdbcurl());
        popWindow.setContent(sqlSheet);
        builderUI.addWindow(popWindow);
    }
}