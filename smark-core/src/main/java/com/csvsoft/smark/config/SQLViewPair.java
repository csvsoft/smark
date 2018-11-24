package com.csvsoft.smark.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SQLViewPair extends BaseSQLPair implements HaveViews {

    private String view;
    private String persistMode;

    public SQLViewPair() {
        super();
    }

    public SQLViewPair(String sql, String view) {
        this.setSql(sql);
        this.view = view;
    }

    public String getPersistMode() {
        return persistMode;
    }

    public void setPersistMode(String persistMode) {
        this.persistMode = persistMode;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SQLViewPair that = (SQLViewPair) o;
        return Objects.equals(getSqlViewPairId(), that.getSqlViewPairId());
    }

    @Override
    public List<String> getViewNames() {
        return Arrays.asList(view);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSqlViewPairId());
    }
}
