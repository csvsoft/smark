package com.csvsoft.smark.ui.model;

import com.csvsoft.smark.config.SQLViewPair;
import com.csvsoft.smark.config.SmarkTaskSQLSpec;

import java.util.Collection;
import java.util.Objects;

public class SQLViewTreeItem implements  TreeItemData {

    SQLViewPair sqlViewPair;
    SmarkTaskSQLSpec smarkTaskSQLSpec;

    public SQLViewTreeItem(SQLViewPair sqlViewPair, SmarkTaskSQLSpec smarkTaskSQLSpec) {
        this.sqlViewPair = sqlViewPair;
        this.smarkTaskSQLSpec = smarkTaskSQLSpec;
    }

    public SQLViewPair getSqlViewPair() {
        return sqlViewPair;
    }

    public void setSqlViewPair(SQLViewPair sqlViewPair) {
        this.sqlViewPair = sqlViewPair;
    }

    public SmarkTaskSQLSpec getSmarkTaskSQLSpec() {
        return smarkTaskSQLSpec;
    }

    public void setSmarkTaskSQLSpec(SmarkTaskSQLSpec smarkTaskSQLSpec) {
        this.smarkTaskSQLSpec = smarkTaskSQLSpec;
    }

    @Override
    public String getCaption() {
        return sqlViewPair.getView();
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SQLViewTreeItem that = (SQLViewTreeItem) o;
        return Objects.equals(sqlViewPair, that.sqlViewPair) &&
                Objects.equals(smarkTaskSQLSpec, that.smarkTaskSQLSpec);
    }

    @Override
    public int hashCode() {

        return Objects.hash(sqlViewPair, smarkTaskSQLSpec);
    }
    @Override
    public String getId() {
        return this.sqlViewPair.getSqlViewPairId();
    }
}
