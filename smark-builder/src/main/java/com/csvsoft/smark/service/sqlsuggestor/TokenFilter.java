package com.csvsoft.smark.service.sqlsuggestor;

import java.util.LinkedList;
import java.util.List;

public class TokenFilter {

	public static List<Token> filter(List<Token> tokenList, String tokenType) {
		List<Token> filteredList = new LinkedList<Token>();
		for (Token t : tokenList) {
			if (tokenType.equals(t.tokenType)) {
				filteredList.add(t);
			}
		}
		return filteredList;
	}

}
