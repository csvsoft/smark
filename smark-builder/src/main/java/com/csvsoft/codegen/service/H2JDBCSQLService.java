package com.csvsoft.codegen.service;

import com.vaadin.data.provider.QuerySortOrder;

import java.sql.Connection;
import java.util.List;

public class H2JDBCSQLService extends JDBCSQLService {
    public H2JDBCSQLService(Connection con){
        super(con);
    }
    @Override
    public String getRunSQL(String sql, String filter, int limit, int offset, List<QuerySortOrder> sortOrders) {
      String sqlNoLimit = this.getRunSQLNoLimitOffSet(sql,filter,limit,offset,sortOrders);
      StringBuilder runSQL= new StringBuilder(sqlNoLimit);
      runSQL.append(" LIMIT ").append(limit).append(" OFFSET ").append(offset);
      return runSQL.toString();

    }
}
