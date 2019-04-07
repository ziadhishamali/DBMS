package eg.edu.alexu.csd.oop.db.cs61;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eg.edu.alexu.csd.oop.db.Command;

public class Validation {

	// Create Database REGEX
	private static final String regex1 = "(CREATE)([\\s]*) {1}(DATABASE)([\\s]*) {1}([\\w\\d_0-9]*)([\\s]*?)";

	// Select Database REGEX
	private static final String regex2 = "(USE)([\\s]*)([\\w\\d_0-9]*)([\\s]*?)";

	// Create Table REGEX
	private static final String regex3 = "(CREATE)([\\s]*) {1}(TABLE)([\\s]*) {1}([\\w\\d_0-9]*)([\\s]*)([\\(])([\\w\\d_\\s\\w\\,]+)([\\)])";
	/* CREATE TABLE table_name1(column_name1 varchar, column_name2 int, column_name varchar)*/

	// Insert In Table REGEX
	private static final String regex4 = "(INSERT)([\\s]*) {1}(INTO)([\\s]*) {1}([\\w\\d_0-9]*)([\\s]*?)([\\(]){0,1}([\\w\\d_0-9,?[\\s]?]*){0,1}([\\)]){0,1}([\\s]*){0,1}(VALUES)([\\s]*)([\\(])([[']?[\\-]?\\w\\d_0-9[']?,?[\\s]?]*)([\\)])";
	/* INSERT INTO TABLE_NAME VALUES (value1,value2,value3,...valueN)*/
	/* INSERT INTO TABLE_NAME (column1, column2, column3,...columnN) VALUES (value1,value2, value3,...valueN)*/

	// Select From Table REGEX
	private static final String regex5 = "(SELECT)([\\s]*)([\\*]) {1}([\\s]*)(FROM)([\\s]*) {1}([\\w\\d_0-9]*)([\\s]*?)((WHERE)([\\s]*)([a-zA-Z_0-9\\s\\W\\s[\\-]?0-9]*)){0,1}";
	/* SELECT * FROM table_name where id < 10*/

	// Delete From Table REGEX
	private static final String regex6 = "(DELETE)([\\s]*) {1}(FROM)([\\s]*) {1}([\\w\\d_0-9]*)([\\s]*)((WHERE)([\\s]?)([a-zA-Z_0-9\\s\\W\\s[\\-]?0-9]*)){0,1}";

	//private static final String regex6 = "(DELETE)([\\s]*) {1}(FROM)([\\s]*) {1}([\\w\\d_0-9]*)([\\s]?)((WHERE)([\\s]*)([a-zA-Z_0-9\\s\\W\\s0-9]*)){0,1}";
	/*DELETE FROM table_name WHERE [condition]*/

	// Update Table REGEX
	private static final String regex7 = "(UPDATE)([\\s]*)([\\w\\d_0-9]*)([\\s]*)(SET)([\\s]*)([a-zA-Z_0-9[\\s]*?=[\\']?[\\-]?0-9[\\']?[\\s]*?[,]?]*)([\\s]*) {0,1}((WHERE)([\\s]?)([a-zA-Z_0-9\\s\\W\\s[\\-]?0-9]*)){0,1}";
	/*UPDATE table_name SET column1 = value1, column2 = value2...., columnN = value WHERE [condition];*/
	
	// Drop Database REGEX
	private static final String regex8 = "(DROP)([\\s]*) {1}(DATABASE)([\\s]*) {1}([\\w\\d_0-9]*)([\\s]*?)";

	// Drop Table REGEX
	private static final String regex9 = "(DROP)([\\s]*) {1}(TABLE)([\\s]*) {1}([\\w\\d_0-9]*)([\\s]*?)";

	// Select From Table REGEX
	private static final String regex10 = "(SELECT)([\\s]*)([\\w\\d_0-9,?[\\s]?]*)([\\s]*)(FROM)([\\s]*)([\\w\\d_0-9]*)([\\s]*)((WHERE) {0,1}([\\s]*){0,1}([a-zA-Z_0-9\\s\\W\\s[\\-]?0-9]*)){0,1}";

	private Pattern pattern;
	private Matcher match;

	private ArrayList<LinkedHashMap<String, String>> list;
	private LinkedHashMap<String, String> map;

