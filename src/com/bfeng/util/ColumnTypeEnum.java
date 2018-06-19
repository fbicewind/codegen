package com.bfeng.util;

import java.util.EnumSet;

/**
 * 数据库字段与java字段转换枚举类
 * @author fengbin
 *
 */
public enum ColumnTypeEnum {

	CHAR_TYPE("CHAR", "String"), 
	VARCHAR_TYPE("VARCHAR", "String"), 
	LONGVARCHAR_TYPE("LONGVARCHAR", "String"),
	TEXT_TYPE("TEXT", "String"),
	NUMERIC_TYPE("NUMERIC", "BigDecimal"),
	DECIMAL_TYPE("DECIMAL", "BigDecimal"),
	BIT_TYPE("BIT", "Boolean"),
	TINYINT_TYPE("TINYINT", "Integer"),
	SMALLINT_TYPE("SMALLINT", "Integer"),
	MEDIUMINT_TYPE("MEDIUMINT", "Integer"),
	INT_TYPE("INT", "Integer"),
	INTEGER_TYPE("INTEGER", "Integer"),
	BIGINT_TYPE("BIGINT", "Long"),
	REAL_TYPE("REAL", "Float"),
	FLOAT("FLOAT", "Double"),
	DOUBLE_TYPE("DOUBLE", "Double"),
	BINARY("BINARY", "byte[]"),
	VARBINARY_TYPE("VARBINARY", "byte[]"),
	LONGVARBINARY_TYPE("LONGVARBINARY", "byte[]"),
	DATE_TYPE("DATE", "Date"),
	DATETIME_TYPE("DATETIME", "Date"),
	TIME_TYPE("TIME", "Date"),
	TIMESTAMP_TYPE("TIMESTAMP", "Date");

	private String dbType;
	private String javaType;

	public static String getTypeWithAll(String columnType) {
		EnumSet<ColumnTypeEnum> typeSet = EnumSet.allOf(ColumnTypeEnum.class);
		for (ColumnTypeEnum type : typeSet) {
			if (type.getDbType().equalsIgnoreCase(columnType))
				return "\tprivate ".concat(type.javaType).concat(" ?;\r\n");
		}
		return null;
	}
	
	public static String getType(String columnType) {
		EnumSet<ColumnTypeEnum> typeSet = EnumSet.allOf(ColumnTypeEnum.class);
		for (ColumnTypeEnum type : typeSet) {
			if (type.getDbType().equalsIgnoreCase(columnType))
				return type.javaType;
		}
		return null;
	}

	private ColumnTypeEnum(String dbType, String javaType) {
		this.dbType = dbType;
		this.javaType = javaType;
	}

	public String getDbType() {
		return dbType;
	}

	public void setDbType(String dbType) {
		this.dbType = dbType;
	}

	public String getJavaType() {
		return javaType;
	}

	public void setJavaType(String javaType) {
		this.javaType = javaType;
	}

}
