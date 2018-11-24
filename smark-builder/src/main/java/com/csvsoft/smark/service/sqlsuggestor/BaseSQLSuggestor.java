package com.csvsoft.smark.service.sqlsuggestor;

import com.csvsoft.smark.entity.FunctionSignature;
import com.csvsoft.smark.entity.IField;
import com.csvsoft.smark.sevice.ICatalogueProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.aceeditor.Suggester;
import org.vaadin.aceeditor.Suggestion;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class BaseSQLSuggestor implements Suggester {

	private static final Logger LOGGER = LoggerFactory.getLogger(BaseSQLSuggestor.class);

	protected ICatalogueProvider cataLogProvider;
	protected ISimpleSQLTokenizer sqlTokenizer;
	protected ISQLKeywordProivers keyworkProvider;

	protected List<String> tableList = new LinkedList<>();
	protected Map<String, String> tableAliasMap = new HashMap<String, String>();
	protected Map<String, String> aliasTablesMap = new HashMap<>();

	protected List<Suggestion> suggs = new LinkedList<Suggestion>();
	protected List<Token> allTokenList;
	protected List<Token> idenfierList;

	protected final static Pattern nonVarNameCharPattern = Pattern.compile("[^a-zA-Z_$0-9%\\*\\{]");

	protected int textOffset = 0;

	class TextToParse {
		public TextToParse(String text, int offset) {
			this.text = text;
			this.offset = offset;
		}

		public String text;
		public int offset;

	}

	protected static class MySuggestion extends Suggestion {
		private String insertThis;

		MySuggestion(String displayText, String descriptionText, String suggestionText) {
			super(displayText, descriptionText, suggestionText);
			insertThis = suggestionText;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(this.getDisplayText()).append("  Desc:").append(this.getDescriptionText()).append("  suggestText:").append(this.getSuggestionText());
			return sb.toString();
		}

	}

	private List<Token>  removeConsectiveSpaces(List<Token> tokenList){
		List<Token> reDucedTokenList = new ArrayList<>(tokenList.size());
		int i=0;
		while(i<tokenList.size()){
			Token token= tokenList.get(i);
			if(ISimpleSQLTokenizer.TOKEN_TYPE_SPACE.equals(token.tokenType)){
				reDucedTokenList.add(token);
				i++;
				while(i<tokenList.size()){
					Token nextToken =tokenList.get(i);
					if(!ISimpleSQLTokenizer.TOKEN_TYPE_SPACE.equals(nextToken.tokenType)){
						reDucedTokenList.add(nextToken);
						break;
					}
                   i++;
				}
			}else{
				reDucedTokenList.add(token);
			}
			i++;
		}
		return reDucedTokenList;
	}

	private List<Suggestion> myGetSuggestions(String text, int cursor) {

		if (cursor <= 1) {
			return suggs;
		}

		int cursorIndex = cursor - 1;

		sqlTokenizer.tokennize(text);
		idenfierList = sqlTokenizer.getIdentifiers();

		// Filter our keywords get table/view ,column, function references
		List<Token> entityList = new LinkedList<>();
		for (Token t : idenfierList) {
			if (!this.keyworkProvider.isKeyword(t.sequence)) {
				entityList.add(t);
			}
		}

		// Get table/view aliases, if a fragment matches table as alias, or
		// table alias

		allTokenList = sqlTokenizer.getAllTokens();
		allTokenList = allTokenList.stream().filter(t-> !"comment".equals(t.tokenType)).collect(Collectors.toList());
        allTokenList = removeConsectiveSpaces(allTokenList);
		for (Token t : entityList) {
			String name = t.sequence;
			if (cataLogProvider.isTable(t.sequence) || cataLogProvider.isView(name)) {
				tableList.add(t.sequence);
				// if next token is a space or as and next next token is an
				// entity, it is alias.
				int tokenPos = t.tokenPos;
				boolean foundAlias = false;
				if (allTokenList.size() > tokenPos + 2) {
					Token nextToken = allTokenList.get(tokenPos + 1);
					Token nextNextToken = allTokenList.get(tokenPos + 2);
					if (nextToken.tokenType == ISimpleSQLTokenizer.TOKEN_TYPE_SPACE
							&& ISimpleSQLTokenizer.TOKEN_TYPE_IDENTIFIER.equalsIgnoreCase(nextNextToken.tokenType)
							&& !"AS".equalsIgnoreCase(nextNextToken.sequence)) {
						foundAlias = true;
						tableAliasMap.put(name, nextNextToken.sequence);
						aliasTablesMap.put(nextNextToken.sequence, name);
					}
				}
				if (!foundAlias && allTokenList.size() > tokenPos + 4) {
					Token nextToken = allTokenList.get(tokenPos + 1);
					Token asToken = allTokenList.get(tokenPos + 2);
					Token aliasToken = allTokenList.get(tokenPos + 4);

					if (nextToken.tokenType == ISimpleSQLTokenizer.TOKEN_TYPE_SPACE && "AS".equalsIgnoreCase(asToken.sequence)
							&& ISimpleSQLTokenizer.TOKEN_TYPE_IDENTIFIER.equalsIgnoreCase(aliasToken.tokenType)) {
						tableAliasMap.put(name, asToken.sequence);
						aliasTablesMap.put(aliasToken.sequence, name);
					}
				}
			}
		}

		char prevChar = text.charAt(cursorIndex);

		if (prevChar == '.') {
			populateSuggs4Dot(cursor);
		} else if (nonVarNameCharPattern.matcher(String.valueOf(prevChar)).matches()) {
			populateSuggs4NonVarName();
		} else {
			populateSuggs4RegChar(cursor);
		}

		return suggs;
	}

	protected void populateSuggs4RegChar(int cursor) {
		Token token = getCursorToken(cursor);
		if (token == null) {
			return;
		}
		String tokenStr= token.sequence;
		if(tokenStr.contains("$")){
			processVar(cursor,token);
		}else {
			populateAllSuggesions(token.sequence);
		}

	}

	protected Token getCursorToken(int cursor) {
		Token token = null;
		int cursorIndex = cursor - 1;
		for (Token t : allTokenList) {
			if (cursorIndex >= t.start && cursorIndex < t.end) {
				token = t;
				break;
			}
		}
		return token;
	}

	protected void populateSuggs4Dot(int cursor) {

		int cursorIndex = cursor - 1;
		Suggestion sugg = null;

		// find the token before the dot
		Token token = getCursorToken(cursor);
		if (token != null) {
			String alias = token.sequence;
			if (alias.endsWith(".")) {
				alias = alias.substring(0, alias.length() - 1);
			}
			String table = aliasTablesMap.get(alias);

			if (table != null) {
				LOGGER.info("populating fields for table:"+ table);
				addFieldAllSugg(table,alias);
				addFieldSuggs(table, null);
			}else if (alias.contains("$")){
				processVar(cursor,token);
				//LOGGER.info("No table found for alias:"+alias );
			}
		}
	}

	protected void processVar(int cursor,Token token){

	}

	protected void addFieldSuggs(String table, String startWith) {
		Suggestion sugg = null;
		IField[] fields = cataLogProvider.getFields(table);
		for (IField f : fields) {
			if (startWith == null || f.getFieldName().toUpperCase().startsWith(startWith.toUpperCase())) {
				sugg = convertField2Suggestion(table, f);
				suggs.add(sugg);
			}
		}
	}

	protected void addFieldAllSugg(String table, String alias) {

		IField[] fields = cataLogProvider.getFields(table);
		StringBuilder sb = new StringBuilder();

		//	}
		int i=0;
		for (IField f : fields) {
			if(i!=0){
				sb.append(",");
				sb.append(alias);
				sb.append(".").append(f.getFieldName()).append("\n");
			}else{
				sb.append(f.getFieldName()).append("\n");
			}

			i++;
		}
		MySuggestion sugg = new MySuggestion("*", "Column:" + table + ".*" , sb.toString());
		this.suggs.add(sugg);

	}
	/**
	 * Default implementation for case when the previous char is a space. It populates the columns of tables being referenced if there is any, tables, then functions,keywords,
	 */
	protected void populateSuggs4NonVarName() {
		populateAllSuggesions(null);
	}

	protected void populateAllSuggesions(String startWith) {
		// Add columns
		for (String table : tableList) {
			addFieldSuggs(table, startWith);
		}

		addTables2Suggs(cataLogProvider.getAllTables(), startWith);

		// add functions
		addFunction2Suggs(startWith);
		addKeywords2Suggs(startWith);
	}

	private Suggestion convertTable2Suggestion(String table) {
		return new MySuggestion(table, "Table", table);
	}

	private Suggestion convertField2Suggestion(String table, IField field) {
		return new MySuggestion(field.getFieldName(), "Column:" + table + "." + field.getFieldName() + " "+field.getDataType(), field.getFieldName());
	}

	private Suggestion convertFunction2Suggestion(FunctionSignature func) {
		return new MySuggestion(func.getName(), "Function:" + func.getSignatureText(), func.getSuggestion());
	}

	private Suggestion convertKeyWord2Suggestion(String keyWord) {
		return new MySuggestion(keyWord, "Keyword", keyWord);
	}

	protected void addKeywords2Suggs(String startWith) {
		for (String str : keyworkProvider.getKeyWords()) {
			if (startWith == null || str.toUpperCase().startsWith(startWith.toUpperCase())) {
				suggs.add(convertKeyWord2Suggestion(str));
			}
		}
	}

	protected void addTables2Suggs(List<String> tableList, String startWith) {
		for (String str : tableList) {
			if (startWith == null || str.toUpperCase().startsWith(startWith.toUpperCase())) {
				suggs.add(convertTable2Suggestion(str));
			}
		}
	}

	protected void addFunction2Suggs(String startWith) {

		for (FunctionSignature func : cataLogProvider.getAllFunctions()) {
			if (startWith == null || func.getName().toUpperCase().startsWith(startWith.toUpperCase())) {
				suggs.add(convertFunction2Suggestion(func));
			}

		}

	}

	@Override
	public List<Suggestion> getSuggestions(String text, int cursor) {
		cleanup();
		TextToParse textToParse = getTextToParse(text, cursor);
		textOffset = textToParse.offset;
		suggs = myGetSuggestions(textToParse.text, cursor - textToParse.offset);
		return suggs;
	}

	private void cleanup() {
		tableList.clear();
		tableAliasMap.clear();
		aliasTablesMap.clear();
		suggs.clear();
	}

	private boolean isInComment(List<Token> commentTokens, int pos) {
		boolean isIn = false;
		for (Token t : commentTokens) {
			if (pos > t.start && pos < t.end) {
				isIn = true;
				break;
			}

		}
		return isIn;
	}

	private TextToParse getTextToParse(String text, int cursor) {

		// StringUtils.split(text,"\n")

		List<Token> commentTokens = sqlTokenizer.getCommentTokens(text);

		// find the start
		int start = 0;
		boolean findStart = false;
		for (int i = cursor - 1; i >= 0; i--) {
			char c = text.charAt(i);
			if (';' == c) {
				// make sure it is not in the comment;
				if (!isInComment(commentTokens, i)) {
					start = i;
					findStart = true;
					break;
				}

			}
		}

		// find the end
		int end = 0;
		boolean findEnd = false;
		for (int i = cursor; i < text.length(); i++) {
			char c = text.charAt(i);
			if (';' == c) {
				// make sure it is not in the comment;
				if (!isInComment(commentTokens, i)) {
					end = i;
					findEnd = true;
					break;
				}

			}
		}

		start = findStart ? (start + 1) : 0;
		end = findEnd ? end : text.length();
		return new TextToParse(text.substring(start, end), start);

	}

	@Override
	public String applySuggestion(Suggestion sugg, String text, int cursor) {
		// sugg is one of the objects returned by getSuggestions -> it's a
		// MySuggestion.
		String ins = ((MySuggestion) sugg).insertThis;

		int insertIndex = cursor;
		char prevChar = text.charAt(cursor - 1);

		if ('.' == prevChar || nonVarNameCharPattern.matcher(String.valueOf(prevChar)).matches()) {
			insertIndex = cursor;
		} else {
			Token token = this.getCursorToken(cursor - textOffset);
			if (token != null) {
				insertIndex = cursor - token.sequence.length();
			}
		}
		String s1 = text.substring(0, insertIndex) + ins + text.substring(cursor);
		return s1;
	}

	public void setCataLogProvider(ICatalogueProvider cataLogProvider) {
		this.cataLogProvider = cataLogProvider;
	}

	public void setSqlTokenizer(ISimpleSQLTokenizer sqlTokenizer) {
		this.sqlTokenizer = sqlTokenizer;
	}

	public void setKeyworkProvider(ISQLKeywordProivers keyworkProvider) {
		this.keyworkProvider = keyworkProvider;
	}

}