package com.csvsoft.smark.sevice;

import com.csvsoft.smark.entity.DummyField;
import com.csvsoft.smark.entity.FunctionSignature;
import com.csvsoft.smark.entity.IField;
import org.apache.commons.lang3.StringUtils;
import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.catalog.Column;
import org.apache.spark.sql.catalog.Function;
import org.apache.spark.sql.catalog.Table;
import org.apache.spark.sql.catalyst.expressions.ExpressionDescription;
import org.apache.spark.sql.types.StructField;
import scala.Option;
import scala.Serializable;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SparkCatalogProvider implements ICatalogueProvider, Serializable {

    private transient  SparkSession sc;
    private List<String> tableNames = new ArrayList<>(0);
    private Map<String, IField[]> tableFielsMap = new HashMap<>();

    private List<FunctionSignature> functionList=new ArrayList<>(0);;
   public SparkCatalogProvider(SparkSession sc) {
        this.sc = sc;
        refresh();
    }
    public SparkCatalogProvider(){

    }

    public void refresh(){
        this.poulateTables();
        this.populateFields();
        populateFunctions();
    }

    private void poulateTables() {
        List<Table> tables = sc.catalog().listTables().collectAsList();
        List<String> tableList = tables.stream().map(t -> t.name()).collect( Collectors.toList() );;
        //String[] tableNames = (String[]) tableList.toArray();
        this.tableNames = tableList;
    }

    private void populateFields2() {
        this.tableFielsMap.clear();
        for (String tableName : this.tableNames) {
            StructField[] sFields = getSparkTableFields(tableName);
            IField[] fields = convert2IFields(sFields);
            this.tableFielsMap.put(tableName, fields);
        }
    }
    private void populateFields() {
        this.tableFielsMap.clear();
        for (String tableName : this.tableNames) {
            Column[] sFields = getSparkTableColumns(tableName);
            IField[] fields = convertCols2IFields(sFields);
            this.tableFielsMap.put(tableName, fields);
        }
    }
    private Column[] getSparkTableColumns(String tableName){
        Column[] columns = new Column[0];
        try {
            Dataset<Column> columnDataset = sc.catalog().listColumns(tableName);
           List<Column> columnList = columnDataset.collectAsList();
           columns = (Column[]) columnList.toArray();
            //Arrays.stream(columns).map(column -> column.)
        } catch (AnalysisException e) {
            e.printStackTrace();
        }
        return columns;

    }
    private IField[] convertCols2IFields(Column[] cols){
       IField[] fields = new DummyField[cols.length];
       for(int i=0;i<cols.length;i++){
           Column col=cols[i];
           IField field=new DummyField(col.name(),col.dataType(),col.description(),"",col.nullable());
           fields[i]=field;
       }
       return fields;
    }

    private void populateFunctions(){
        List<Function> functionList = sc.catalog().listFunctions().collectAsList();
        List<FunctionSignature> fsList = new ArrayList<>(functionList.size());
        for(Function f:functionList){
            try {
                if(f!=null && f.className()!=null) {
                    Class<?> clazz = Class.forName(f.className());
                    fsList.add(getFunctionSignature(f.name(), clazz));
                }else if(f!=null){
                    fsList.add(new FunctionSignature(f.name()));
                }
            } catch (ClassNotFoundException e) {
               // e.printStackTrace();
            }

        }
        this.functionList = fsList;
    }

    private IField[] convert2IFields(StructField[] sFields) {
        IField[] fields = new DummyField[sFields.length];
        for (int i = 0; i < sFields.length; i++) {
            StructField sf = sFields[i];
            Option<String> comment = sf.getComment();
            DummyField df = new DummyField(sf.name(), sf.dataType().typeName(), comment.isDefined()?comment.get():"", "", sf.nullable());
            fields[i] = df;
        }
        return fields;
    }

    private StructField[] getSparkTableFields(String tableName) {
        if (!this.tableNames.contains(tableName)) {
            return new StructField[0];
        }
        Dataset<Row> rowSet = sc.sqlContext().sql("select * from " + tableName + " where 1 = 0");
        return rowSet.schema().fields();

    }

    public List<String> getAllTables() {
        return this.tableNames;
    }

    public IField[] getFields(String tableName) {
       return this.tableFielsMap.get(tableName);
    }

    @Override
    public List<FunctionSignature> getAllFunctions() {
        return this.functionList;



    }

    @Override
    public boolean isTable(String name) {
        return this.getAllTables().contains(name);
    }

    @Override
    public boolean isView(String name) {
        return this.getAllTables().contains(name);
    }

    private FunctionSignature getFunctionSignature(String functionName,Class<?> functionClass){
        Annotation[] annotations = functionClass.getAnnotations();
        FunctionSignature fs= new FunctionSignature();
        fs.setName(functionName);
        for(Annotation annotation : annotations) {
            if (annotation instanceof ExpressionDescription) {
                ExpressionDescription myAnnotation = (ExpressionDescription) annotation;
                String usage = myAnnotation.usage();
                usage = StringUtils.replace(usage, "_FUNC_", functionName);
                fs.setDesc(usage);
                String example = StringUtils.replace(myAnnotation.extended(), "_FUNC_", functionName);
                fs.setExample(example);
            }
        }
        return fs;
    }

    @Override
    public boolean isFunction(String name) {
       if(name == null){
           return false;
       }
        for (FunctionSignature f : getAllFunctions()) {
           if(f.getName() == null){
               int x=1;
           }
            if (name.equalsIgnoreCase(f.getName())) {
                return true;
            }
        }
        return false;
    }

}
