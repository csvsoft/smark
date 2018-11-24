package com.csvsoft.smark.util;

import org.apache.spark.sql.SaveMode;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class SaveModeUtils {
    static Map<String, SaveMode> modeSringMapping = new HashMap<>();

    static {init();}
    static void init() {
        modeSringMapping.put("Append", SaveMode.Append);
        modeSringMapping.put("OverWrite", SaveMode.Overwrite);
        modeSringMapping.put("ErrorIfExists", SaveMode.ErrorIfExists);
    }

    public static SaveMode getSaveMode(String value) {
        return modeSringMapping.get(value);
    }
    public static String[] getSaveModes(){
      return modeSringMapping.keySet().stream().collect(Collectors.toList()).toArray(new String[0]);
    }

}
