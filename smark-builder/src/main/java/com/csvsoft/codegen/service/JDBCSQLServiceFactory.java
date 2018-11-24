package com.csvsoft.codegen.service;

import com.csvsoft.smark.util.JDBCConstants;

import java.sql.Connection;

public class JDBCSQLServiceFactory {
    public static JDBCSQLService getJDBCSQLService(String driverClassName, Connection conn){
        if(JDBCConstants.DRIVER_CLASS_H2.equals(driverClassName)){
            return new H2JDBCSQLService(conn);
        }else if(JDBCConstants.DRIVER_CLASS_MYSQL.equals(driverClassName)){
            return new MySQLJDBCSQLService(conn);
        }
        else{
            throw new RuntimeException("Unsupported driver class:"+driverClassName);
        }
    }
}
