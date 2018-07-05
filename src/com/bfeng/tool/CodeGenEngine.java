package com.bfeng.tool;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.TreeSet;

import javax.persistence.Id;

import com.bfeng.modal.CodeModal;
import com.bfeng.util.ColumnTypeEnum;
import com.bfeng.util.ConstantsUtil;

/**
 * 代码生成类
 * 
 * @author fengbin
 *
 */
public class CodeGenEngine {

	public static String configPath = "codeinfo";
	// jdbc相关
	private static Connection connection;
	private static String driverClass;
	private static String dbUrl;
	private static String username;
	private static String password;
	// 包路径相关
	private static String rootPath = System.getProperty("user.dir");
	private static String entityPath = rootPath + "/src/entity";
	private static String daoPath = rootPath + "/src/dao";
	private static String replaceStr = "";
	private static boolean isLombok = false;

	/**
	 * 自动生成代码主方法
	 * 
	 * @param tableNames
	 *            数据库表名
	 * @param entityNames
	 *            实体名，精确到包
	 * @param entityCode
	 *            是否生成实体类
	 * @param daoCode
	 *            是否生成dao
	 */
	public static void genAllCode(String[] tableNames, String[] entityNames, boolean entityCode, boolean daoCode) {
		if (entityCode && (tableNames == null || tableNames.length == 0)) {
			System.err.println("Generate code failed. Caused by tableNames is null when generated the entity.");
			return;
		}
		if (!entityCode && (entityNames == null || entityNames.length == 0)) {
			System.err.println("Generate code failed. Caused by entityNames is null when generated the dao.");
			return;
		}
		initPathInfo(entityCode, daoCode);
		if (entityCode) {
			genAllEntityCode(tableNames, daoCode);
			return;
		}
		if (daoCode && (entityNames == null || entityNames.length == 0)) {
			System.err.println("Generate code failed. Caused by entityNames is null when generated the dao.");
			return;
		}
		for (String className : entityNames) {
			try {
				Class<?> clazz = Class.forName(className);
				if (daoCode)
					genDaoCode(clazz, null, null);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 生成实体类方法
	 * 
	 * @param tableNames
	 * @param daoCode
	 */
	private static void genAllEntityCode(String[] tableNames, boolean daoCode) {
		boolean initFlag = initConnectionInfos();
		if (!initFlag)
			return;
		boolean getConnectionFlag = getConnection();
		if (!getConnectionFlag)
			return;
		try {
			DatabaseMetaData dbMetaData = connection.getMetaData();
			for (String tableName : tableNames)
				genEntityCode(dbMetaData, tableName, daoCode);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeConnection();
		}
	}

	/**
	 * 生成dao方法
	 * 
	 * @param clazz
	 * @param longEntityName
	 * @param primaryKeyType
	 */
	private static void genDaoCode(Class<?> clazz, String longEntityName, String primaryKeyType) {
		if (clazz == null && (longEntityName == null || primaryKeyType == null)) {
			System.err.println("Generate dao code failed. Caused by param is null.");
			return;
		}
		String entityName = clazz == null ? longEntityName : clazz.getName();
		String subEntityName = substringLastDot(entityName);
		String packageStr = ConstantsUtil.PACKAGE_STR.concat(daoPath.replace(replaceStr, "").replaceAll("/", "\\.")).concat(ConstantsUtil.SEMICOLON_STR)
				.concat(ConstantsUtil.NEW_LINE_TAG).concat(ConstantsUtil.NEW_LINE_TAG);
		TreeSet<String> importSet = new TreeSet<>();
		importSet.add(ConstantsUtil.IMPORT_ENTITY_STR.replace("?", entityName));
		importSet.add(ConstantsUtil.IMPORT_JPAREPOSITORY);
		List<String> mainList = new ArrayList<>();
		mainList.add(ConstantsUtil.INTERFACE_DAO.replaceAll("\\?", subEntityName).replace("*", clazz == null ? primaryKeyType : getPrimaryKeyType(clazz)));
		mainList.add(ConstantsUtil.NEW_LINE_TAG);
		mainList.add(ConstantsUtil.CLASS_CLOSE_TAG);
		genFile(getText(packageStr, importSet, mainList), daoPath, subEntityName.concat(ConstantsUtil.POSTFIX_DAO));
	}

	/**
	 * 截取类名
	 * 
	 * @param str
	 * @return
	 */
	private static String substringLastDot(String str) {
		String[] strArr = str.split("\\.");
		return strArr.length > 1 ? strArr[strArr.length - 1] : str;
	}

	/**
	 * 获取主键类型
	 * 
	 * @param clazz
	 * @return
	 */
	private static String getPrimaryKeyType(Class<?> clazz) {
		String str = "Integer";
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			Id id = field.getDeclaredAnnotation(Id.class);
			if (id != null) {
				str = substringLastDot(field.getGenericType().getTypeName());
				break;
			}
		}
		return str;
	}

	/**
	 * 初始化jdbc信息
	 * 
	 * @return
	 */
	private static boolean initConnectionInfos() {
		boolean flag = true;
		try {
			ResourceBundle resource = ResourceBundle.getBundle(configPath);
			driverClass = resource.getString("jdbc.driver");
			dbUrl = resource.getString("jdbc.url");
			username = resource.getString("jdbc.username");
			password = resource.getString("jdbc.password");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * jdbc连接
	 * 
	 * @return
	 */
	private static boolean getConnection() {
		boolean flag = true;
		try {
			Class.forName(driverClass);
			connection = DriverManager.getConnection(dbUrl, username, password);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			flag = false;
		} catch (SQLException e) {
			e.printStackTrace();
			flag = false;
		}
		return flag;
	}

	/**
	 * 关闭jdbc连接
	 */
	private static void closeConnection() {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 初始化包路径信息
	 */
	private static void initPathInfo(boolean entityCode, boolean daoCode) {
		ResourceBundle resource = ResourceBundle.getBundle(configPath);
		if (entityCode) {
			try {
				entityPath = rootPath + resource.getString("code.entity.path");
			} catch (Exception e) {
				System.err.println("Warning, Entity path is null, using default path!");
			}
		}
		if (daoCode) {
			try {
				daoPath = rootPath + resource.getString("code.dao.path");
			} catch (Exception e) {
				System.err.println("Warning, Dao path is null, using default path!");
			}
		}
		try {
			replaceStr = rootPath + resource.getString("code.replace.str");
		} catch (Exception e) {
			System.err.println("Warning, ReplaceStr path is null, using default path!");
		}
		String lombokStr = resource.getString("code.islombok");
		isLombok = "true".equals(lombokStr);
	}

	/**
	 * 生成文件
	 * 
	 * @param text
	 *            文件内容
	 * @param filePath
	 *            文件路径
	 * @param fileName
	 *            文件名(带后缀)
	 */
	private static void genFile(String text, String filePath, String fileName) {
		File dirs = new File(filePath);
		if (!dirs.exists() || !dirs.isDirectory())
			dirs.mkdirs();
		File file = new File(filePath.concat("/").concat(fileName));
		try {
			if (!file.exists())
				file.createNewFile();
			FileWriter fw = new FileWriter(file);
			fw.write(text);
			fw.close();
			System.out.println("Generate file successfully. ".concat(filePath.concat("/").concat(fileName)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 生成实体类方法
	 * 
	 * @param dbMetaData
	 * @param tableName
	 * @param daoCode
	 * @throws SQLException
	 */
	private static void genEntityCode(DatabaseMetaData dbMetaData, String tableName, boolean daoCode) throws SQLException {
		ResultSet rs = dbMetaData.getColumns(null, null, tableName, null);
		ResultSet pkrs = dbMetaData.getPrimaryKeys(null, null, tableName);
		String primaryKey = "";
		while (pkrs.next()) {
			primaryKey = pkrs.getString(ConstantsUtil.COLUMN_NAME);
		}
		List<CodeModal> modalList = new ArrayList<>();
		while (rs.next()) {
			CodeModal codeModal = new CodeModal();
			codeModal.setColumnName(rs.getString(ConstantsUtil.COLUMN_NAME));
			codeModal.setDbColumnType(rs.getString(ConstantsUtil.COLUMN_TYPE));
			codeModal.setIsPrimaryKey(primaryKey.equals(rs.getString(ConstantsUtil.COLUMN_NAME)));
			codeModal.setIsGeneratedValue(rs.getBoolean(ConstantsUtil.COLUMN_IS_AUTOINCREMENT));
			codeModal.setJavaColumnType(ColumnTypeEnum.getType(codeModal.getDbColumnType()));
			modalList.add(codeModal);
		}
		String pascalEntityName = fmtToPascalName(tableName);
		genFile(fmtEntityText(modalList, tableName), entityPath, pascalEntityName.concat(ConstantsUtil.POSTFIX_JAVA));
		rs.close();
		pkrs.close();
		genDaoByTable(daoCode, modalList, pascalEntityName);
	}

	/**
	 * 通过table来生成dao
	 * 
	 * @param daoCode
	 * @param modalList
	 * @param entityName
	 */
	private static void genDaoByTable(boolean daoCode, List<CodeModal> modalList, String entityName) {
		if (daoCode) {
			String primaryKeyType = "Integer";
			for (CodeModal cm : modalList) {
				if (cm.getIsPrimaryKey()) {
					primaryKeyType = cm.getJavaColumnType();
					break;
				}
			}
			String longEntityName = entityPath.replace(replaceStr, "").replaceAll("/", "\\.").concat(".").concat(entityName);
			genDaoCode(null, longEntityName, primaryKeyType);
		}
	}

	private static String fmtEntityText(List<CodeModal> modalList, String tableName) {
		TreeSet<String> importSet = new TreeSet<>();
		List<String> mainList = new ArrayList<>();
		fmtFixedColumnInfo(importSet, mainList, tableName);
		fmtColumnInfo(importSet, mainList, modalList);
		return getText(getEntityPackage(), importSet, mainList);
	}

	private static String getText(String packageStr, TreeSet<String> importSet, List<String> mainList) {
		StringBuilder sb = new StringBuilder(packageStr);
		importSet.forEach((str) -> {
			sb.append(str);
		});
		sb.append(ConstantsUtil.NEW_LINE_TAG);
		mainList.forEach((str) -> {
			sb.append(str);
		});
		return sb.toString();
	}

	private static void fmtFixedColumnInfo(TreeSet<String> importSet, List<String> mainList, String tableName) {
		if (isLombok) {
			importSet.add(ConstantsUtil.IMPORT_LOMBOK_DATA);
			mainList.add(ConstantsUtil.ANNOTATION_DATA);
		}
		importSet.add(ConstantsUtil.IMPORT_ENTITY);
		importSet.add(ConstantsUtil.IMPORT_ID);
		importSet.add(ConstantsUtil.IMPORT_SERIALIZABLE);
		importSet.add(ConstantsUtil.IMPORT_TABLE);
		importSet.add(ConstantsUtil.IMPORT_COLUMN);

		mainList.add(ConstantsUtil.ANNOTATION_ENTITY);
		mainList.add(ConstantsUtil.ANNOTATION_TABLE.replace("?", tableName));
		mainList.add(ConstantsUtil.PUBLIC_CLASS_STR.replace("?", fmtToPascalName(tableName)));
		mainList.add(ConstantsUtil.NEW_LINE_TAG);
		mainList.add(ConstantsUtil.SERIAL_VERSION_UID);
		mainList.add(ConstantsUtil.NEW_LINE_TAG);
	}

	private static void fmtColumnInfo(TreeSet<String> importSet, List<String> mainList, List<CodeModal> modalList) {
		for (CodeModal modal : modalList) {
			if (modal.getIsPrimaryKey())
				mainList.add(ConstantsUtil.ANNOTATION_ID);
			if (modal.getIsGeneratedValue()) {
				importSet.add(ConstantsUtil.IMPORT_GENERATED_VALUE);
				mainList.add(ConstantsUtil.ANNOTATION_GENERATEDVALUE);
			}
			if ("Date".equalsIgnoreCase(modal.getJavaColumnType())) {
				importSet.add(ConstantsUtil.IMPORT_DATE);
				importSet.add(ConstantsUtil.IMPORT_TEMPORAL);
				importSet.add(ConstantsUtil.IMPORT_TEMPORAL_TYPE);
				if ("DATE".equalsIgnoreCase(modal.getDbColumnType())) {
					mainList.add(ConstantsUtil.ANNOTATION_TEMPORALTYPE_DATE);
				} else if ("TIME".equalsIgnoreCase(modal.getDbColumnType())) {
					mainList.add(ConstantsUtil.ANNOTATION_TEMPORALTYPE_TIME);
				} else {
					mainList.add(ConstantsUtil.ANNOTATION_TEMPORALTYPE_TIMESTAMP);
				}
			}
			if ("BigDecimal".equalsIgnoreCase(modal.getJavaColumnType())) {
				importSet.add(ConstantsUtil.IMPORT_BIGDECIMAL);
			}
			mainList.add(ConstantsUtil.ANNOTATION_COLUMN.replace("?", modal.getColumnName()));
			String columnTypeLong = ColumnTypeEnum.getTypeWithAll(modal.getDbColumnType());
			if (columnTypeLong == null) {
				System.err.println("Error, Column " + modal.getColumnName() + " can't be changed!");
				continue;
			}
			mainList.add(columnTypeLong.replace("?", fmtToCamelName(modal.getColumnName())));
			mainList.add(ConstantsUtil.NEW_LINE_TAG);
		}
		if (!isLombok) {
			for (CodeModal modal : modalList) {
				String camelName = fmtToCamelName(modal.getColumnName());
				String pascalName = fmtToPascalName(modal.getColumnName());
				String columnType = modal.getJavaColumnType();
				mainList.add(ConstantsUtil.GETTER_STR.replace("*", columnType).replace("?", pascalName).replace("!", camelName));
				mainList.add(ConstantsUtil.SETTER_STR.replace("*", columnType).replace("?", pascalName).replaceAll("!", camelName));
			}
		}
		mainList.add("}");
	}

	private static String getEntityPackage() {
		StringBuilder sb = new StringBuilder();
		String entityPackage = entityPath.replace(replaceStr, "").replaceAll("/", "\\.");
		sb.append(ConstantsUtil.PACKAGE_STR).append(entityPackage).append(ConstantsUtil.SEMICOLON_STR).append(ConstantsUtil.NEW_LINE_TAG)
				.append(ConstantsUtil.NEW_LINE_TAG);
		return sb.toString();
	}

	/**
	 * 驼峰命名法:除第一个但此外,其余每个单词首字母大写
	 * 
	 * @param str
	 * @return
	 */
	private static String fmtToCamelName(String str) {
		String[] strArr = str.split("_");
		String camelName = strArr[0].toLowerCase();
		if (strArr.length > 1) {
			for (int i = 1; i < strArr.length; i++) {
				if (strArr[i].length() == 0)
					continue;
				String prevStr = strArr[i].substring(0, 1);
				String nextStr = strArr[i].substring(1);
				camelName += prevStr.toUpperCase() + nextStr.toLowerCase();
			}
		}
		return camelName;
	}

	/**
	 * 帕斯卡命名法:每个单词首字母都大写
	 * 
	 * @param str
	 * @return
	 */
	private static String fmtToPascalName(String str) {
		String[] strArr = str.split("_");
		String camelName = "";
		for (int i = 0; i < strArr.length; i++) {
			if (strArr[i].length() == 0)
				continue;
			String prevStr = strArr[i].substring(0, 1);
			String nextStr = strArr[i].substring(1);
			camelName += prevStr.toUpperCase() + nextStr.toLowerCase();
		}
		return camelName;
	}

	public static void main(String[] args) throws SQLException {
		genAllCode(new String[] { "demo_a_test" }, null, true, true);
	}
}
