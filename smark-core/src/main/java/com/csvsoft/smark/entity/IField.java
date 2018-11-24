package com.csvsoft.smark.entity;

import java.io.Serializable;

public interface IField extends Serializable{

	public String getFieldName();

	public String getDataType();

	public boolean isNullable();

	public String getDesc();

	public String getMisc();

}