	public Command createVerify(String query) throws SQLException {

		pattern = Pattern.compile(regex1, Pattern.CASE_INSENSITIVE);
		match = pattern.matcher(query);

		if (match.find()) {
			if (match.group(5).equals("") || match.group(5).equals(null)) {
				throw new SQLException();

			} else {
				String temp = "";
				boolean flag = false;
				Pattern pattern = Pattern.compile("[a-zA-Z0-9_]*");
				for (int i = query.length() - 1; i >= 0; i--) {
					if (flag) {
						flag = false;
						break;
					}
					if (query.charAt(i) == ' ') {
						continue;
					} else {
						for (int j = i; j >= 0; j--) {
							if (query.charAt(j) == ' ') {
								flag = true;
								break;
							}
							temp = query.charAt(j) + temp;
						}
					}
				}
				String tempName = temp;
				Matcher matcher = pattern.matcher(tempName);

				if (!matcher.matches()) {
					// System.out.println("string '" + tempName + "' contains special character");
					throw new SQLException();

				} else {
					// System.out.println("string '" + tempName + "' doesn't contains special
					// character");
					System.out.println("Name Of DataBase:" + match.group(5)); // NAME

					return new CreateDB();
				}
			}

		} else {

			pattern = Pattern.compile(regex3, Pattern.CASE_INSENSITIVE);
			match = pattern.matcher(query);
			if (match.find()) {
				// System.out.println(match.group(5)); //TABLE NAME
				// System.out.println(match.group(8)); //COLOUMNS AND THEIR TYPES

				return new CreateTable();

			} else {
				pattern = Pattern.compile(regex8, Pattern.CASE_INSENSITIVE);
				match = pattern.matcher(query);
				if (match.find()) {
					// System.out.println(match.group(1)); // DROP
					// System.out.println(match.group(3)); // DATABASE
					// System.out.println(match.group(5)); // NAME
					if (match.group(5).equals("") || match.group(5).equals(null)) {
						throw new SQLException();
					} else {
						list = new ArrayList<LinkedHashMap<String, String>>();
						map = new LinkedHashMap();
						String g = match.group(5);
						System.out.println("Drop:" + g);
						map.put("DatabaseName", match.group(5));
						list.add(map);

						return new DropDB();
					}

				} else {
					pattern = Pattern.compile(regex9, Pattern.CASE_INSENSITIVE);
					match = pattern.matcher(query);
					if (match.find()) {

						if (match.group(5).equals("") || match.group(5).equals(null)) {
							throw new SQLException();
						} else {

							// System.out.println(match.group(1)); // DROP
							// System.out.println(match.group(3)); // Table
							// System.out.println(match.group(5)); // NAME

							return new DropTable();
						}
					} else {
						throw new SQLException();
					}
				}
			}
		}
	}

	public Command selectVerify(String query) throws SQLException {
		pattern = Pattern.compile(regex2, Pattern.CASE_INSENSITIVE);
		match = pattern.matcher(query);
		if (match.find()) {
			// System.out.println(match.group(1)); //USE
			// System.out.println(match.group(3)); //NAME

			return null;

		} else {
			pattern = Pattern.compile(regex5, Pattern.CASE_INSENSITIVE);
			match = pattern.matcher(query);
			if (match.find()) {
					// System.out.println(match.group(3)); //ELEMENT
					System.out.println("tableName   :" + match.group(7)); // TABLE NAME
					// System.out.println("condition :" + match.group(11)); // CONDITION
					return new Select();
				
			} else {
				pattern = Pattern.compile(regex10, Pattern.CASE_INSENSITIVE);
				match = pattern.matcher(query);
				if (match.find()) {
					// System.out.println(match.group(1)); // Select
					// System.out.println(match.group(4)); // COLOUMNS
					// System.out.println(match.group(7)); // TABLE NAME

					return new Select();

				} else {
					throw new SQLException();
				}
			}
		}
	}

	public Command updateVerify(String query) throws SQLException {
		pattern = Pattern.compile(regex4, Pattern.CASE_INSENSITIVE);
		match = pattern.matcher(query);
		if (match.find()) {
			String f = match.group(8);
			System.out.println(f);
			//CREATE TABLE table_name3(column_name1 varchar, column_name2 int, column_name3 varchar)
			//INSERT INTO table_name3 VALUES ('value1', 3,'value3')
			if(!(f.matches("[\\s]*"))) {  //coloumn exist
				String bracket = match.group(7);
				String bracket1 = match.group(9);
				if ((bracket == null) || (bracket1 == null)) { //bracket dont exist
					throw new SQLException();
				} else {
					System.out.println("TableName   :" + match.group(5)); //TABLE NAME
					System.out.println("Bracket   :" + match.group(7));
					System.out.println("coloumns   :" + match.group(8)); //COLOUMNS
					System.out.println("values   :" + match.group(14)); //VALUES
					

					return new Insert();
				}
			} else {
				String bracket = match.group(7);
				String bracket1 = match.group(9);
				if (!(bracket == null) || !(bracket1 == null)) {
					throw new SQLException();
				} else {
					System.out.println("TableName   :" + match.group(5)); //TABLE NAME
					System.out.println("Bracket   :" + match.group(7));
					System.out.println("coloumns   :" + match.group(8)); //COLOUMNS
					System.out.println("values   :" + match.group(14)); //VALUES

					return new Insert();
				}
			}
			
		
		} else {
			pattern = Pattern.compile(regex6, Pattern.CASE_INSENSITIVE);
			match = pattern.matcher(query);
			System.out.println(match.toString());
			
			//DELETE From table_name3  WHERE column_name3='value3'
			if (match.find()) {
				 if (query.trim().split(" ")[0].equalsIgnoreCase("DELETE")) {
					 System.out.println(match.group(0));
					 System.out.println("TableName   :" + match.group(5)); //TABLE NAME
					 System.out.println("Condition   :" + match.group(7)); // CONDITION
					return new Delete();
				 } else {
					 throw new SQLException();
				 }


			} else {
				pattern = Pattern.compile(regex7, Pattern.CASE_INSENSITIVE);
				match = pattern.matcher(query);
				if (match.find()) {
					// System.out.println(match.group(3)); //TABLE NAME
					// System.out.println(match.group(7)); //UPDATE
					// System.out.println(match.group(9)); //CONDITION
					if (query.trim().split(" ")[0].equalsIgnoreCase("UPDATE")) {
						return new Update();
					}else {
						throw new SQLException();
					}
				

				} else {
					throw new SQLException();
				}
			}
		}
	}

