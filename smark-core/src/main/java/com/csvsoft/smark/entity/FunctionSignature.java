package com.csvsoft.smark.entity;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class FunctionSignature implements Serializable {

	public FunctionSignature(String name) {
		this.name = name;
	}
	public FunctionSignature(){}

	private String name;
	private String returnType;

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	private String desc;

	public String getExample() {
		return example;
	}

	public void setExample(String example) {
		this.example = example;
	}

	private String example;

	private List<FuntionParameter> parameterList = new LinkedList<FuntionParameter>();

	public void addParameter(FuntionParameter param) {
		this.parameterList.add(param);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	public List<FuntionParameter> getParameterList() {
		return parameterList;
	}

	public void setParameterList(List<FuntionParameter> parameterList) {
		this.parameterList = parameterList;
	}

	// @Override
	public String getSignatureText() {

		StringBuilder sb = new StringBuilder();

		sb.append(this.returnType).append(" ");
		sb.append(this.name).append("(");
		for (FuntionParameter p : parameterList) {
			sb.append(p.dataType).append(" ").append(p.name);
		}
		sb.append(")");

		return sb.toString();
	}

	public String getSuggestion() {

		StringBuilder sb = new StringBuilder();

		sb.append(this.name).append("(");
		for (FuntionParameter p : parameterList) {
			sb.append(p.name);
		}
		sb.append(")");

		return sb.toString();
	}
}
