package com.csvsoft.smark.ui.model;

import java.util.Collection;

public interface TreeItemData {
    public String getCaption();
    public String getId();
    public Collection<TreeItemData> getChildren();
    public TreeItemData getParent();
}
