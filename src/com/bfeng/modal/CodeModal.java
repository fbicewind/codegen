package com.bfeng.modal;

/**
 * ���ݿ��ֶ���Ҫ��Ϣʵ����
 * 
 * @author fengbin
 *
 */
public class CodeModal {

	private String columnName;
	private String dbColumnType;
	private String javaColumnType;
	private Boolean isPrimaryKey;
	private Boolean isGeneratedValue;

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getDbColumnType() {
		return dbColumnType;
	}

	public void setDbColumnType(String dbColumnType) {
		this.dbColumnType = dbColumnType;
	}

	public String getJavaColumnType() {
		return javaColumnType;
	}

	public void setJavaColumnType(String javaColumnType) {
		this.javaColumnType = javaColumnType;
	}

	public Boolean getIsPrimaryKey() {
		return isPrimaryKey;
	}

	public void setIsPrimaryKey(Boolean isPrimaryKey) {
		this.isPrimaryKey = isPrimaryKey;
	}

	public Boolean getIsGeneratedValue() {
		return isGeneratedValue;
	}

	public void setIsGeneratedValue(Boolean isGeneratedValue) {
		this.isGeneratedValue = isGeneratedValue;
	}

}
