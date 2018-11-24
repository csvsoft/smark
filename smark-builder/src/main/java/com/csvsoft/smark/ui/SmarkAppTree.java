package com.csvsoft.smark.ui;

import com.csvsoft.smark.config.*;
import com.csvsoft.smark.ui.model.SQLViewTreeItem;
import com.csvsoft.smark.ui.model.SmarkAppSpecTreeItem;
import com.csvsoft.smark.ui.model.SmarkTaskSpecTreeItem;
import com.csvsoft.smark.ui.model.TreeItemData;
import com.vaadin.contextmenu.ContextMenu;
import com.vaadin.data.TreeData;
import com.vaadin.data.provider.TreeDataProvider;
import com.vaadin.shared.ui.dnd.EffectAllowed;
import com.vaadin.ui.*;
import com.vaadin.ui.dnd.DragSourceExtension;

import java.util.Collection;
import java.util.List;

public class SmarkAppTree extends Tree<TreeItemData> {

    SmarkAppBuilderUI builderUI;
    TreeData<TreeItemData> treeData = new TreeData<>();
    TreeDataProvider treeDataProvider = new TreeDataProvider(treeData);
    SmarkAppSpec activeAppSpec;

    SmarkAppSpecTreeItem activeAppSpecTreeItem;
    SmarkTaskSpecTreeItem activeTaskSpectTreeItem;


    public SmarkAppTree(SmarkAppBuilderUI builderUI) {
        this.builderUI = builderUI;
        init();
    }

    private void init() {
        this.setDataProvider(treeDataProvider);
        this.setCaption("SmarkApps");
        this.setItemCaptionGenerator(treeItemData -> {
            return treeItemData.getCaption();
        });
        addContextMenu();

        this.addItemClickListener(e -> {
                    boolean isDoubleClick = e.getMouseEventDetails().isDoubleClick();
                    if (!isDoubleClick) {
                        testDrag(e.getSource());
                        return;
                    }

                    TreeItemData treeItemData = e.getItem();
                    if (treeItemData instanceof SmarkTaskSpecTreeItem) {
                        SmarkTaskSpecTreeItem taskTreeItem = (SmarkTaskSpecTreeItem) treeItemData;
                        editTask(taskTreeItem);
                    } else if (treeItemData instanceof SmarkAppSpecTreeItem) {
                        SmarkAppSpecTreeItem appSpecTreeItem = (SmarkAppSpecTreeItem) treeItemData;
                        editApp(appSpecTreeItem);
                    } else if (treeItemData instanceof SQLViewTreeItem) {
                        SQLViewTreeItem sqlViewTreeItem = (SQLViewTreeItem) treeItemData;
                        editSQLView(sqlViewTreeItem);
                    }

                }
        );

    }

    private void addContextMenu() {
        ContextMenu contextMenu = new ContextMenu(this, false);
        this.addContextClickListener(event -> {
                    TreeContextClickEvent<TreeItemData> myEvent = (TreeContextClickEvent<TreeItemData>) event;
                    TreeItemData treeItemData = myEvent.getItem();
                    contextMenu.removeItems();

                    if (treeItemData instanceof SmarkAppSpecTreeItem) {
                        SmarkAppSpecTreeItem specTreeItem = (SmarkAppSpecTreeItem) treeItemData;
                        activeAppSpec = specTreeItem.getAppSpec();
                        activeAppSpecTreeItem = specTreeItem;
                        contextMenu.addItem("New Task", e -> {

                            NewSmarkTaskForm newSmarkTaskForm = new NewSmarkTaskForm(builderUI, activeAppSpec);
                            Window popWindow = builderUI.getPopWindow();
                            popWindow.setContent(newSmarkTaskForm);
                            builderUI.addWindow(popWindow);

                        });
                        contextMenu.addItem("Edit", e -> {
                            editApp(specTreeItem);
                        });
                    } else if (treeItemData instanceof SmarkTaskSpecTreeItem) {
                        SmarkTaskSpecTreeItem taskTreeItem = (SmarkTaskSpecTreeItem) treeItemData;
                        activeAppSpec = taskTreeItem.getSmarkAppSpec();
                        activeTaskSpectTreeItem = taskTreeItem;
                        activeAppSpecTreeItem = (SmarkAppSpecTreeItem) treeData.getParent(treeItemData);
                        contextMenu.addItem("Edit", e -> {
                            editTask(taskTreeItem);
                        });

                        contextMenu.addItem("Delete Task", e -> {

                            builderUI.showConfirm("Confirmation","Are you sure to delete " + taskTreeItem.getCaption() + "?"
                                    , conformResult -> {
                                        Notification.show("user clicked yes:"+conformResult);
                                        if (conformResult == false) {
                                            return;
                                        }
                                        treeData.removeItem(taskTreeItem);
                                        Notification.show("Removing smark tasks:" + taskTreeItem.getSmarkTaskSpec().getSmarkTaskId());
                                        //treeDataProvider.refreshItem(activeAppSpecTreeItem);
                                        treeDataProvider.refreshAll();
                                        activeAppSpec.removeSmarkTask(taskTreeItem.getSmarkTaskSpec());
                                        //builderUI.getSmarkAppSpecService().saveSmarkAppSpec(builderUI.getUserCredential(), activeAppSpec);
                                        builderUI.saveAppSpec(activeAppSpec);
                                    });


                        });
                        SmarkTaskSpec smarkTaskSpec = taskTreeItem.getSmarkTaskSpec();
                        if (smarkTaskSpec instanceof SmarkTaskSQLSpec) {
                            SmarkTaskSQLSpec sqlSpec = (SmarkTaskSQLSpec) smarkTaskSpec;
                            contextMenu.addItem("New SQL View", e -> {
                                SQLViewPair sp = new SQLViewPair();
                                SmarkSQLViewForm smarkSQLViewForm = new SmarkSQLViewForm(builderUI, sp, sqlSpec, activeAppSpec);
                                builderUI.setMainRightInTab(smarkSQLViewForm, "New SQL View");
                            });




                        }


                    } else if (treeItemData instanceof SQLViewTreeItem) {
                        SQLViewTreeItem sqlViewTreeItem = (SQLViewTreeItem) treeItemData;
                        contextMenu.addItem("Edit", e -> {
                            editSQLView(sqlViewTreeItem);
                        });
                        contextMenu.addItem("Delete", e -> {

                            removeSQLView(sqlViewTreeItem);
                            Notification.show("Removing sqlview:" + sqlViewTreeItem.getSqlViewPair().getSqlViewPairId());
                            //treeDataProvider.refreshItem(activeAppSpecTreeItem);
                            treeDataProvider.refreshAll();

                        });
                    }
                    contextMenu.open(event.getClientX(), event.getClientY());
                }

        );


    }

