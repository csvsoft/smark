package com.csvsoft.smark.ui;

import com.csvsoft.smark.config.*;
import com.csvsoft.smark.core.SmarkApp;
import com.csvsoft.smark.ui.model.*;
import com.vaadin.contextmenu.ContextMenu;
import com.vaadin.contextmenu.MenuItem;
import com.vaadin.data.TreeData;
import com.vaadin.data.provider.TreeDataProvider;
import com.vaadin.shared.ui.dnd.DropEffect;
import com.vaadin.shared.ui.dnd.EffectAllowed;
import com.vaadin.shared.ui.grid.DropMode;
import com.vaadin.ui.*;
import com.vaadin.ui.components.grid.TreeGridDragSource;
import com.vaadin.ui.components.grid.TreeGridDropTarget;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SmarkAppTreeGrid extends TreeGrid<TreeItemData> {

    SmarkAppBuilderUI builderUI;
    TreeData<TreeItemData> treeData = new TreeData<>();
    TreeDataProvider treeDataProvider = new TreeDataProvider(treeData);
    SmarkAppSpec activeAppSpec;

    SmarkAppSpecTreeItem activeAppSpecTreeItem;
    SmarkTaskSpecTreeItem activeTaskSpectTreeItem;
    List<TreeItemData> draggedItems = null;

    SmarkTaskSpecTreeItem duplicatedTaskTreeItem;


    public SmarkAppTreeGrid(SmarkAppBuilderUI builderUI) {
        this.builderUI = builderUI;
        init();
        this.addColumn(TreeItemData::getCaption);
        this.setHeight("100%");
        this.setHeaderVisible(false);
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
                    } else if (treeItemData instanceof SQLVarTreeItem) {
                        SQLVarTreeItem sqlVarTreeItem = (SQLVarTreeItem) treeItemData;
                        editSQLVar(sqlVarTreeItem);
                    }

                }
        );

        enableRowDragAndDrop();

    }

    private void addContextMenu() {
        ContextMenu contextMenu = new ContextMenu(this, false);


        this.addContextClickListener(event -> {
                    GridContextClickEvent<TreeItemData> myEvent = (GridContextClickEvent<TreeItemData>) event;
                    TreeItemData treeItemData = myEvent.getItem();
                    contextMenu.removeItems();

                    if (treeItemData instanceof SmarkAppSpecTreeItem) {
                        SmarkAppSpecTreeItem specTreeItem = (SmarkAppSpecTreeItem) treeItemData;
                        activeAppSpec = specTreeItem.getAppSpec();
                        activeAppSpecTreeItem = specTreeItem;
                        MenuItem newMenuItem =contextMenu.addItem("New Task", e -> {

                            NewSmarkTaskForm newSmarkTaskForm = new NewSmarkTaskForm(builderUI, activeAppSpec);
                            Window popWindow = builderUI.getPopWindow();
                            popWindow.setCaption("New Task");
                            popWindow.setContent(newSmarkTaskForm);
                            builderUI.addWindow(popWindow);

                        });
                        if(this.builderUI.getUserCredential().isReadOnly()){
                            newMenuItem.setEnabled(false);
                        }
                        contextMenu.addItem("Edit", e -> {
                            editApp(specTreeItem);
                        });
                      MenuItem deleteMenu = contextMenu.addItem("Delete", e -> {
                            builderUI.showConfirm("Confirmation", "Are you sure to delete " + specTreeItem.getCaption() + "?"
                                    , conformResult -> {
                                        if (conformResult == false) {
                                            return;
                                        }

                                        builderUI.getSmarkAppSpecService().deleteSmarkAppSpec(builderUI.getUserCredential(), specTreeItem.getAppSpec());
                                        treeData.removeItem(specTreeItem);
                                        treeDataProvider.refreshAll();
                                        Notification.show("Removed App:" + specTreeItem.getCaption());
                                    });
                        });

                        if(this.builderUI.getUserCredential().isReadOnly()){
                            deleteMenu.setEnabled(false);
                        }
                    } else if (treeItemData instanceof SmarkTaskSpecTreeItem) {
                        SmarkTaskSpecTreeItem taskTreeItem = (SmarkTaskSpecTreeItem) treeItemData;
                        activeAppSpec = taskTreeItem.getSmarkAppSpec();
                        activeTaskSpectTreeItem = taskTreeItem;
                        activeAppSpecTreeItem = (SmarkAppSpecTreeItem) treeData.getParent(treeItemData);
                        contextMenu.addItem("Edit", e -> {
                            editTask(taskTreeItem);
                        });

                       MenuItem duplicate = contextMenu.addItem("Duplicate", e -> {
                            duplicateTask(taskTreeItem);
                        });
                        if(this.builderUI.getUserCredential().isReadOnly()){
                            duplicate.setEnabled(false);
                        }
                        if (this.duplicatedTaskTreeItem != null) {
                            contextMenu.addItem("Paste", e -> {
                                pasteTask(taskTreeItem);
                            });
                        }


                        MenuItem deleteMenuItem = contextMenu.addItem("Delete", e -> {

                            builderUI.showConfirm("Confirmation", "Are you sure to delete " + taskTreeItem.getCaption() + "?"
                                    , conformResult -> {
                                        //Notification.show("user clicked yes:"+conformResult);
                                        if (conformResult == false) {
                                            return;
                                        }
                                        treeData.removeItem(taskTreeItem);
                                        Notification.show("Removing smark tasks:" + taskTreeItem.getSmarkTaskSpec().getSmarkTaskId());
                                        //treeDataProvider.refreshItem(activeAppSpecTreeItem);
                                        treeDataProvider.refreshAll();
                                        activeAppSpec.removeSmarkTask(taskTreeItem.getSmarkTaskSpec());
                                        // builderUI.getSmarkAppSpecService().saveSmarkAppSpec(builderUI.getUserCredential(), activeAppSpec);
                                        builderUI.saveAppSpec(activeAppSpec);
                                    });

                        });
                        if(this.builderUI.getUserCredential().isReadOnly()){
                            deleteMenuItem.setEnabled(false);
                        }
                        SmarkTaskSpec smarkTaskSpec = taskTreeItem.getSmarkTaskSpec();
                        if (smarkTaskSpec instanceof SmarkTaskSQLSpec) {
                            SmarkTaskSQLSpec sqlSpec = (SmarkTaskSQLSpec) smarkTaskSpec;
                            MenuItem newSQLView = contextMenu.addItem("New SQL View", e -> {
                                SQLViewPair sp = new SQLViewPair();
                                SmarkSQLViewForm smarkSQLViewForm = new SmarkSQLViewForm(builderUI, sp, sqlSpec, activeAppSpec);
                                builderUI.setMainRightInTab(smarkSQLViewForm, "New SQL View");
                            });
                            //SmarkSQLVarForm

                            MenuItem newSQLVar =contextMenu.addItem("New SQL Var", e -> {
                                SQLVarPair sp = new SQLVarPair();
                                SmarkSQLVarForm smarkSQLViewForm = new SmarkSQLVarForm(builderUI, sp, sqlSpec, activeAppSpec);
                                builderUI.setMainRightInTab(smarkSQLViewForm, "New SQL View");
                            });

                            if(this.builderUI.getUserCredential().isReadOnly()){
                                newSQLVar.setEnabled(false);
                                newSQLView.setEnabled(false);
                            }
                        }
                    } else if (treeItemData instanceof SQLViewTreeItem) {
                        SQLViewTreeItem sqlViewTreeItem = (SQLViewTreeItem) treeItemData;
                        contextMenu.addItem("Edit", e -> {
                            editSQLView(sqlViewTreeItem);
                        });
                        MenuItem deleteMenuItem =contextMenu.addItem("Delete", e -> {

                            builderUI.showConfirm("Confirmation", "Are you sure to delete " + sqlViewTreeItem.getCaption() + "?"
                                    , conformResult -> {
                                        if (conformResult == false) {
                                            return;
                                        }
                                        removeSQLView(sqlViewTreeItem);
                                        treeDataProvider.refreshAll();
                                        Notification.show("Removed SQL View:" + sqlViewTreeItem.getCaption());
                                    });
                        });
                        if(this.builderUI.getUserCredential().isReadOnly()){
                            deleteMenuItem.setEnabled(false);

                        }
                    } else if (treeItemData instanceof SQLVarTreeItem) {
                        SQLVarTreeItem sqlVarTreeItem = (SQLVarTreeItem) treeItemData;
                        contextMenu.addItem("Edit", e -> {
                            editSQLVar(sqlVarTreeItem);
                        });
                        MenuItem deleteMenuItem=  contextMenu.addItem("Delete", e -> {

                            builderUI.showConfirm("Confirmation", "Are you sure to delete " + sqlVarTreeItem.getCaption() + "?"
                                    , conformResult -> {
                                        if (conformResult == false) {
                                            return;
                                        }
                                        removeSQLVar(sqlVarTreeItem);
                                        treeDataProvider.refreshAll();
                                        Notification.show("Removed SQL Var:" + sqlVarTreeItem.getCaption());
                                    });
                        });
                        if(this.builderUI.getUserCredential().isReadOnly()){
                            deleteMenuItem.setEnabled(false);

                        }
                    }

                    contextMenu.open(event.getMouseEventDetails().getClientX(), event.getMouseEventDetails().getClientY());
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

    private void editSQLVar(SQLVarTreeItem sqlVarTreeItem) {
        SQLVarPair sqlVarPair = sqlVarTreeItem.getSqlVarPair();
        SmarkTaskSQLSpec smarkTaskSQLSpec = sqlVarTreeItem.getSmarkTaskSQLSpec();

        TreeItemData taskViewItem = treeData.getParent(sqlVarTreeItem);
        TreeItemData appViewItem = treeData.getParent(taskViewItem);
        SmarkAppSpecTreeItem appSpecTreeItem = (SmarkAppSpecTreeItem) appViewItem;
        SmarkAppSpec appSpec = appSpecTreeItem.getAppSpec();

        SmarkSQLVarForm editSmarkTaskForm = new SmarkSQLVarForm(builderUI, sqlVarPair, smarkTaskSQLSpec, appSpec);
        builderUI.setMainRightInTab(editSmarkTaskForm, sqlVarTreeItem.getCaption());
    }

    private void removeSQLView(SQLViewTreeItem sqlViewTreeItem) {
        BaseSQLPair sqlViewPair = sqlViewTreeItem.getSqlViewPair();

        TreeItemData taskViewItem = treeData.getParent(sqlViewTreeItem);
        TreeItemData appViewItem = treeData.getParent(taskViewItem);
        SmarkAppSpecTreeItem appSpecTreeItem = (SmarkAppSpecTreeItem) appViewItem;
        SmarkAppSpec appSpec = appSpecTreeItem.getAppSpec();

        appSpec.removeSQLViewById(sqlViewPair.getSqlViewPairId());

        builderUI.saveAppSpec(appSpec);
        // builderUI.getSmarkAppSpecService().saveSmarkAppSpec(builderUI.getUserCredential(), appSpec);

        treeData.removeItem(sqlViewTreeItem);
        treeDataProvider.refreshAll();
    }

    private void removeSQLVar(SQLVarTreeItem sqlViewTreeItem) {
        SQLVarPair sqlVarPair = sqlViewTreeItem.getSqlVarPair();

        TreeItemData taskViewItem = treeData.getParent(sqlViewTreeItem);
        TreeItemData appViewItem = treeData.getParent(taskViewItem);
        SmarkAppSpecTreeItem appSpecTreeItem = (SmarkAppSpecTreeItem) appViewItem;
        SmarkAppSpec appSpec = appSpecTreeItem.getAppSpec();

        appSpec.removeSQLViewById(sqlVarPair.getSqlViewPairId());
        //builderUI.getSmarkAppSpecService().saveSmarkAppSpec(builderUI.getUserCredential(), appSpec);

        builderUI.saveAppSpec(appSpec);
        treeData.removeItem(sqlViewTreeItem);
        treeDataProvider.refreshAll();
    }

    private void duplicateTask(SmarkTaskSpecTreeItem taskItem) {
        this.duplicatedTaskTreeItem = taskItem;
        Notification.show("Duplicated:" + taskItem.getCaption());
    }

    private void saveAppSpec(SmarkAppSpec appSpec) {
        //builderUI.getSmarkAppSpecService().saveSmarkAppSpec(builderUI.getUserCredential(), appSpec);
        builderUI.saveAppSpec(appSpec);
    }

    private void pasteTask(SmarkTaskSpecTreeItem taskItem) {
        if (this.duplicatedTaskTreeItem == null) {
            return;
        }
        SmarkAppSpec smarkAppSpec = taskItem.getSmarkAppSpec();
        SmarkTaskSpec duplicatedSmarkTaskSpec = duplicatedTaskTreeItem.getSmarkTaskSpec();
        SmarkTaskSpec clonedTaskSpec = duplicatedSmarkTaskSpec.clone();
        clonedTaskSpec.generateNewId();
        clonedTaskSpec.setName(clonedTaskSpec.getName() + "_Copied");
        smarkAppSpec.addSmarkTask(clonedTaskSpec);
        saveAppSpec(smarkAppSpec);

        SmarkTaskSpecTreeItem newCopiedItem = new SmarkTaskSpecTreeItem(clonedTaskSpec, smarkAppSpec);
        this.treeData.addItem(this.treeData.getParent(taskItem), newCopiedItem);

        this.duplicatedTaskTreeItem = null;
        this.treeDataProvider.refreshAll();

    }

    private void editTask(SmarkTaskSpecTreeItem taskItem) {
        activeAppSpec = taskItem.getSmarkAppSpec();
        activeTaskSpectTreeItem = taskItem;
        activeAppSpecTreeItem = (SmarkAppSpecTreeItem) treeData.getParent(taskItem);
        SmarkTaskSpec taskSpec = taskItem.getSmarkTaskSpec();
        // Notification.show(taskSpec.getClass().getName());
        if (taskSpec instanceof SmarkTaskReadCSVSpec) {
            DefaultReadCSVTaskForm editSmarkTaskForm = new DefaultReadCSVTaskForm((SmarkTaskReadCSVSpec) taskSpec, activeAppSpec, builderUI);
            builderUI.setMainRightInTab(editSmarkTaskForm, taskItem.getCaption());
        } else if (taskSpec instanceof SmarkTaskReadJDBCSpec) {
            SmarkTaskReadJDBCForm editSmarkTaskForm = new SmarkTaskReadJDBCForm((SmarkTaskReadJDBCSpec) taskItem.getSmarkTaskSpec(), activeAppSpec, builderUI);
            builderUI.setMainRightInTab(editSmarkTaskForm, taskItem.getCaption());
        } else if (taskSpec instanceof SmarkTaskSQLSpec) {
            SQLTaskForm editSmarkTaskForm = new SQLTaskForm((SmarkTaskSQLSpec) taskItem.getSmarkTaskSpec(), activeAppSpec, builderUI);
            builderUI.setMainRightInTab(editSmarkTaskForm, taskItem.getCaption());
        } else if (taskSpec instanceof SmarkTaskSaveCSVSpec) {
            SmarkTaskSaveCSVForm editSmarkTaskForm = new SmarkTaskSaveCSVForm((SmarkTaskSaveCSVSpec) taskItem.getSmarkTaskSpec(), activeAppSpec, builderUI);
            builderUI.setMainRightInTab(editSmarkTaskForm, taskItem.getCaption());
        } else if (taskSpec instanceof SmarkTaskSaveJDBCSpec) {
            SmarkTaskSaveJDBCForm editSmarkTaskForm = new SmarkTaskSaveJDBCForm((SmarkTaskSaveJDBCSpec) taskItem.getSmarkTaskSpec(), activeAppSpec, builderUI);
            builderUI.setMainRightInTab(editSmarkTaskForm, taskItem.getCaption());
        } else if (taskSpec instanceof SmarkTaskCodeSpec) {
            SmarkTaskCodeForm editSmarkTaskForm = new SmarkTaskCodeForm((SmarkTaskCodeSpec) taskItem.getSmarkTaskSpec(), activeAppSpec, builderUI);
            builderUI.setMainRightInTab(editSmarkTaskForm, taskItem.getCaption());
        }
    }

    public void addSmarkAppSpec(SmarkAppSpec smarkAppSpec) {
        SmarkAppSpecTreeItem specTreeItem = new SmarkAppSpecTreeItem(smarkAppSpec);
        treeData = this.treeData.addItem(null, specTreeItem);
        addChildren(specTreeItem);
        treeDataProvider.refreshAll();
    }

    private void addChildren(TreeItemData treeItem) {
        Collection<TreeItemData> treeItemChildren = treeItem.getChildren();
        if (treeItemChildren == null) {
            return;
        }
        treeData.addItems(treeItem, treeItemChildren);
        //this.addColumn(TreeItemData::getCaption);
        for (TreeItemData child : treeItemChildren) {
            addChildren(child);
        }
    }

    private void enableRowDragAndDrop() {
        this.setSelectionMode(SelectionMode.SINGLE);
        getColumns().stream().forEach(col -> col.setSortable(false));

        TreeGridDropTarget<TreeItemData> dropTarget = new TreeGridDropTarget<>(this, DropMode.BETWEEN);
        dropTarget.setDropEffect(DropEffect.MOVE);
        TreeGridDragSource<TreeItemData> dragSource = new TreeGridDragSource<>(this);

        dragSource.setEffectAllowed(EffectAllowed.MOVE);

        dragSource.setDragDataGenerator("text", treeItemData -> {
            return treeItemData.getId();
        });

        dragSource.addGridDragStartListener(event ->
                // Keep reference to the dragged items
                draggedItems = event.getDraggedItems()
        );

        dragSource.addGridDragEndListener(event -> {
            // If drop was successful, remove dragged items from source Grid
            if (event.getDropEffect() == DropEffect.MOVE) {
                // draggedItems.forEach(item -> this.treeData.removeItem(item));
                //this.getDataProvider().refreshAll();
            }
        });
        dropTarget.addTreeGridDropListener(e -> {
            Optional<TreeItemData> dropTargetRow = e.getDropTargetRow();
            if (!dropTargetRow.isPresent()) {
                return;
            }
            processDragDrop(draggedItems, dropTargetRow.get());
        });
    }

    private void processDragDrop(List<TreeItemData> draggedItems, TreeItemData targetRow) {
        if (draggedItems.size() != 1) {
            return;
        }
        TreeItemData draggedItem = draggedItems.get(0);

        TreeItemData draggedParent = treeData.getParent(draggedItem);
        TreeItemData targetParent = treeData.getParent(targetRow);
        boolean targetIsParent = targetRow.equals(draggedParent);
        boolean targetIsSibling = targetParent != null && targetParent.equals(draggedParent);
        // builderUI.showError("targetIsSibling:"+targetIsSibling + "  targetIsParent:"+targetIsParent);
        if (!(targetIsParent || targetIsSibling)) {
            builderUI.showError("Not supported drag and drop");
            return;
        }

        TreeItemData parent = targetIsParent ? targetRow : targetParent;
        List<TreeItemData> children = treeData.getChildren(parent);
        List<TreeItemData> newChildren = new LinkedList<>();


        if (targetIsParent) {
            newChildren.add(draggedItem);
            LinkedList<TreeItemData> childrenCopy = new LinkedList<>(children);
            childrenCopy.remove(draggedItem);
            newChildren.addAll(childrenCopy);
        } else {
            for (TreeItemData targetItem : children) {
                if (!draggedItem.equals(targetItem)) {
                    newChildren.add(targetItem);
                }
                if (targetItem.equals(targetRow)) {
                    newChildren.add(draggedItem);
                }
            }
        }

        List<TreeItemData> childrenCopy = new LinkedList<>(children);
        childrenCopy.forEach(c -> treeData.removeItem(c));

        treeData.addItems(parent, newChildren);
        newChildren.forEach(c -> this.addChildren(c));
        this.getDataProvider().refreshAll();

        // persist the changes
        //Collection<TreeItemData> oldChildren = parent.getChildren();
        List<TreeItemData> shuffledChildren = treeData.getChildren(parent);
        if (parent instanceof SmarkAppSpecTreeItem) {
            SmarkAppSpecTreeItem appSpecTreeItem = (SmarkAppSpecTreeItem) parent;
            SmarkAppSpec appSpec = appSpecTreeItem.getAppSpec();

            List<SmarkTaskSpec> smarkTaskSpecs = shuffledChildren.stream().map(c -> {
                SmarkTaskSpecTreeItem taskTreeItem = (SmarkTaskSpecTreeItem) c;
                SmarkTaskSpec smarkTaskSpec = taskTreeItem.getSmarkTaskSpec();
                return smarkTaskSpec;
            }).collect(Collectors.toList());
            appSpec.setSmarkTasks(smarkTaskSpecs);
            //builderUI.getSmarkAppSpecService().saveSmarkAppSpec(builderUI.getUserCredential(),appSpec);
            builderUI.saveAppSpec(appSpec);

        } else if (parent instanceof SmarkTaskSpecTreeItem) {
            SmarkTaskSpecTreeItem taskSpecTreeItem = (SmarkTaskSpecTreeItem) parent;
            SmarkTaskSpec smarkTaskSpec = taskSpecTreeItem.getSmarkTaskSpec();
            SmarkAppSpec smarkAppSpec = taskSpecTreeItem.getSmarkAppSpec();
            if (smarkTaskSpec instanceof SmarkTaskSQLSpec) {
                SmarkTaskSQLSpec taskSQLSpec = (SmarkTaskSQLSpec) smarkTaskSpec;
                List<BaseSQLPair> sqlViewPairs = shuffledChildren.stream().map(c -> {
                    SQLViewTreeItem sqlViewTreeItem = (SQLViewTreeItem) c;
                    BaseSQLPair sqlViewPair = sqlViewTreeItem.getSqlViewPair();
                    return sqlViewPair;
                }).collect(Collectors.toList());
                taskSQLSpec.setSqlviewPairs(sqlViewPairs);
                // builderUI.getSmarkAppSpecService().saveSmarkAppSpec(builderUI.getUserCredential(),smarkAppSpec);
                builderUI.saveAppSpec(smarkAppSpec);
            }

        }

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
        if (activeAppSpecTreeItem != null) {
            this.activeAppSpecTreeItem.setSpec(appSpec);
            List<TreeItemData> children = treeData.getChildren(activeAppSpecTreeItem);
            Collection<TreeItemData> newChildren = activeAppSpecTreeItem.getChildren();

            if (newChildren != null) {
                for (TreeItemData newItem : newChildren) {
                    if (!children.contains(newItem)) {
                        treeData.addItem(activeAppSpecTreeItem, newItem);
                    }
                }
            }

            this.treeDataProvider.refreshItem(activeAppSpecTreeItem);
        }
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
