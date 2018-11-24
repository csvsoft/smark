package com.csvsoft.smark.entity;

import com.csvsoft.smark.entity.FieldDataType;
import org.apache.spark.sql.types.*;

import java.util.HashMap;
import java.util.Map;


public class StructFieldTypeMapper {

    static Map<String,FieldDataType> typeMap = new HashMap<>();
    static {init();}
    static void init() {
        typeMap.put(StringType.class.getName(), FieldDataType.STRING);
        typeMap.put(ByteType.class.getName(), FieldDataType.BYTE);
        typeMap.put(ShortType.class.getName(), FieldDataType.SHORT);
        typeMap.put(IntegerType.class.getName(), FieldDataType.INTEGER);
        typeMap.put(LongType.class.getName(), FieldDataType.LONG);
        typeMap.put(FloatType.class.getName(), FieldDataType.FLOAT);
        typeMap.put(DoubleType.class.getName(), FieldDataType.DOUBLE);
        typeMap.put(DecimalType.class.getName(), FieldDataType.DECIMAL);

        typeMap.put(DateType.class.getName(), FieldDataType.DATE);
        typeMap.put(TimestampType.class.getName(), FieldDataType.TIMESTAMP);
        typeMap.put(BooleanType.class.getName(), FieldDataType.BOOLEAN);
    }

    public static FieldDataType getFieldDataType(String structFieldTypeClassName){
        String key = structFieldTypeClassName;
      if(structFieldTypeClassName.endsWith("$")){
          key =structFieldTypeClassName.substring(0,structFieldTypeClassName.length()-1);
      }
        return   typeMap.get(key);
    }


}