    private void editSQLView(SQLViewTreeItem sqlViewTreeItem) {
        SQLViewPair sqlViewPair = sqlViewTreeItem.getSqlViewPair();
        SmarkTaskSQLSpec smarkTaskSQLSpec = sqlViewTreeItem.getSmarkTaskSQLSpec();

        TreeItemData taskViewItem = treeData.getParent(sqlViewTreeItem);
        TreeItemData appViewItem = treeData.getParent(taskViewItem);
        SmarkAppSpecTreeItem appSpecTreeItem = (SmarkAppSpecTreeItem) appViewItem;
        SmarkAppSpec appSpec = appSpecTreeItem.getAppSpec();

        SmarkSQLViewForm editSmarkTaskForm = new SmarkSQLViewForm(builderUI, sqlViewPair, smarkTaskSQLSpec, appSpec);
        builderUI.setMainRightInTab(editSmarkTaskForm, sqlViewTreeItem.getCaption());
    }

    private void removeSQLView(SQLViewTreeItem sqlViewTreeItem) {
        BaseSQLPair sqlViewPair = sqlViewTreeItem.getSqlViewPair();

        TreeItemData taskViewItem = treeData.getParent(sqlViewTreeItem);
        TreeItemData appViewItem = treeData.getParent(taskViewItem);
        SmarkAppSpecTreeItem appSpecTreeItem = (SmarkAppSpecTreeItem) appViewItem;
        SmarkAppSpec appSpec = appSpecTreeItem.getAppSpec();

        appSpec.removeSQLViewById(sqlViewPair.getSqlViewPairId());
       // builderUI.getSmarkAppSpecService().saveSmarkAppSpec(builderUI.getUserCredential(), appSpec);
        builderUI.saveAppSpec(appSpec);
        treeData.removeItem(sqlViewTreeItem);
        //treeDataProvider.refreshAll();

    }

    private void editTask(SmarkTaskSpecTreeItem taskItem) {
        activeAppSpec = taskItem.getSmarkAppSpec();
        activeTaskSpectTreeItem = taskItem;
        activeAppSpecTreeItem = (SmarkAppSpecTreeItem) treeData.getParent(taskItem);
        SmarkTaskSpec taskSpec = taskItem.getSmarkTaskSpec();
        if (taskSpec instanceof SmarkTaskReadCSVSpec) {
            DefaultReadCSVTaskForm editSmarkTaskForm = new DefaultReadCSVTaskForm((SmarkTaskReadCSVSpec) taskSpec, activeAppSpec, builderUI);
            builderUI.setMainRightInTab(editSmarkTaskForm, taskItem.getCaption());
        } else if (taskSpec instanceof SmarkTaskReadJDBCSpec) {
            SmarkTaskReadJDBCForm editSmarkTaskForm = new SmarkTaskReadJDBCForm((SmarkTaskReadJDBCSpec) taskItem.getSmarkTaskSpec(), activeAppSpec, builderUI);
            builderUI.setMainRightInTab(editSmarkTaskForm, taskItem.getCaption());
        } else if (taskSpec instanceof SmarkTaskSQLSpec) {
            SQLTaskForm editSmarkTaskForm = new SQLTaskForm((SmarkTaskSQLSpec) taskItem.getSmarkTaskSpec(), activeAppSpec, builderUI);
            builderUI.setMainRightInTab(editSmarkTaskForm, taskItem.getCaption());
        }
    }

