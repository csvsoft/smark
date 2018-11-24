package com.csvsoft.smark.util;

public class JDBCConstants {
    public final static String DRIVER_CLASS_H2 = "org.h2.Driver";
    public final static String DRIVER_CLASS_MYSQL = "com.mysql.jdbc.Driver";
    public final static String DRIVER_CLASS_ORACLE = "oracle.jdbc.OracleDriver";

    public final static String TRANSACTION_ISOLATION_NONE = "None";
    public final static String TRANSACTION_ISOLATION_READ_UNCOMMITTED = "READ_UNCOMMITTED";
    public final static String TRANSACTION_ISOLATION_READ_COMMITTED = "READ_COMMITTED";
    public final static String TRANSACTION_ISOLATION_REPEATABLE_READ = "REPEATABLE_READ";
    public final static String TRANSACTION_ISOLATION_SERIALIZABLE = "SERIALIZABLE";

    public final static String[] TRANSACTION_ISOLATION_LEVELS = new String[]{TRANSACTION_ISOLATION_NONE,TRANSACTION_ISOLATION_READ_UNCOMMITTED
    ,TRANSACTION_ISOLATION_READ_COMMITTED,TRANSACTION_ISOLATION_REPEATABLE_READ,TRANSACTION_ISOLATION_SERIALIZABLE};



}
