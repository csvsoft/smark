package com.csvsoft.smark.config;


import com.csvsoft.smark.util.TemplateUtils;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;

public class PropertiesBuilder {
    public static Properties getProps(String nameValuePairs) {

        Properties prop = new Properties();
        if (StringUtils.isBlank(nameValuePairs)) {
            return prop;
        }
        Properties finalProp = prop;
        try {
            prop.load(new StringReader(nameValuePairs));

            Map<String, String> env = System.getenv();
            finalProp = evalProp(prop, env);
            int i = 0;
            boolean containsVars = true;
            while (containsVars && i < 10) {
                Map<String, String> propMap = prop2Map(finalProp);
                finalProp = evalProp(finalProp, propMap);
                containsVars = propValuesContainsVar(finalProp);
                i++;
            }
        } catch (IOException ex) {
            //this will not happen.
        }
        return finalProp;
    }

    public static Map<String, String> getMap(String nameValuePairs) {
        Properties props = getProps(nameValuePairs);
        return prop2Map(props);
    }

    private static boolean propValuesContainsVar(Properties prop) {
        Set<String> propNames = prop.stringPropertyNames();
        for (String propName : propNames) {
            String value = prop.getProperty(propName);
            if (value != null && value.contains("$")) {
                return true;
            }
        }
        return false;
    }

    private static Properties evalProp(Properties prop, Map<String, String> map) {

        Properties newProp = new Properties();
        Set<String> propNames = prop.stringPropertyNames();
        for (String propName : propNames) {
            String value = prop.getProperty(propName);
            if (value != null && value.contains("$")) {
                String newValue = TemplateUtils.merge(value, map);
                newProp.setProperty(propName, newValue);
            } else {
                newProp.setProperty(propName, value);
            }
        }
        return newProp;
    }

    public static Map<String, String> prop2Map(Properties props) {
        Map<String, String> map = new HashMap<>();
        Enumeration<String> names = (Enumeration<String>) props.propertyNames();

        while (names.hasMoreElements()) {
            String name = names.nextElement();
            map.put(name, props.getProperty(name));
        }
        return map;

    }
}
