package com.csvsoft.smark.util;

import com.csvsoft.smark.config.SmarkTaskBaseJDBCSpec;
import com.csvsoft.smark.config.SmarkTaskReadJDBCSpec;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class JDBCUtils {


    public static  Connection getConnectin(SmarkTaskBaseJDBCSpec jdbcSpec) throws ClassNotFoundException,SQLException{
        loadDriverClass(jdbcSpec.getDriverClass());
        Connection connection = DriverManager.getConnection(jdbcSpec.getJdbcurl(), jdbcSpec.getJdbcOptionProps());
        return connection;

    }
    private static void loadDriverClass(String className) throws ClassNotFoundException {
        Class.forName(className);
    }

    public static String[] getSupportedJDBCDrivers(){
      return  new String[]{JDBCConstants.DRIVER_CLASS_H2,JDBCConstants.DRIVER_CLASS_MYSQL,JDBCConstants.DRIVER_CLASS_ORACLE};

    }
}
