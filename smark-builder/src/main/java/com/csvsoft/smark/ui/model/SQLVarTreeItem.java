package com.csvsoft.smark.ui.model;

import com.csvsoft.smark.config.SQLVarPair;
import com.csvsoft.smark.config.SmarkTaskSQLSpec;

import java.util.Collection;
import java.util.Objects;

public class SQLVarTreeItem implements  TreeItemData {

    SQLVarPair sqlVarPair;
    SmarkTaskSQLSpec smarkTaskSQLSpec;

    public SQLVarTreeItem(SQLVarPair sqlViewPair, SmarkTaskSQLSpec smarkTaskSQLSpec) {
        this.sqlVarPair = sqlViewPair;
        this.smarkTaskSQLSpec = smarkTaskSQLSpec;
    }

    public SQLVarPair getSqlVarPair() {
        return sqlVarPair;
    }

    public void setSqlVarPair(SQLVarPair sqlVarPair) {
        this.sqlVarPair = sqlVarPair;
    }

    public SmarkTaskSQLSpec getSmarkTaskSQLSpec() {
        return smarkTaskSQLSpec;
    }

    public void setSmarkTaskSQLSpec(SmarkTaskSQLSpec smarkTaskSQLSpec) {
        this.smarkTaskSQLSpec = smarkTaskSQLSpec;
    }

    @Override
    public String getCaption() {
        return sqlVarPair.getVarialeName();
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
        SQLVarTreeItem that = (SQLVarTreeItem) o;
        return Objects.equals(sqlVarPair, that.sqlVarPair) &&
                Objects.equals(smarkTaskSQLSpec, that.smarkTaskSQLSpec);
    }

    @Override
    public int hashCode() {

        return Objects.hash(sqlVarPair, smarkTaskSQLSpec);
    }
    @Override
    public String getId() {
        return this.sqlVarPair.getSqlViewPairId();
    }
}
