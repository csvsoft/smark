package com.csvsoft.smark.ui;

import com.csvsoft.smark.config.SmarkAppSpec;
import com.csvsoft.smark.config.SmarkTaskReadCSVSpec;
import com.csvsoft.smark.config.SmarkTaskSpec;
import com.csvsoft.smark.ui.SmarkAppBuilderUI;
import com.vaadin.data.Binder;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;

public abstract class BaseTaskForm<T extends SmarkTaskSpec> extends SmarkForm {
    protected Binder<T> binder = new Binder<>();
    protected SmarkAppBuilderUI builderUI;
    protected T smarkTaskSpec;
    protected SmarkAppSpec smarkAppSpec;

    public BaseTaskForm(T smarkTaskSpec, SmarkAppSpec smarkAppSpec, SmarkAppBuilderUI builderUI) {
        super();
        this.builderUI = builderUI;
        this.smarkTaskSpec = smarkTaskSpec;
        this.smarkAppSpec = smarkAppSpec;
        binder.setBean(smarkTaskSpec);

        initUI();
        setMargin(true);
    }



    protected void commonUI(){
        TextField taskName = new TextField("Task Name");
        taskName.setWidth("30%");
        taskName.setDescription("Give the task an unique name");
        taskName.setRequiredIndicatorVisible(true);
        addComponent(taskName);
        binder.forField(taskName)
                .asRequired("Task name is required")
                .bind(SmarkTaskSpec::getName, SmarkTaskSpec::setName);

        TextField description = new TextField("Description");
        description.setDescription("Descriptions for the task");
        description.setWidth("60%");
        addComponent(description);
        binder.forField(description)
                .bind(SmarkTaskSpec::getDesc, SmarkTaskSpec::setDesc);

        TextField className = new TextField("Class Name");
        className.setWidth("30%");
        className.setDescription("Generated  code class name");
        className.setRequiredIndicatorVisible(true);
        addComponent(className);
        binder.forField(className)
                .asRequired("Class name is required")
                .bind(SmarkTaskSpec::getClassName, SmarkTaskSpec::setClassName);

    }



    public SmarkAppBuilderUI getBuilderUI() {
        return builderUI;
    }

    public void setBuilderUI(SmarkAppBuilderUI builderUI) {
        this.builderUI = builderUI;
    }

    public T getSmarkTaskSpec() {
        return smarkTaskSpec;
    }

    public void setSmarkTaskSpec(T smarkTaskSpec) {
        this.smarkTaskSpec = smarkTaskSpec;
    }

    public SmarkAppSpec getSmarkAppSpec() {
        return smarkAppSpec;
    }

    public void setSmarkAppSpec(SmarkAppSpec smarkAppSpec) {
        this.smarkAppSpec = smarkAppSpec;
    }

    protected abstract void initUI();
}
