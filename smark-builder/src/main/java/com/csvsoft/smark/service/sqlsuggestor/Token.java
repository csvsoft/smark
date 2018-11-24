package com.csvsoft.smark.service.sqlsuggestor;

public class Token {
	public final String tokenType;
	public final String sequence;
	public int tokenPos;
	public int start;
	public int end;

	public Token(String tokenType, String sequence, int tokenPos, int start, int end) {
		super();
		this.tokenType = tokenType;
		this.sequence = sequence;
		this.tokenPos = tokenPos;
		this.start = start;
		this.end = end;
	}

	@Override
	public String toString() {

		return this.sequence;
	}

}