	/*
	 * public boolean IsSyntaxValid (String query) {
	 * 
	 * pattern = Pattern.compile(regex1, Pattern.CASE_INSENSITIVE); match =
	 * pattern.matcher(query); if (match.find()) {
	 * //System.out.println(match.group(1)); //CREATE
	 * //System.out.println(match.group(3)); //DATABASE
	 * //System.out.println(match.group(5)); //NAME
	 * 
	 * return true;
	 * 
	 * 
	 * } else { pattern = Pattern.compile(regex2, Pattern.CASE_INSENSITIVE); match =
	 * pattern.matcher(query); if (match.find()) {
	 * //System.out.println(match.group(1)); //USE
	 * //System.out.println(match.group(3)); //NAME
	 * 
	 * return true;
	 * 
	 * } else { pattern = Pattern.compile(regex3, Pattern.CASE_INSENSITIVE); match =
	 * pattern.matcher(query); if (match.find()) {
	 * //System.out.println(match.group(5)); //TABLE NAME
	 * //System.out.println(match.group(8)); //COLOUMNS AND THEIR TYPES
	 * 
	 * return true;
	 * 
	 * } else { pattern = Pattern.compile(regex4, Pattern.CASE_INSENSITIVE); match =
	 * pattern.matcher(query); if (match.find()) {
	 * //System.out.println(match.group(5)); //TABLE NAME
	 * //System.out.println(match.group(8)); //COLOUMNS
	 * //System.out.println(match.group(14)); //VALUES
	 * 
	 * return true;
	 * 
	 * } else { pattern = Pattern.compile(regex5, Pattern.CASE_INSENSITIVE); match =
	 * pattern.matcher(query); if (match.find()) {
	 * //System.out.println(match.group(3)); //ELEMENT
	 * //System.out.println(match.group(7)); //TABLE NAME
	 * //System.out.println(match.group(11)); // CONDITION
	 * 
	 * return true;
	 * 
	 * } else { pattern = Pattern.compile(regex6, Pattern.CASE_INSENSITIVE); match =
	 * pattern.matcher(query); if (match.find()) {
	 * //System.out.println(match.group(5)); //TABLE NAME
	 * //System.out.println(match.group(7)); // CONDITION
	 * 
	 * return true;
	 * 
	 * } else { pattern = Pattern.compile(regex7, Pattern.CASE_INSENSITIVE); match =
	 * pattern.matcher(query); if (match.find()){
	 * //System.out.println(match.group(3)); //TABLE NAME
	 * //System.out.println(match.group(7)); //UPDATE
	 * //System.out.println(match.group(9)); //CONDITION
	 * 
	 * return true;
	 * 
	 * } else { pattern = Pattern.compile(regex8, Pattern.CASE_INSENSITIVE); match =
	 * pattern.matcher(query); if (match.find()) {
	 * //System.out.println(match.group(1)); // DROP
	 * //System.out.println(match.group(3)); // DATABASE
	 * //System.out.println(match.group(5)); // NAME list = new
	 * ArrayList<LinkedHashMap<String, String>>(); map = new LinkedHashMap();
	 * map.put("DatabaseName", match.group(5)); list.add(map);
	 * 
	 * return true;
	 * 
	 * } else { pattern = Pattern.compile(regex9, Pattern.CASE_INSENSITIVE); match =
	 * pattern.matcher(query); if (match.find()) {
	 * //System.out.println(match.group(1)); // DROP
	 * //System.out.println(match.group(3)); // Table
	 * //System.out.println(match.group(5)); // NAME
	 * 
	 * return true;
	 * 
	 * } else { pattern = Pattern.compile(regex10, Pattern.CASE_INSENSITIVE); match
	 * = pattern.matcher(query); if (match.find()) {
	 * //System.out.println(match.group(1)); // Select
	 * //System.out.println(match.group(4)); // COLOUMNS
	 * //System.out.println(match.group(7)); // TABLE NAME } else { return false; }
	 * }
	 * 
	 * } } } } } } } }
	 * 
	 * return false;
	 * 
	 * }
	 */

	public boolean isCommandValid(String query) {

		return true;

	}

}
