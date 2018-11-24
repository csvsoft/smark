package com.csvsoft.smark.ui.model;

import com.csvsoft.smark.config.*;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SmarkTaskSpecTreeItem implements  TreeItemData {

    private SmarkTaskSpec smarkTaskSpec;

    private SmarkAppSpec smarkAppSpec;

    public SmarkTaskSpecTreeItem(SmarkTaskSpec smarkTaskSpec, SmarkAppSpec smarkAppSpec) {
        this.smarkTaskSpec = smarkTaskSpec;
        this.smarkAppSpec = smarkAppSpec;
    }

    @Override
    public String getCaption() {
        return smarkTaskSpec.getName();
    }

    @Override
    public Collection<TreeItemData> getChildren() {
        if(smarkTaskSpec instanceof SmarkTaskSQLSpec){
            SmarkTaskSQLSpec smarkTaskSQLSpec = (SmarkTaskSQLSpec)smarkTaskSpec;
            List<BaseSQLPair> sqlviewPairs = smarkTaskSQLSpec.getSqlviewPairs();
            if(sqlviewPairs!=null){
                Collection<TreeItemData> sqlviewTreeItems = sqlviewPairs.stream()
                        .map(sp ->{
                            if(sp instanceof  SQLViewPair){
                              return new SQLViewTreeItem((SQLViewPair) sp,smarkTaskSQLSpec);
                            }else if(sp instanceof  SQLVarPair){
                                return new SQLVarTreeItem((SQLVarPair) sp,smarkTaskSQLSpec);
                            }else{
                                throw new RuntimeException("Not a SQLView/var:"+sp.getClass().getName());
                            }
                            }
                        ).collect(Collectors.toList());
                return sqlviewTreeItems;
            }
        }
        return null;
    }

    @Override
    public TreeItemData getParent() {
        return new SmarkAppSpecTreeItem(smarkAppSpec);
    }

    public SmarkTaskSpec getSmarkTaskSpec() {
        return smarkTaskSpec;
    }

    public void setSmarkTaskSpec(SmarkTaskSpec smarkTaskSpec) {
        this.smarkTaskSpec = smarkTaskSpec;
    }

    public SmarkAppSpec getSmarkAppSpec() {
        return smarkAppSpec;
    }

    public void setSmarkAppSpec(SmarkAppSpec smarkAppSpec) {
        this.smarkAppSpec = smarkAppSpec;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SmarkTaskSpecTreeItem that = (SmarkTaskSpecTreeItem) o;
        return Objects.equals(smarkTaskSpec, that.smarkTaskSpec) &&
                Objects.equals(smarkAppSpec, that.smarkAppSpec);
    }

    @Override
    public int hashCode() {

        return Objects.hash(smarkTaskSpec.getSmarkTaskId(), smarkAppSpec.getSmarkAppSpecId());
    }

    @Override
    public String getId() {
        return this.smarkTaskSpec.getSmarkTaskId();
    }
}
