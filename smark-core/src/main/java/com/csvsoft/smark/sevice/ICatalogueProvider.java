package com.csvsoft.smark.sevice;

import com.csvsoft.smark.entity.FunctionSignature;
import com.csvsoft.smark.entity.IField;

import java.util.List;

public interface ICatalogueProvider {

	public List<String> getAllTables();

	public IField[] getFields(String tableName);

	public List<FunctionSignature> getAllFunctions();

	public boolean isTable(String name);

	public boolean isView(String name);

	public boolean isFunction(String name);

	public void refresh();

}
