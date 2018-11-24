package com.csvsoft.smark.ui.model;

import com.csvsoft.smark.sevice.ICatalogueProvider;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;

public class CatalogColumnTreeItem implements TreeItemData {

    private String columnName;
    private String dataType;
    private String nullorNotNull;
    private ICatalogueProvider catalogueProvider;

    public CatalogColumnTreeItem(String columnName, String dataType, String nullorNotNull, ICatalogueProvider catalogueProvider) {
        this.columnName = columnName;
        this.dataType = dataType;
        this.nullorNotNull = nullorNotNull;
        this.catalogueProvider = catalogueProvider;
    }


    @Override
    public String getCaption() {
        return StringUtils.join(new String[]{columnName,dataType,nullorNotNull},":");
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
        return columnName;
    }
}
