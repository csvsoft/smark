package com.csvsoft.smark.ui.model;

import com.csvsoft.smark.entity.FunctionSignature;

import java.util.Collection;

public class CatalogFunctionTreeItem implements TreeItemData {
    private FunctionSignature functionSignature;

    public CatalogFunctionTreeItem(FunctionSignature functionSignature) {
        this.functionSignature = functionSignature;
    }

    @Override
    public String getCaption() {
        return functionSignature.getName() +":" +functionSignature.getDesc();
    }

    @Override
    public Collection<TreeItemData> getChildren() {
        return null;
    }

    @Override
    public TreeItemData getParent() {
        return null;
    }
    @Override
    public String getId() {
        return functionSignature.getName();
    }
}
