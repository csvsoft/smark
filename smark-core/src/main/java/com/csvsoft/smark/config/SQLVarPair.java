package com.csvsoft.smark.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SQLVarPair extends BaseSQLPair {

    private String varialeName;
    private String dataType;

    public SQLVarPair() {
        super();
    }

    public SQLVarPair(String sql, String varialeName) {
        this.setSql(sql);
        this.varialeName = varialeName;
    }

    public String getVarialeName() {
        return varialeName;
    }

    public void setVarialeName(String varialeName) {
        this.varialeName = varialeName;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SQLVarPair that = (SQLVarPair) o;
        return Objects.equals(getSqlViewPairId(), that.getSqlViewPairId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSqlViewPairId());
    }
}
