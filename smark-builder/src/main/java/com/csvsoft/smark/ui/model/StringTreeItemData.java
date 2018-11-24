package com.csvsoft.smark.ui.model;

import java.util.Collection;

public class StringTreeItemData implements TreeItemData {
    private String title;

    public StringTreeItemData(String title) {
        this.title = title;
    }

    @Override
    public String getCaption() {
        return title;
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
        return title;
    }
}
