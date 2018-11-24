package com.csvsoft.smark.entity;


import com.csvsoft.smark.entity.IField;

public class DummyField implements IField {

	String fieldName;
	String dataType;
	String desc;
	String misc;
	boolean nullable;

	public DummyField(String fieldName, String dataType, String desc,
			String misc, boolean nullable) {
		super();
		this.fieldName = fieldName;
		this.dataType = dataType;
		this.desc = desc;
		this.misc = misc;
		this.nullable = nullable;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getMisc() {
		return misc;
	}

	public void setMisc(String misc) {
		this.misc = misc;
	}

	public boolean isNullable() {
		return nullable;
	}

	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

}
