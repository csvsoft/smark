package com.csvsoft.smark.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({SQLViewPair.class,SQLVarPair.class})
public class BaseSQLPair  implements Serializable{
    private String sql;
    private String description;

    private int order =0;

    public String getSqlViewPairId() {
        return sqlViewPairId;
    }

    private String sqlViewPairId;


    public BaseSQLPair() {
        if (sqlViewPairId == null)
            this.sqlViewPairId = UUID.randomUUID().toString();
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseSQLPair that = (BaseSQLPair) o;
        return Objects.equals(sqlViewPairId, that.sqlViewPairId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(sqlViewPairId);
    }
}
