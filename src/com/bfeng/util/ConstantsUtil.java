package com.bfeng.util;

public class ConstantsUtil {

	public static final String COLUMN_NAME = "COLUMN_NAME";
	public static final String COLUMN_TYPE = "TYPE_NAME";
	public static final String COLUMN_IS_NULLABLE = "IS_NULLABLE";
	public static final String COLUMN_IS_AUTOINCREMENT = "IS_AUTOINCREMENT";

	public static final String PACKAGE_STR = "package ";

	public static final String IMPORT_LOMBOK_DATA = "import lombok.Data;\r\n";
	public static final String IMPORT_ENTITY = "import javax.persistence.Entity;\r\n";
	public static final String IMPORT_GENERATED_VALUE = "import javax.persistence.GeneratedValue;\r\n";
	public static final String IMPORT_ID = "import javax.persistence.Id;\r\n";
	public static final String IMPORT_SERIALIZABLE = "import java.io.Serializable;\r\n";
	public static final String IMPORT_COLUMN = "import javax.persistence.Column;\r\n";
	public static final String IMPORT_TABLE = "import javax.persistence.Table;\r\n";
	public static final String IMPORT_DATE = "import java.util.Date;\r\n";
	public static final String IMPORT_TEMPORAL = "import javax.persistence.Temporal;\r\n";
	public static final String IMPORT_TEMPORAL_TYPE = "import javax.persistence.TemporalType;\r\n";
	public static final String IMPORT_GENERICGENERATOR = "import org.hibernate.annotations.GenericGenerator;\r\n";
	public static final String IMPORT_BIGDECIMAL = "import java.math.BigDecimal;\r\n";
	public static final String IMPORT_ENTITY_STR = "import ?;\r\n";
	public static final String IMPORT_JPAREPOSITORY = "import org.springframework.data.jpa.repository.JpaRepository;\r\n";

	public static final String ANNOTATION_ENTITY = "@Entity\r\n";
	public static final String ANNOTATION_DATA = "@Data\r\n";
	public static final String ANNOTATION_COLUMN = "\t@Column(name = \"?\")\r\n";
	public static final String ANNOTATION_TABLE = "@Table(name = \"?\")\r\n";
	public static final String ANNOTATION_ID = "\t@Id\r\n";
	public static final String ANNOTATION_GENERATEDVALUE = "\t@GeneratedValue\r\n";
	public static final String ANNOTATION_GENERATEDVALUE_UUID = "\t@GeneratedValue(generator = \"uuid\")\r\n";
	public static final String ANNOTATION_GENERATOR_UUID = "\t@GenericGenerator(name = \"uuid\", strategy = \"uuid\")\r\n";
	public static final String ANNOTATION_TEMPORALTYPE_TIMESTAMP = "\t@Temporal(TemporalType.TIMESTAMP)\r\n";
	public static final String ANNOTATION_TEMPORALTYPE_TIME = "\t@Temporal(TemporalType.TIME)\r\n";
	public static final String ANNOTATION_TEMPORALTYPE_DATE = "\t@Temporal(TemporalType.DATE)\r\n";

	public static final String SEMICOLON_STR = ";";

	public static final String NEW_LINE_TAG = "\r\n";
	public static final String TAB_TAG = "\t";

	public static final String PUBLIC_CLASS_STR = "public class ? implements Serializable {\r\n";
	public static final String SERIAL_VERSION_UID = "\tprivate static final long serialVersionUID = 1L;\r\n";
	// *代表字段类型, ?代表帕斯卡命名, !代表驼峰命名
	public static final String GETTER_STR = "\tpublic * get?() {\r\n\t\treturn !;\r\n\t}\r\n";
	public static final String SETTER_STR = "\tpublic void set?(* !) {\r\n\t\tthis.! = !;\r\n\t}\r\n";

	public static final String POSTFIX_JAVA = ".java";
	public static final String POSTFIX_DAO = "Dao.java";
	public static final String POSTFIX_SERVICE = "Service.java";
	public static final String POSTFIX_SERVICEIMPL = "ServiceImpl.java";
	
	public static final String INTERFACE_DAO = "public interface ?Dao extends JpaRepository<?, *> {\r\n";
	public static final String CLASS_CLOSE_TAG = "}";

}
