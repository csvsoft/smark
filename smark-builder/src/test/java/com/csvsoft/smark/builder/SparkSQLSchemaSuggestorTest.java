package com.csvsoft.smark.builder;

import com.csvsoft.smark.entity.FieldDataType;
import com.csvsoft.smark.ui.model.SparkSQLSchemaSuggestor;
import org.junit.Assert;
import org.junit.Test;
import org.vaadin.aceeditor.Suggestion;

import java.util.List;

public class SparkSQLSchemaSuggestorTest {

    @Test
    public void testSuggestorFullDataType(){
        SparkSQLSchemaSuggestor sug= new SparkSQLSchemaSuggestor();
        String s="#columName,dataType,nullable,dateFormat\nfirstName,";
        List<Suggestion> suggestions = sug.getSuggestions(s, s.length());
        Assert.assertEquals(FieldDataType.TIMESTAMP.getAllDataType().size(),suggestions.size());
    }
    @Test
    public void testSuggestorStringDataType(){
        SparkSQLSchemaSuggestor sug= new SparkSQLSchemaSuggestor();
        String s="#columName,dataType,nullable,dateFormat\nfirstName,St";
        List<Suggestion> suggestions = sug.getSuggestions(s, s.length());
        Assert.assertEquals(1,suggestions.size());
        Assert.assertEquals("String",suggestions.get(0).getSuggestionText());
    }
    @Test
    public void testSuggestorNULL(){
        SparkSQLSchemaSuggestor sug= new SparkSQLSchemaSuggestor();
        String s="#columName,dataType,nullable,dateFormat\nfirstName,String,NU";
        List<Suggestion> suggestions = sug.getSuggestions(s, s.length());
        Assert.assertEquals(1,suggestions.size());
        Assert.assertEquals("NULL",suggestions.get(0).getSuggestionText());
    }
    @Test
    public void testFullNULLSuggestions(){
        SparkSQLSchemaSuggestor sug= new SparkSQLSchemaSuggestor();
        String s="#columName,dataType,nullable,dateFormat\nfirstName,String,";
        List<Suggestion> suggestions = sug.getSuggestions(s, s.length());
        Assert.assertEquals(2,suggestions.size());

    }

    @Test
    public void testSuggestionsInMiddle(){
        SparkSQLSchemaSuggestor sug= new SparkSQLSchemaSuggestor();
        String s="#columName,dataType,nullable,dateFormat\nfirstName,String,\nlastName,String";
        String s1="#columName,dataType,nullable,dateFormat\nfirstName,String,";
        List<Suggestion> suggestions = sug.getSuggestions(s, s1.length());
        Assert.assertEquals(2,suggestions.size());

    }
    @Test
    public void testCommentLineSuggestions(){
        SparkSQLSchemaSuggestor sug= new SparkSQLSchemaSuggestor();
        String s="#columName,dataType,nullable,dateFormat\n#firstName,String,";
        List<Suggestion> suggestions = sug.getSuggestions(s, s.length());
        Assert.assertEquals("No suggestions for command line",0,suggestions.size());

    }
    @Test
    public void testSpaceBeforeToken(){
        SparkSQLSchemaSuggestor sug= new SparkSQLSchemaSuggestor();
        String s="#columName,dataType,nullable,dateFormat\nfirstName,String, NU";
        List<Suggestion> suggestions = sug.getSuggestions(s, s.length());
        Assert.assertEquals(1,suggestions.size());
        Assert.assertEquals("Space should be ignored ","NULL",suggestions.get(0).getSuggestionText());

    }
    @Test
    public void testApplySuggestion(){
        SparkSQLSchemaSuggestor sug= new SparkSQLSchemaSuggestor();
        String s="#columName,dataType,nullable,dateFormat\nfirstName,String, NU";
        String expectedAfterApply ="#columName,dataType,nullable,dateFormat\nfirstName,String, NULL";
        List<Suggestion> suggestions = sug.getSuggestions(s, s.length());

        Assert.assertEquals(1,suggestions.size());

        String appliedText = sug.applySuggestion(suggestions.get(0),s,s.length());

        Assert.assertEquals("Apply suggestion should be working",expectedAfterApply,appliedText);

    }

}
