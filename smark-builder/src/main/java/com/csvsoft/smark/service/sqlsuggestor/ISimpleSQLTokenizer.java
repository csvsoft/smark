package com.csvsoft.smark.service.sqlsuggestor;


import java.util.List;

public interface ISimpleSQLTokenizer {

	public final String TOKEN_TYPE_SPACE = "space";
	public final String TOKEN_TYPE_IDENTIFIER = "identifier";

	public void tokennize(String sql);

	public List<Token> getAllTokens();

	public List<Token> getIdentifiers();

	public List<Token> getCommentTokens(String sql);

}
