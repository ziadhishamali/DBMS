package eg.edu.alexu.csd.oop.db.cs61;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;

import eg.edu.alexu.csd.oop.db.Command;

public class Update implements Command<Integer> {
	// (WHERE) {0,1}([\\s]*){0,1}([a-zA-Z_0-9\\s\\W\\s0-9]*){0,1}
	// ((WHERE)([\\s]?)([a-zA-Z_0-9\\s\\W\\s0-9]*)){0,1}
	private static final String regex = "(UPDATE)([\\s]*)([\\w\\d_0-9]*)([\\s]*)(SET)([\\s]*)([a-zA-Z_0-9[\\s]*?\\W[\\']?[\\-]?0-9[\\']?[\\s]*?[,]?]*)([\\s]*)((WHERE) {0,1}([\\s]*) {0,1}([a-zA-Z_0-9\\s\\W\\s0-9]*)){0,1}";
	private int updateCounter = 0;
	private Table table;
	private Read read = new Read();
	private String dataBase;
	private String path;

	@Override
	public Integer execute(String query) throws SQLException {

		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher match = pattern.matcher(query);
		match.find();
		/*
		 * if (query.replaceAll(" ", "").length() != match.group(0).replaceAll(" ",
		 * "").length()) { throw new SQLException(); }
		 */
		String tableName = match.group(3).trim().toLowerCase();
		String condition = "";

		table = Cache_Pool.getInstance().getTable(tableName);

		LinkedHashMap<String, String> map = new LinkedHashMap<>();

		String[] partions = match.group(7).split(",");
		// String ff = match.group(8);

		if (partions[partions.length - 1] == "") {
			try {
				Cache_Pool.getInstance().returnTable(table);
				throw new SQLException();
			} catch (Exception e) {

			}
		}

		String s = partions[partions.length - 1].toLowerCase();
		if (s.contains("WHERE".toLowerCase())) {
			String[] temp = partions[partions.length - 1].trim().split("\\s*(?i)where\\s*");
			partions[partions.length - 1] = temp[0];
			condition = temp[1];
		}

		for (int i = 0; i < partions.length; i++) {

			if (partions[i].contains("=")) {
				String[] group = partions[i].split("=");
				ArrayList<String> temp = new ArrayList<String>();
				for (int j = 0; j < group.length; j++) {
					temp.add(group[j].trim());
				}
				map.put(temp.get(0).toLowerCase(), temp.get(1));
			}

		}

		/**
		 * check the schema
		 */
		if (!CheckSchema(table.getSchema(), map)) {
			Cache_Pool.getInstance().returnTable(table);
			throw new SQLException();
		}

		if (condition.equals("")) {

			for (int i = 0; i < table.getData().size(); i++) {

				LinkedHashMap<String, String> tempMap = table.getData().get(i);

				for (String key : map.keySet()) {
					if (tempMap.containsKey(key)) {
						tempMap.put(key, map.get(key));
					} else {
						try {
							Cache_Pool.getInstance().returnTable(table);
							throw new SQLException();
						} catch (Exception e) {

						}
					}
				}

				updateCounter++;

			}

		} else {

			if (condition.contains("=")) {

				String[] tempCond = condition.split("=");

				tempCond[0] = tempCond[0].toLowerCase().trim();
				tempCond[1] = tempCond[1].toLowerCase().trim();

				for (int i = 0; i < table.getData().size(); i++) {

					LinkedHashMap<String, String> tempMap = table.getData().get(i);

					if (tempMap.containsKey(tempCond[0])) {
						/**
						 * check if comparing value type not exist
						 */
						if (!CheckCompare(table.getSchema(), tempCond[0], tempCond[1])) {
							Cache_Pool.getInstance().returnTable(table);
							throw new SQLException();
						}
						if (tempMap.get(tempCond[0]).equalsIgnoreCase(tempCond[1])) {
							for (String key : map.keySet()) {
								if (tempMap.containsKey(key)) {
									tempMap.put(key, map.get(key));
								} else {
									try {
										Cache_Pool.getInstance().returnTable(table);
										throw new SQLException();
									} catch (SQLException e) {
										
									}
								}
							}

							updateCounter++;

						}

					} else {

						try {
							Cache_Pool.getInstance().returnTable(table);
							throw new SQLException();
						} catch (SQLException e) {

						}

					}

				}

			} else if (condition.contains("<")) {

				String[] tempCond = condition.split("<");

				tempCond[0] = tempCond[0].toLowerCase().trim();
				tempCond[1] = tempCond[1].toLowerCase().trim();

				for (int i = 0; i < table.getData().size(); i++) {

					LinkedHashMap<String, String> tempMap = table.getData().get(i);

					if (tempMap.containsKey(tempCond[0])) {
						/**
						 * check if comparing value type not exist
						 */
						if (!CheckCompare(table.getSchema(), tempCond[0], tempCond[1])) {
							Cache_Pool.getInstance().returnTable(table);
							throw new SQLException();
						}
						if (tempMap.get(tempCond[0]).trim().matches("[[\\-]?0-9]+") && tempCond[1].trim().matches("[[\\-]?0-9]+")) {
							int x = Integer.parseInt(tempMap.get(tempCond[0]));
							int y = Integer.parseInt(tempCond[1]);
							if (x < y) {
								for (String key : map.keySet()) {
									if (tempMap.containsKey(key)) {
										tempMap.put(key, map.get(key));
									} else {
										try {
											Cache_Pool.getInstance().returnTable(table);
											throw new SQLException();
										} catch (SQLException e) {

										}
									}
								}
								updateCounter++;
							}
						} else if (tempMap.get(tempCond[0]).compareToIgnoreCase(tempCond[1]) < 0) {
							for (String key : map.keySet()) {
								if (tempMap.containsKey(key)) {
									tempMap.put(key, map.get(key));
								} else {
									try {
										Cache_Pool.getInstance().returnTable(table);
										throw new SQLException();
									} catch (SQLException e) {

									}
								}
							}

							updateCounter++; 

						}

					} else {

						try {
							Cache_Pool.getInstance().returnTable(table);
							throw new SQLException();
						} catch (SQLException e) {

						}

					}

				}

			} else if (condition.contains(">")) {

				String[] tempCond = condition.split(">");

				tempCond[0] = tempCond[0].toLowerCase().trim();
				tempCond[1] = tempCond[1].toLowerCase().trim();

				for (int i = 0; i < table.getData().size(); i++) {

					LinkedHashMap<String, String> tempMap = table.getData().get(i);

					if (tempMap.containsKey(tempCond[0])) {
						/**
						 * check if comparing value type not exist
						 */
						if (tempMap.get(tempCond[0]).trim().matches("[[\\-]?0-9]+") && tempCond[1].trim().matches("[[\\-]?0-9]+")) {
							int x = Integer.parseInt(tempMap.get(tempCond[0]));
							int y = Integer.parseInt(tempCond[1]);
							if (x > y) {
								for (String key : map.keySet()) {
									if (tempMap.containsKey(key)) {
										tempMap.put(key, map.get(key));
									} else {
										try {
											Cache_Pool.getInstance().returnTable(table);
											throw new SQLException();
										} catch (SQLException e) {

										}
									}
								}
								updateCounter++;
							}
						} else if (tempMap.get(tempCond[0]).compareToIgnoreCase(tempCond[1]) > 0) {
							for (String key : map.keySet()) {
								if (tempMap.containsKey(key)) {
									tempMap.put(key, map.get(key));
								} else {
									try {
										Cache_Pool.getInstance().returnTable(table);
										throw new SQLException();
									} catch (SQLException e) {

									}
								}
							}

							updateCounter++;

						}
					} else {

						try {
							Cache_Pool.getInstance().returnTable(table);
							throw new SQLException();
						} catch (SQLException e) {

						}

					}

				}

			}

		}

		Cache_Pool.getInstance().returnTable(table);

		return updateCounter;
	}

