package com.csvsoft.smark.ui.model;

import com.csvsoft.smark.config.SmarkAppSpec;
import com.csvsoft.smark.config.SmarkTaskSpec;
import sun.reflect.generics.tree.Tree;

import java.util.*;
import java.util.stream.Collectors;

public class SmarkAppSpecTreeItem implements TreeItemData {
    SmarkAppSpec spec;

    @Override
    public String getCaption() {
        return spec.getName();
    }

    @Override
    public Collection<TreeItemData> getChildren() {
        List<SmarkTaskSpec> smarkTasks = spec.getSmarkTasks();
        if(smarkTasks == null || smarkTasks.isEmpty()){
            return new LinkedList<TreeItemData>();
        }
        Collection<TreeItemData> taskSpecTreeItems = smarkTasks.stream().map(task -> new SmarkTaskSpecTreeItem(task, spec)).collect(Collectors.toList());
        return taskSpecTreeItems;
    }

    @Override
    public TreeItemData getParent() {
        return null;
    }

    public SmarkAppSpec getAppSpec(){
        return spec;
    }

    public SmarkAppSpecTreeItem(SmarkAppSpec spec){
        this.spec = spec;
    }

    public void setSpec(SmarkAppSpec spec) {
        this.spec = spec;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SmarkAppSpecTreeItem that = (SmarkAppSpecTreeItem) o;
        return Objects.equals(spec, that.spec);
    }

    @Override
    public int hashCode() {

        return Objects.hash(spec);
    }

    @Override
    public String getId() {
        return this.getAppSpec().getSmarkAppSpecId();
    }
}
