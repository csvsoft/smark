package com.csvsoft.smark.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Properties;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SmarkTaskSaveJDBCSpec extends SmarkTaskBaseJDBCSpec {

    private String saveMode;
    private int parallelCount = 3;
    private String targetTable;

    private String isolationLevel = "READ_UNCOMMITTED";
    private int batchsize = 1000;
    private boolean truncate = false;
    private String createTableOptions;
    private String createTableColumnTypes;
    private String fromView;

    public String getFromView() {
        return fromView;
    }

    public void setFromView(String fromView) {
        this.fromView = fromView;
    }

    public String getIsolationLevel() {
        return isolationLevel;
    }

    public void setIsolationLevel(String isolationLevel) {
        this.isolationLevel = isolationLevel;
    }

    public int getBatchsize() {
        return batchsize;
    }

    public void setBatchsize(int batchsize) {
        this.batchsize = batchsize;
    }

    public boolean isTruncate() {
        return truncate;
    }

    public String getTruncate() {
        return truncate?"True":"False";
    }

    public void setTruncate(String truncate) {
        this.truncate = "True".equalsIgnoreCase(truncate)?true:false;
    }

    public void setTruncate(boolean truncate) {
        this.truncate = truncate;
    }
    public String getCreateTableOptions() {
        return createTableOptions;
    }

    public void setCreateTableOptions(String createTableOptions) {
        this.createTableOptions = createTableOptions;
    }

    public String getCreateTableColumnTypes() {
        return createTableColumnTypes;
    }

    public void setCreateTableColumnTypes(String createTableColumnTypes) {
        this.createTableColumnTypes = createTableColumnTypes;
    }

    public String getSaveMode() {
        return saveMode;
    }

    public void setSaveMode(String saveMode) {
        this.saveMode = saveMode;
    }

    public int getParallelCount() {
        return parallelCount;
    }

    public void setParallelCount(int parallelCount) {
        this.parallelCount = parallelCount;
    }

    public String getTargetTable() {
        return targetTable;
    }

    public void setTargetTable(String targetTable) {
        this.targetTable = targetTable;
    }
}