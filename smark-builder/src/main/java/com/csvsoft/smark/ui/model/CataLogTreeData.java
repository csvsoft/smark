package com.csvsoft.smark.ui.model;

import com.csvsoft.smark.entity.FunctionSignature;
import com.csvsoft.smark.sevice.ICatalogueProvider;
import com.vaadin.data.TreeData;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CataLogTreeData extends TreeData<TreeItemData> {

    ICatalogueProvider catalogueProvider;

    public CataLogTreeData(ICatalogueProvider catalogueProvider) {
        this.catalogueProvider = catalogueProvider;
        init();
    }

    private void init() {
        List<String> allTables = catalogueProvider.getAllTables();

        TreeItemData tableItem = new StringTreeItemData("Tables");
        this.addRootItems(tableItem);
        Collection<TreeItemData> catalogTableTreeItems = allTables.stream().map(table -> new CatalogTableTreeItem(catalogueProvider, table)).collect(Collectors.toList());
        this.addItems(tableItem, catalogTableTreeItems);
        for(TreeItemData titem: catalogTableTreeItems){
            this.addItems(titem, titem.getChildren());
        }

        List<FunctionSignature> allFunctions = catalogueProvider.getAllFunctions();
        if (allFunctions != null && !allFunctions.isEmpty()) {
            TreeItemData functionItem = new StringTreeItemData("Functions");
            this.addRootItems(functionItem);
            Collection<TreeItemData> catalogFunctionTreeItems = allFunctions.stream().map(func -> new CatalogFunctionTreeItem(func)).collect(Collectors.toList());
            this.addItems(functionItem, catalogFunctionTreeItems);
        }

    }

}
