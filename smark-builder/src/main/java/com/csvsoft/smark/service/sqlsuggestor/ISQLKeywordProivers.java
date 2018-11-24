package com.csvsoft.smark.service.sqlsuggestor;

import java.util.List;

public interface ISQLKeywordProivers {

	public List<String> getKeyWords();

	public boolean isKeyword(String word);

}
