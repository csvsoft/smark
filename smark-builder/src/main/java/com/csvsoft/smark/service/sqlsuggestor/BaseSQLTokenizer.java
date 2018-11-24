package com.csvsoft.smark.service.sqlsuggestor;


import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BaseSQLTokenizer implements ISimpleSQLTokenizer {

	protected static Tokenizer tokenizer = new Tokenizer();

	protected static Pattern commentPattern = Pattern.compile("(?s)/\\*.*?\\*/|--[^\\r\\n]*");

	static {
		init();
	}

	protected List<Token> allTokenList;
	protected List<Token> identifierList;

	public BaseSQLTokenizer() {
	}

	public void tokennize(String sql) {
		allTokenList = tokenizer.tokenize(sql);
		identifierList = TokenFilter.filter(allTokenList, ISimpleSQLTokenizer.TOKEN_TYPE_IDENTIFIER);

	}

	protected static void init() {

		int i = 0;
		tokenizer.add("(?s)/\\*.*?\\*/|--[^\\r\\n]*", i++, "comment");
		tokenizer.add("\\s+", i++, ISimpleSQLTokenizer.TOKEN_TYPE_SPACE);
		tokenizer.add("\"[a-zA-Z0-9_\\s]+\"", i++, "quotedIdentifier"); // quoted
																		// identifier
		tokenizer.add("[a-zA-Z_$][\\{\\}a-zA-Z_$0-9\\.\\*]*", i++, ISimpleSQLTokenizer.TOKEN_TYPE_IDENTIFIER); // unquoted
		// identifier
		tokenizer.add("\\(", i++);
		tokenizer.add("\\)", i++);
		tokenizer.add("\\,", i++);
		tokenizer.add("'(?:[^']|'')*'", i++, "stringLiteral"); // sql string
																// literal

		tokenizer.add("[+-/\\*=><%]|(\\|\\|)", i++, "operator"); // operator
	}

	@Override
	public List<Token> getAllTokens() {
		// TODO Auto-generated method stub
		return allTokenList;
	}

	@Override
	public List<Token> getIdentifiers() {

		return identifierList;
	}

	public List<Token> getCommentTokens(String sql) {

		List<Token> tokens = new LinkedList<Token>();
		Matcher m = commentPattern.matcher(sql);
		int i = 0;
		while (m.find()) {
			int start = m.start();
			int end = m.end();
			String tok = m.group();
			tokens.add(new Token("comment", tok, i++, start, end));
		}
		return tokens;
	}

}
