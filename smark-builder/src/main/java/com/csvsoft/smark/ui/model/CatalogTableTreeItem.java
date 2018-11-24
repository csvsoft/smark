package com.csvsoft.smark.ui.model;

import com.csvsoft.smark.sevice.ICatalogueProvider;
import com.csvsoft.smark.entity.IField;

import java.util.Collection;
import java.util.LinkedList;

public class CatalogTableTreeItem implements TreeItemData {
    private ICatalogueProvider catalogueProvider;
    private String table;
    public CatalogTableTreeItem(ICatalogueProvider catalogueProvider,String table){
        this.catalogueProvider = catalogueProvider;
        this.table = table;
    }


    @Override
    public String getCaption() {
        return table;
    }

    @Override
    public Collection<TreeItemData> getChildren() {
        IField[] fields = catalogueProvider.getFields(table);
        Collection<TreeItemData>  columnList = new LinkedList<>();
        for(IField field:fields){
            CatalogColumnTreeItem columnTreeItem = new CatalogColumnTreeItem(field.getFieldName(),field.getDataType(),field.isNullable()?"NULL":"NOT NULL",catalogueProvider);
            columnList.add(columnTreeItem);
        }

        return columnList;
    }

    @Override
    public TreeItemData getParent() {
        return null;
    }

    @Override
    public String getId() {
        return table;
    }
}
