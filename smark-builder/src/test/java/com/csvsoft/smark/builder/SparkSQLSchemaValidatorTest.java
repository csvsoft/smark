package com.csvsoft.smark.builder;

import com.csvsoft.smark.util.SparkSQLSchemaValidator;
import org.junit.Assert;
import org.junit.Test;
import scala.Option;

public class SparkSQLSchemaValidatorTest {

    @Test
    public void testInCompleteLine(){
        String schemaText = "#column,datatype,nullable,dateFormat\n firstName";
        Option<String> msgOption = SparkSQLSchemaValidator.validateSchema(schemaText);
        Assert.assertEquals(true,msgOption.isDefined());
        Assert.assertEquals("Incomplete field definition at line:1, expected Field,Data Type, NULL/NOt NULL, Date format(Only applicable to Date/TimeStamp",msgOption.get());
    }
    @Test
    public void testInvalidCol(){
        String schemaText = "#column,datatype,nullable,dateFormat\n$firstName,String,NULL";
        Option<String> msgOption = SparkSQLSchemaValidator.validateSchema(schemaText);
        Assert.assertEquals(true,msgOption.isDefined());
        Assert.assertEquals("Field Name not valid at line:1",msgOption.get());
    }
    @Test
    public void testInvalidDataType(){
        String schemaText = "#column,datatype,nullable,dateFormat\nfirstName,Strings,NULL";
        Option<String> msgOption = SparkSQLSchemaValidator.validateSchema(schemaText);
        Assert.assertEquals(true,msgOption.isDefined());
        Assert.assertEquals("Data type not valid at line:1",msgOption.get());
    }

    @Test
    public void testInvalidNull(){
        String schemaText = "#column,datatype,nullable,dateFormat\nfirstName,String,NULLs";
        Option<String> msgOption = SparkSQLSchemaValidator.validateSchema(schemaText);
        Assert.assertEquals(true,msgOption.isDefined());
        Assert.assertEquals("Nullability can only be NULL or NOT NULL at line:1",msgOption.get());
    }
    @Test
    public void testMissingDateFormat(){
        String schemaText = "#column,datatype,nullable,dateFormat\nfirstName,Date,NULL";
        Option<String> msgOption = SparkSQLSchemaValidator.validateSchema(schemaText);
        Assert.assertEquals(true,msgOption.isDefined());
        Assert.assertEquals("Date Format is required if data type is Date at line:1",msgOption.get());
    }

    @Test
    public void testMissingInvalidDateFormat(){
        String schemaText = "#column,datatype,nullable,dateFormat\nfirstName,Date,NULL,yyyy-MI";
        Option<String> msgOption = SparkSQLSchemaValidator.validateSchema(schemaText);
        Assert.assertEquals(true,msgOption.isDefined());
        Assert.assertEquals("Not a valid Date Format, at line:1, please check:",msgOption.get());
    }
    @Test
    public void testMissingValidLine(){
        String schemaText = "#column,datatype,nullable,dateFormat\nfirstName,Date,NULL,yyyy-MM-dd";
        Option<String> msgOption = SparkSQLSchemaValidator.validateSchema(schemaText);
        Assert.assertEquals(false,msgOption.isDefined());
    }
}
