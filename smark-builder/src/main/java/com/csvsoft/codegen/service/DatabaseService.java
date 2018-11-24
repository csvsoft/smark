package com.csvsoft.codegen.service;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseService {

    public static Connection getConnection(String jdbcURL,String user,String password,String driverClass){
        try {
            Class clazz = Class.forName(driverClass);
            return DriverManager.getConnection(jdbcURL, user, password);
        }catch(Exception ex){
            throw new RuntimeException("Unable to anquire a connection for:"+jdbcURL,ex);
        }
    }
}
