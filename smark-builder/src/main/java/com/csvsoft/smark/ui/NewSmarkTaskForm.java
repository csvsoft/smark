package com.csvsoft.smark.ui;

import com.csvsoft.smark.config.*;
import com.csvsoft.smark.core.*;
import com.csvsoft.smark.ui.model.TaskletTypeItem;
import com.vaadin.ui.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class NewSmarkTaskForm extends VerticalLayout {

    private SmarkAppBuilderUI smarkAppBuilderUI;
    private SmarkAppSpec appSpec;

    static List<TaskletTypeItem> taskletTypeItems = getTaskTypeItems();
    public NewSmarkTaskForm(SmarkAppBuilderUI smarkAppBuilderUI,SmarkAppSpec appSpec){
        this.smarkAppBuilderUI = smarkAppBuilderUI;
        this.appSpec = appSpec;
        init();
    }

    private void init(){
        TextField name = new TextField("Name");
        name.setRequiredIndicatorVisible(true);
        addComponent(name);

        NativeSelect<TaskletTypeItem> select =
                new NativeSelect<>("Select a Task Type");
        select.setItems(taskletTypeItems);
        select.setItemCaptionGenerator(TaskletTypeItem::getTaskletType);

        select.addValueChangeListener(e->
        {
            TaskletTypeItem taskletTypeItem = e.getValue();
        });
        addComponent(select);

        HorizontalLayout hl = new HorizontalLayout();
        Button saveButton = new Button("Save");
        saveButton.addClickListener(e->
        {

           // name.setComponentError(new CompositeErrorMessage("name is required"));
            Optional<TaskletTypeItem> selectedItem = select.getSelectedItem();
            if(selectedItem.isPresent()){
                TaskletTypeItem taskletTypeItem = selectedItem.get();
                Notification.show("Selected class:"+taskletTypeItem.getTaskletClass().getName());
                if(taskletTypeItem.getTaskletClass() == DefaultReadCSVTasklet.class){
                    SmarkTaskReadCSVSpec csvSpec = new SmarkTaskReadCSVSpec();
                    csvSpec.setName(name.getValue());
                    DefaultReadCSVTaskForm csvForm = new DefaultReadCSVTaskForm(csvSpec,appSpec,smarkAppBuilderUI);
                    smarkAppBuilderUI.setMainRightInTab(csvForm,csvSpec.getName());

                }else if(taskletTypeItem.getTaskletClass() == ReadJDBCTasklet.class){
                    SmarkTaskReadJDBCSpec jdbcSpec = new SmarkTaskReadJDBCSpec();
                    jdbcSpec.setName(name.getValue());
                    SmarkTaskReadJDBCForm readJDBCTaskForm = new SmarkTaskReadJDBCForm(jdbcSpec,appSpec,smarkAppBuilderUI);
                    smarkAppBuilderUI.setMainRightInTab(readJDBCTaskForm,jdbcSpec.getName());

                }else if(taskletTypeItem.getTaskletClass() == DefaultSQLTasklet.class){
                    SmarkTaskSQLSpec sqlSpec = new SmarkTaskSQLSpec();
                    sqlSpec.setName(name.getValue());
                    SQLTaskForm sqlTaskForm = new SQLTaskForm(sqlSpec,appSpec,smarkAppBuilderUI);
                    smarkAppBuilderUI.setMainRightInTab(sqlTaskForm,sqlSpec.getName());

                }else if(taskletTypeItem.getTaskletClass() == DefaultSaveCSVTasklet.class){
                    SmarkTaskSaveCSVSpec saveCSVSpec = new SmarkTaskSaveCSVSpec();
                    saveCSVSpec.setName(name.getValue());
                    SmarkTaskSaveCSVForm sqlTaskForm = new SmarkTaskSaveCSVForm(saveCSVSpec,appSpec,smarkAppBuilderUI);
                    smarkAppBuilderUI.setMainRightInTab(sqlTaskForm,saveCSVSpec.getName());
                }else if(taskletTypeItem.getTaskletClass() == SaveJDBCTasklet.class){
                    SmarkTaskSaveJDBCSpec saveJDBCSpec = new SmarkTaskSaveJDBCSpec();
                    saveJDBCSpec.setName(name.getValue());
                    SmarkTaskSaveJDBCForm sqlTaskForm = new SmarkTaskSaveJDBCForm(saveJDBCSpec,appSpec,smarkAppBuilderUI);
                    smarkAppBuilderUI.setMainRightInTab(sqlTaskForm,saveJDBCSpec.getName());
                }else if(taskletTypeItem.getTaskletClass() == DefaultCodeTasklet.class){
                    SmarkTaskCodeSpec taskCodeSpec = new SmarkTaskCodeSpec();
                    taskCodeSpec.setName(name.getValue());
                    SmarkTaskCodeForm sqlTaskForm = new SmarkTaskCodeForm(taskCodeSpec,appSpec,smarkAppBuilderUI);
                    smarkAppBuilderUI.setMainRightInTab(sqlTaskForm,taskCodeSpec.getName());
                }
                //SmarkTaskCodeForm
                smarkAppBuilderUI.getPopWindow().close();

            }
        });
        Button cancelButton = new Button("Cancel");
        hl.addComponent(saveButton);
        hl.addComponent(cancelButton);
        addComponent(hl);
    }

    private static List<TaskletTypeItem> getTaskTypeItems(){
        List<TaskletTypeItem> itemList = new LinkedList<>();
        itemList.add(new TaskletTypeItem("DefaultReadCSV", DefaultReadCSVTasklet.class));
        itemList.add(new TaskletTypeItem("ReadJDBC", ReadJDBCTasklet.class));
        itemList.add(new TaskletTypeItem("DefaultSQLTask", DefaultSQLTasklet.class));
        itemList.add(new TaskletTypeItem("SaveToCSV", DefaultSaveCSVTasklet.class));
        itemList.add(new TaskletTypeItem("SaveToJDBC", SaveJDBCTasklet.class));
        itemList.add(new TaskletTypeItem("GeneralCode", DefaultCodeTasklet.class));
        itemList.add(new TaskletTypeItem("UserDefinedFunction", UDFCodeTasklet.class));
        return itemList;
  }
}
