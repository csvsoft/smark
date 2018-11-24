package com.csvsoft.smark.ui;

import com.csvsoft.smark.config.SmarkAppSpec;
import com.csvsoft.smark.config.SmarkTaskReadCSVSpec;
import com.csvsoft.smark.config.SmarkTaskSQLSpec;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;

public class SQLTaskForm  extends BaseTaskForm<SmarkTaskSQLSpec> {


    public SQLTaskForm(SmarkTaskSQLSpec smarkTaskSQLSpec, SmarkAppSpec smarkAppSpec, SmarkAppBuilderUI builderUI) {
        super(smarkTaskSQLSpec, smarkAppSpec, builderUI);
    }

    @Override
    protected void initUI() {

        commonUI();
        // Save cancel buttons
        Button saveButton = new Button("Save", event -> {
            if (binder.validate().isOk()) {
                //MyBackend.updatePersonInDatabase(person);
                SmarkTaskSQLSpec taskSpec = binder.getBean();

                if (this.smarkAppSpec.getTaskSpecById(taskSpec.getSmarkTaskId()) == null) {
                    this.smarkAppSpec.addSmarkTask(binder.getBean());
                } else {
                }
               // builderUI.getSmarkAppSpecService().saveSmarkAppSpec(builderUI.getUserCredential(), this.smarkAppSpec);
                builderUI.saveAppSpec(this.smarkAppSpec);
                builderUI.smackAppTree.refresh(smarkAppSpec);
               // builderUI.showDeskTop();
            }
        });

        if(builderUI.getUserCredential().isReadOnly()){
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