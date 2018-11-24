package com.csvsoft.smark.ui.model;

import com.csvsoft.smark.core.Tasklet;

public class TaskletTypeItem {

    private String taskletType;
    private Class< ? extends Tasklet> taskletClass;

    public TaskletTypeItem(String taskletType, Class<? extends Tasklet> taskletClass) {
        this.taskletType = taskletType;
        this.taskletClass = taskletClass;
    }

    public String getTaskletType() {
        return taskletType;
    }

    public void setTaskletType(String taskletType) {
        this.taskletType = taskletType;
    }

    public Class<? extends Tasklet> getTaskletClass() {
        return taskletClass;
    }

    public void setTaskletClass(Class<? extends Tasklet> taskletClass) {
        this.taskletClass = taskletClass;
    }
}
