package com.csvsoft.smark.config;

import com.csvsoft.smark.core.entity.SQLView;

import javax.xml.bind.annotation.*;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SmarkTaskSQLSpec extends SmarkTaskSpec implements HaveViews{


    @XmlElementWrapper(name = "sql-view-pairs")
    @XmlElement(name = "sql-view-pair")
    private List<BaseSQLPair> sqlviewPairs;

    public List<BaseSQLPair> getSqlviewPairs() {
        return sqlviewPairs;
    }
    public void setSqlviewPairs(List<BaseSQLPair> sqlviewPairs) {
        this.sqlviewPairs = sqlviewPairs;
    }

    public void addSQLView(String view,String sql,String description){
        SQLViewPair p = new SQLViewPair(sql,view);
        p.setDescription(description);
        if(this.sqlviewPairs == null){
            this.sqlviewPairs = new LinkedList<>();
        }
        this.sqlviewPairs.add(p);
    }

    public void addSQLView(BaseSQLPair sqlViewPair){

        if(this.sqlviewPairs == null){
            this.sqlviewPairs = new LinkedList<>();
        }
        this.sqlviewPairs.add(sqlViewPair);
    }
    public boolean hasSQLViewById(String sqlViewId){
        return getSQLViewPairById(sqlViewId) ==null?false:true;
    }


    public BaseSQLPair getSQLViewPairById(String sqlViewId){
        if(this.sqlviewPairs == null){
            return null;
        }
        for(BaseSQLPair sp:this.sqlviewPairs){
            if(sp.getSqlViewPairId().equals(sqlViewId)){
                return sp;
            }
        }
        return null;
    }

    public boolean hasViewName(String viewName){
        return getSQLViewPairByName(viewName) == null?false:true;
    }

    public BaseSQLPair getSQLViewPairByName(String viewName){
        if(this.sqlviewPairs == null){
            return null;
        }
        for(BaseSQLPair sp:this.sqlviewPairs){
            if(sp instanceof SQLViewPair) {
                SQLViewPair svp = (SQLViewPair)sp;
                if (svp.getView().equals(viewName)) {
                    return sp;
                }
            }
        }
        return null;
    }

    public SQLVarPair getSQLVarByName(String varName){
        if(this.sqlviewPairs == null){
            return null;
        }
        for(BaseSQLPair sp:this.sqlviewPairs){
            if(sp instanceof SQLVarPair) {
                SQLVarPair svp = (SQLVarPair)sp;
                if (svp.getVarialeName().equals(varName)) {
                    return svp;
                }
            }
        }
        return null;
    }
    @Override
    public List<String> getViewNames() {
        if(this.sqlviewPairs == null){
            return null;
        }
        return sqlviewPairs.stream().filter(sp-> sp!=null && sp instanceof SQLViewPair)
                .map(sp->(SQLViewPair)sp)
                .map(sp->sp.getView()).collect(Collectors.toList());
    }

    public void adjustSQLOrder(){
        if(this.sqlviewPairs==null){
            return;
        }
        int i=0;
        for(BaseSQLPair sp:sqlviewPairs){
            sp.setOrder(i++);
        }
    }

}
