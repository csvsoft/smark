package com.csvsoft.smark.service.sqlsuggestor;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.texen.util.PropertiesUtil;
import org.vaadin.aceeditor.Suggestion;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SQLVarSuggestor extends BaseSQLSuggestor {
    Map<String, Object> ctxMap;

    public SQLVarSuggestor(Map<String, Object> ctxMap) {
        this.ctxMap = ctxMap;
    }

    public void test(String expr) {
        this.suggs.clear();
        processVar(0, new Token("x", expr, 1, 2, 3));

        System.out.println();
        System.out.println("-------" + expr);

        for (Suggestion sug : this.suggs) {
            String txt = sug.getDisplayText() + "    " + sug.getDescriptionText() + " " + sug.getSuggestionText();
            System.out.println(txt);
        }
    }

    private String tokenStr;
    String suggestPrefix;

    @Override
    protected void processVar(int cursor, Token token) {
        tokenStr = token.sequence;
        // tokenStr.substring(0, cursor-token.start);
        int prevCursorIndex = (cursor - token.start - 1) < 0 ? 0 : (cursor - token.start - 1);
        char prev = tokenStr.charAt(prevCursorIndex);

        int index$ = tokenStr.indexOf("$");
        int index$bracket = tokenStr.indexOf("${");
        int prefixEndIndex = index$bracket >= 0 ? (index$ + 2) : (index$ + 1);
        String varStr = tokenStr.substring(index$);
        suggestPrefix = (prev == '.') ? "" : tokenStr.substring(0, prefixEndIndex);
        populateSugg4Var(varStr);
    }

    private void populateSuggsWithMapKeys(String startWith) {
        for (String key : this.ctxMap.keySet()) {
            if (startWith != null && key.equals(startWith)) {
                break;
            }
            if (startWith == null || key.startsWith(startWith)) {

                Object value = ctxMap.get(key);
                String desc = (value == null) ? "Null" : value.toString() + ":" + value.getClass().getName().replace("java.lang.", "");
                suggs.add(new MySuggestion(key, desc, suggestPrefix + key));

            }
        }
    }

    protected void populateSugg4Var(String startWith) {
        int startIndex = startWith.startsWith("${") ? 2 : 1;
        String varName = startWith.substring(startIndex);
        String[] splits = StringUtils.split(varName, ".");
        // return map keys
        if (splits.length <= 1 && !startWith.endsWith(".")) {
            populateSuggsWithMapKeys(splits.length == 1 ? splits[0] : null);
            return;
        }

        Object obj = ctxMap.get(splits[0]);
        if (obj == null) {
            return;
        }

        Class<?> objClazz = obj.getClass();
        List<String> propsList = Arrays.asList(splits);
        // evaluate the whole expression
        Class<?> prevClazz = null;

        String lastExp = null;
        if (startWith.endsWith(".")) {
            prevClazz = getExressionReturnType(objClazz, propsList.subList(1, propsList.size()));
        } else {
            prevClazz = getExressionReturnType(objClazz, propsList.subList(1, propsList.size() - 1));
            lastExp = propsList.get(propsList.size() - 1);
        }

        if (prevClazz != null) {
            final PropertyDescriptor[] propDescs = PropertyUtils.getPropertyDescriptors(prevClazz);
            List<Method> propMethods = new LinkedList<>();
            for (final PropertyDescriptor propDesc : propDescs) {
                String propName = propDesc.getName();
                if (propDesc.getReadMethod() == null) {
                    continue;
                }
                propMethods.add(propDesc.getReadMethod());

                if ("class".equals(propName)) {
                    continue;
                }
                if (lastExp != null && propName.startsWith(lastExp) || lastExp == null) {
                    MySuggestion mySuggestion = new MySuggestion(propDesc.getName(), propDesc.getPropertyType().getName(), suggestPrefix + propDesc.getName());
                    suggs.add(mySuggestion);
                }

            }

            Method[] methods = prevClazz.getMethods();
            for (Method m : methods) {
                if (m.getDeclaringClass().equals(Object.class)) {
                    continue;
                }
                if ("void".equals(m.getReturnType().getName())) {
                    continue;
                }
                if (propMethods.contains(m)) {
                    continue;
                }
                String methodName = m.getName();
                if (lastExp != null && methodName.startsWith(lastExp) || lastExp == null) {
                    List<String> paraTypes = Arrays.stream(m.getParameterTypes()).map(pt -> "a" + pt.getSimpleName()).collect(Collectors.toList());
                    String paraTxt = StringUtils.join(paraTypes, ",");
                    MySuggestion mySuggestion = new MySuggestion(methodName, "Method:" + m.getReturnType().getName(), this.suggestPrefix + methodName + "(" + paraTxt + ")");
                    suggs.add(mySuggestion);
                }
            }

        }
    }

    private Class<?> getExressionReturnType(Class<?> clazz, List<String> splits) {

        for (int i = 0; i < splits.size(); i++) {
            if (clazz == null) {
                break;
            }
            String propName = splits.get(i);
            if (propName.contains("(") && propName.contains(")")) {
                // method call
                Method[] methods = clazz.getMethods();
                List<Method> methods1 = Arrays.stream(methods).filter(m -> m.getName().equals(propName.substring(0, propName.indexOf('(')))).collect(Collectors.toList());
                if (methods1 != null && methods1.size() == 1) {
                    clazz = methods1.get(0).getReturnType();
                } else {
                    clazz = null;
                }
            } else {
                clazz = getPropertyType(clazz, propName);
            }
        }
        return clazz;
    }


    public static Class<?> getPropertyType(Class<?> clazz, String propertyName) {
        if (clazz == null)
            throw new IllegalArgumentException("Clazz must not be null.");
        if (propertyName == null)
            throw new IllegalArgumentException("PropertyName must not be null.");

        final PropertyDescriptor[] propDescs = PropertyUtils.getPropertyDescriptors(clazz);
        for (final PropertyDescriptor propDesc : propDescs) {
            if (propDesc.getName().equals(propertyName)) {
                clazz = propDesc.getPropertyType();
                return clazz;
            }
        }
        return null;
    }

}
