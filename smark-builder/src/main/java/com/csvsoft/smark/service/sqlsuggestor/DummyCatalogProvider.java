package com.csvsoft.smark.service.sqlsuggestor;

import com.csvsoft.smark.entity.DummyField;
import com.csvsoft.smark.entity.FunctionSignature;
import com.csvsoft.smark.entity.FuntionParameter;
import com.csvsoft.smark.entity.IField;
import com.csvsoft.smark.sevice.ICatalogueProvider;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class DummyCatalogProvider implements ICatalogueProvider {

	public List<String> getAllTables() {
		String[] tables = new String[] { "users", "salary", "department" };
		return Arrays.asList(tables);
	}

	@Override
	public void refresh() {

	}

	public IField[] getFields(String tableName) {
		if (tableName.equalsIgnoreCase("users")) {
			return new DummyField[] { new DummyField("first_name", "String", "First Name", null, true),
					new DummyField("last_name", "String", "Last Name", null, true), new DummyField("user_id", "String", "Id", null, false) };
		} else if (tableName.equalsIgnoreCase("salary")) {
			return new DummyField[] { new DummyField("user_id", "String", "First Name", null, true),
					new DummyField("byweekSalary", "float", "Last Name", null, true), new DummyField("level", "String", "level", null, false) };
		} else if (tableName.equalsIgnoreCase("department")) {
			return new DummyField[] { new DummyField("department_id", "String", "First Name", null, true),
					new DummyField("department_name", "float", "Last Name", null, true), new DummyField("level", "String", "level", null, false) };
		}
		return null;

	}

	@Override
	public List<FunctionSignature> getAllFunctions() {
		// TODO Auto-generated method stub
		FunctionSignature toDate = new FunctionSignature();
		toDate.setName("to_date");
		toDate.setReturnType("Date");

		FuntionParameter p = new FuntionParameter();
		p.dataType = "String";
		p.name = "dateText";
		toDate.addParameter(p);

		List<FunctionSignature> funs = new LinkedList<FunctionSignature>();
		funs.add(toDate);
		return funs;
	}

	@Override
	public boolean isTable(String name) {
		// TODO Auto-generated method stub
		return this.getAllTables().contains(name);
	}

	@Override
	public boolean isView(String name) {
		// TODO Auto-generated method stub
		return this.getAllTables().contains(name);
	}

	@Override
	public boolean isFunction(String name) {
		for (FunctionSignature f : getAllFunctions()) {
			if (f.getName().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}

}
