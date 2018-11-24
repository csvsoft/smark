package com.csvsoft.smark.ui.model;

import java.util.Collection;

public interface TreeItem<P,T,C> {
    public String getCaption();
    public Collection<TreeItem<P,T,C>> getChildren();
    public TreeItem<P,T,C> getParent();
}
