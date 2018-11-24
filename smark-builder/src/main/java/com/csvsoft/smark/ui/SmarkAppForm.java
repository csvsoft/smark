package com.csvsoft.smark.ui;

import com.csvsoft.smark.service.SmarkAppSpecService;
import com.vaadin.data.Binder;
import com.vaadin.server.UserError;
import com.vaadin.ui.*;
import com.csvsoft.smark.config.SmarkAppSpec;
import org.vaadin.aceeditor.AceEditor;
import org.vaadin.aceeditor.AceMode;

public class SmarkAppForm extends FormLayout {

    Binder<SmarkAppSpec> binder = new Binder<>();
    SmarkAppBuilderUI builderUI;


    public SmarkAppForm(SmarkAppSpec smarkAppSpec, SmarkAppBuilderUI builderUI) {
        this.builderUI = builderUI;
        // FormLayout form = new FormLayout();
        TextField appName = new TextField("App Name");
        appName.setDescription("Application Name");
        appName.setRequiredIndicatorVisible(true);
        addComponent(appName);

        TextField description = new TextField("Description");
        description.setDescription("Descriptions for application");
        description.setWidth("100%");
        description.setRequiredIndicatorVisible(false);
        addComponent(description);

        TextField packageName = new TextField("Package");
        packageName.setRequiredIndicatorVisible(true);
        addComponent(packageName);


        TextField className = new TextField("Class");
        className.setRequiredIndicatorVisible(true);
        addComponent(className);

        TextField rootDir = new TextField("Root Directory");
        rootDir.setDescription("The root directory for generated source code outputs");
        addComponent(rootDir);
        rootDir.setComponentError(new UserError("Doh!"));

        AceEditor appConfig = new AceEditor();
        appConfig.setCaption("App Configuration");
        appConfig.setMode(AceMode.properties);
        //appConfig.setData("SELECT * from USERS");
        appConfig.setWidth("100%");
        // /appConfig.setSizeFull();
        appConfig.setShowGutter(true);
        addComponent(appConfig);


        Button cancelButton = new Button("Cancel");
        cancelButton.addClickListener(clickEvent -> {
                    builderUI.getPopWindow().close();
                    builderUI.showDeskTop();
                }
        );

        Button saveButton = new Button("Save");
        saveButton.addClickListener(clickEvent -> {
                    if (binder.validate().isOk()) {
                        SmarkAppSpec myAppSpec = binder.getBean();
                        SmarkAppSpecService smarkAppSpecService = builderUI.getSmarkAppSpecService();
                        if (smarkAppSpecService.loadSmarkAppSpec(builderUI.getUserCredential(), myAppSpec.getName()) == null) {
                            builderUI.getSmackAppTree().addSmarkAppSpec(myAppSpec);
                        } else {
                            builderUI.getSmackAppTree().refresh(myAppSpec);
                        }
                        builderUI.saveAppSpec(myAppSpec);
                        builderUI.getPopWindow().close();
                        builderUI.showDeskTop(myAppSpec.getName() + " created successfully.");


                    }
                }

        );

        if (this.builderUI.getUserCredential().isReadOnly()) {
            saveButton.setEnabled(false);
        }

        HorizontalLayout hl = new HorizontalLayout();
        hl.addComponent(cancelButton);
        hl.addComponent(saveButton);
        addComponent(hl);

        binder.forField(appName).asRequired("App name is required").bind(SmarkAppSpec::getName, SmarkAppSpec::setName);
        binder.forField(description).bind(SmarkAppSpec::getDescription, SmarkAppSpec::setDescription);

        binder.forField(packageName).asRequired("Package name is required").bind(SmarkAppSpec::getPackageName, SmarkAppSpec::setPackageName);
        binder.forField(className).asRequired("Package name is required").bind(SmarkAppSpec::getClassName, SmarkAppSpec::setClassName);
        binder.forField(rootDir).asRequired("Code output directory is required").bind(SmarkAppSpec::getCodeOutRootDir, SmarkAppSpec::setCodeOutRootDir);
        binder.forField(appConfig).bind(SmarkAppSpec::getConfigOptions, SmarkAppSpec::setConfigOptions);
        if (smarkAppSpec != null) {
            binder.setBean(smarkAppSpec);
        }

        setMargin(true);
    }

    public SmarkAppSpec getSmarkApp() {
        return binder.getBean();
    }

    public void setSmarkAppSpec(SmarkAppSpec smarkAppSpec) {
        this.binder.setBean(smarkAppSpec);
    }
}