	@Override
	public void setDataBase(String dataBase) {
		// TODO Auto-generated method stub
		this.dataBase = dataBase;
	}

	@Override
	public void setPath(String path) {
		// TODO Auto-generated method stub
		this.path = path;
	}

	boolean CheckSchema(LinkedHashMap<String, String> SCmap, LinkedHashMap<String, String> NewMap) {
		int f = 0;
		for (String key : NewMap.keySet()) {
			if (SCmap.get(key.trim().toLowerCase()).trim().equals("int")) {
				if (!NewMap.get(key).trim().matches("([\\-]{0,1})([0-9]*)")) {
					f++;
				}
			}  else if (SCmap.get(key.trim().toLowerCase()).trim().equals("varchar")) {
				if (!(NewMap.get(key).trim().charAt(0) == '\'') || 
						!(NewMap.get(key).trim().charAt(NewMap.get(key).trim().trim().length() - 1) == '\'')
						|| NewMap.get(key).trim().trim().length() == 1) {
					f++;
				}
			}
		}
		if (f > 0) {
			return false;
		} else {
			System.out.println("ana hna ya abn el 7alal");
			return true;
		}

	}

	boolean CheckCompare(LinkedHashMap<String, String> SCmap, String ColName, String NewVal) {
		int f = 0;
		if (SCmap.get(ColName.trim()).trim().equals("int")) {
			if (!NewVal.matches("([\\-]{0,1})([0-9]*)")) {
				f++;
			}
		} else if (SCmap.get(ColName.trim()).trim().equals("varchar")) {
			if (!(NewVal.trim().charAt(0) == '\'') ||
					!(NewVal.trim().charAt(NewVal.trim().length() - 1) == '\'')
					|| NewVal.trim().length() == 1) {
				System.out.println("no sigle");
				f++;
			}
		}
		if (f > 0) {
			return false;
		} else {
			System.out.println("ana hna ya abn el 7alal from the conditional update");
			return true;
		}
	}

}
