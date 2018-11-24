package com.csvsoft.smark.builder;

import com.csvsoft.smark.service.sqlsuggestor.*;
import com.csvsoft.smark.sevice.ICatalogueProvider;
import org.junit.Test;
import org.vaadin.aceeditor.Suggestion;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SQLSuggestorTest {
    @Test
    public void testBaseSQLSuggestor() {
        Map<String,Object> varMap= new HashMap<>();
        varMap.put("firstName","firstnameValue");
        varMap.put("app_debug","true");
        String sql = "insert into ;select u.,${app_debu, to from users /* comment1; */ as u where to_date(u.); updated x set x=x ";
        String sqi = "12345678901234567890123456789012345678901234567890";
        BaseSQLSuggestor suggestor = new SQLVarSuggestor(varMap);
        ICatalogueProvider cp = new DummyCatalogProvider();
        ISimpleSQLTokenizer t = new BaseSQLTokenizer();
        ISQLKeywordProivers k = new AnsiSQLKeywordProvider();
        suggestor.setCataLogProvider(cp);
        suggestor.setSqlTokenizer(t);
        suggestor.setKeyworkProvider(k);

       // List<Suggestion> suggs = suggestor.getSuggestions(sql, 20);
        // Assert functions

        boolean found = false;
        int cursor ="insert into ;select u.".length();
        List<Suggestion> suggFuncs = suggestor.getSuggestions(sql, cursor);
        for (Suggestion s : suggFuncs) {
           System.out.println(s.getDisplayText()+" "+s.getSuggestionText()+ " ---"+s.getDescriptionText());
            if ("to_date".equalsIgnoreCase(s.getDisplayText())) {
                found = true;
                break;
            }
        }

    }
}
