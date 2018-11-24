package com.csvsoft.smark.service.sqlsuggestor;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tokenizer {
	private class TokenInfo {
		public final Pattern regex;
		public final int token;
		public String tokenType;

		public TokenInfo(Pattern regex, int token) {
			super();
			this.regex = regex;
			this.token = token;
		}

		public TokenInfo(Pattern regex, int token, String tokenType) {
			super();
			this.regex = regex;
			this.token = token;
			this.tokenType = tokenType;
		}
	}

	private LinkedList<TokenInfo> tokenInfos;

	public Tokenizer() {
		tokenInfos = new LinkedList<TokenInfo>();

	}

	public void add(String regex, int token) {
		tokenInfos.add(new TokenInfo(Pattern.compile("^(" + regex + ")"), token));
	}

	public void add(String regex, int token, String tokenType) {
		tokenInfos.add(new TokenInfo(Pattern.compile("^(" + regex + ")"), token, tokenType));
	}

	public List<Token> tokenize(String str) {
		LinkedList<Token> tokens = new LinkedList<>();
		String s = str;
		int totalIndex = -1;

		int i = 0;
		while (!s.equals("")) {
			boolean match = false;
			for (TokenInfo info : tokenInfos) {
				Matcher m = info.regex.matcher(s);
				if (m.find()) {
					match = true;
					int start = m.start();
					int end = m.end();

					String tok = m.group().trim();
					s = m.replaceFirst("");
					tokens.add(new Token(info.tokenType, tok, i++, totalIndex + start + 1, totalIndex + end + 1));
					totalIndex += (end - start);
					break;
				}
			}

			if (!match) {
				s = s.substring(1);
				totalIndex += 1;
			}

		}
		return tokens;
	}

}