    public void addSmarkAppSpec(SmarkAppSpec smarkAppSpec) {
        SmarkAppSpecTreeItem specTreeItem = new SmarkAppSpecTreeItem(smarkAppSpec);
        treeData = this.treeData.addItem(null, specTreeItem);

        addChildren(specTreeItem);

       /* Collection<TreeItemData> specTreeItemChildren = specTreeItem.getChildren();

        if (specTreeItemChildren != null) {
            treeData = treeData.addItems(specTreeItem, specTreeItemChildren);
        }*/

        treeDataProvider.refreshAll();
    }

    private void addChildren(TreeItemData treeItem) {
        Collection<TreeItemData> treeItemChildren = treeItem.getChildren();
        if (treeItemChildren == null) {
            return;
        }
        treeData.addItems(treeItem, treeItemChildren);
        for (TreeItemData child : treeItemChildren) {
            addChildren(child);
        }
    }

    private void testDrag(AbstractComponent component) {
        DragSourceExtension<AbstractComponent> dragSource = new DragSourceExtension<>(component);
        dragSource.setEffectAllowed(EffectAllowed.MOVE);
// set the text to transfer
        dragSource.setDataTransferText("hello receiver");
// set other data to transfer (in this case HTML)
        dragSource.setDataTransferData("text/html", "<label>hello receiver</label>");

        dragSource.addDragStartListener(event -> {
                    AbstractComponent component1 = event.getComponent();


                    Object data = event.getComponent().getData();
                    Notification.show("Drag event started:" + component1.getClass().getName());
                }
        );
        dragSource.addDragEndListener(event -> {
            if (event.isCanceled()) {
                Notification.show("Drag event was canceled");
            } else {
                Notification.show("Drag event finished");
            }
        });

    }

    public TreeItemData getTreeItemById(String treeItemId) {
        List<TreeItemData> rootItems = treeData.getRootItems();
        for (TreeItemData item : rootItems) {
            TreeItemData found = find(item, treeItemId);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    public void refreshTreeItemById(String treeItemId) {
        TreeItemData treeItem = getTreeItemById(treeItemId);
        if (treeItem != null) {
            refreshTreeItem(treeItem);
            treeDataProvider.refreshAll();
            Notification.show("treeItem updated");
        } else {
            Notification.show("treeItem not found");
        }
    }

    private TreeItemData find(TreeItemData item, String id) {
        if (item.getId().equals(id)) {
            return item;
        }
        List<TreeItemData> children = treeData.getChildren(item);
        for (TreeItemData child : children) {
            TreeItemData found = find(child, id);
            if (found != null) {
                return found;
            }

        }
        return null;

    }

    public void refreshTreeItem(TreeItemData sqlTreeItem) {

        List<TreeItemData> children = treeData.getChildren(sqlTreeItem);
        Collection<TreeItemData> newChildren = sqlTreeItem.getChildren();
        if (newChildren != null) {
            for (TreeItemData newItem : newChildren) {
                if (!children.contains(newItem)) {
                    treeData.addItem(sqlTreeItem, newItem);
                }
            }
        }
        this.treeDataProvider.refreshItem(sqlTreeItem);
    }

    public void refresh(SmarkAppSpec appSpec) {
        this.activeAppSpecTreeItem.setSpec(appSpec);
        List<TreeItemData> children = treeData.getChildren(activeAppSpecTreeItem);
        Collection<TreeItemData> newChildren = activeAppSpecTreeItem.getChildren();


        /*if (children != null) {
            for (TreeItemData item : children) {
                treeData.removeItem(item);
            }
        }*/
        if (newChildren != null) {
            for (TreeItemData newItem : newChildren) {
                if (!children.contains(newItem)) {
                    treeData.addItem(activeAppSpecTreeItem, newItem);
                }
            }
        }

        this.treeDataProvider.refreshItem(activeAppSpecTreeItem);
        treeDataProvider.refreshAll();

    }

    private void editApp(SmarkAppSpecTreeItem specTreeItem) {
        SmarkAppForm appForm = new SmarkAppForm(specTreeItem.getAppSpec(), builderUI);
        builderUI.setMainRightInTab(appForm, specTreeItem.getCaption());
    }

    public SmarkAppSpec getActiveAppSpec() {
        return this.activeAppSpec;
    }


}
