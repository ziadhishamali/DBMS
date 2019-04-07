package eg.edu.alexu.csd.oop.db.cs61;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eg.edu.alexu.csd.oop.db.Command;

public class Select implements Command<Object[][]> {
	private String dataBase;
	private String path;
	private Table table;
	private static final String regex5 = "(SELECT)([\\s]*)([\\*] {1})([\\s]*)(FROM)([\\s]*)([\\w\\d_0-9]*)([\\s]*)((WHERE) {0,1}([\\s]*){0,1}([a-zA-Z_0-9\\s\\W\\s[\\-]?0-9]*)){0,1}";
	private static final String regex6 = "(SELECT)([\\s]*)([\\*] {1})([\\s]*)(FROM)([\\s]*)([\\w\\d_0-9]*)([\\s]*)(WHERE) {0,1}([\\s]*){0,1}([a-zA-Z_0-9\\s\\W\\s[\\-]?0-9]*){0,1}";
	private static final String regex10 = "(SELECT)([\\s]*)([\\w\\d_0-9,?[\\s]?]*)([\\s]*)(FROM)([\\s]*)([\\w\\d_0-9]*)([\\s]*)((WHERE) {0,1}([\\s]*){0,1}([a-zA-Z_0-9\\s\\W\\s[\\-]?0-9]*)){0,1}";

	@Override
	public Object[][] execute(String query) throws SQLException {
		String t = query.toLowerCase();
		if (query.contains("*") && !t.contains("where")) {
			Pattern pattern = Pattern.compile(regex5, Pattern.CASE_INSENSITIVE);
			Matcher match = pattern.matcher(query);
			match.find();

			if (query.replaceAll(" ", "").length() != match.group(0).replaceAll(" ", "").length()) {
				throw new SQLException();
			}

			String tableName = match.group(7).trim().toLowerCase();
			table = Cache_Pool.getInstance().getTable(tableName);
			Object[][] temp = new Object[table.getData().size()][table.getSchema().size()];
			for (int i = 0; i < table.getData().size(); i++) {
				LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
				map = table.getData().get(i);
				int j = 0;
				for (String key : map.keySet()) {
					if (CheckType(table.getSchema(), key).equals("int")) {
						temp[i][j] = Integer.parseInt(map.get(key));
					} else {
						temp[i][j] = map.get(key);
					} 
					j++;
				}
			}

			ArrayList<String> cols = new ArrayList<>();
			for (String key : table.getSchema().keySet()) {
				cols.add(key);
			}
			Proxy.getInstance().setColNames(cols);
			
			Cache_Pool.getInstance().returnTable(table);

			return temp;
		} else if (query.contains("*")) {
			Pattern pattern = Pattern.compile(regex6, Pattern.CASE_INSENSITIVE);
			Matcher match = pattern.matcher(query);
			match.find();
			if (query.replaceAll(" ", "").length() != match.group(0).replaceAll(" ", "").length()) {
				throw new SQLException();
			}
			String tableName = match.group(7).trim();
			String condition = match.group(11).trim();

			table = Cache_Pool.getInstance().getTable(tableName);
			if (condition.contains("<") && !condition.contains(">") && !condition.contains("=")) {
				String[] tempCond = condition.split("<");

				tempCond[0] = tempCond[0].toLowerCase().trim();
				tempCond[1] = tempCond[1].toLowerCase().trim();
				ArrayList<LinkedHashMap<String, String>> temp = new ArrayList<>();

				for (int i = 0; i < table.getData().size(); i++) {

					LinkedHashMap<String, String> tempMap = table.getData().get(i);
					String s = tempMap.get(tempCond[0]);
					String s1 = tempCond[1];
					if (s.matches("[[\\-]?0-9]+") && s1.matches("[[\\-]?0-9]+")) {
						int x = Integer.parseInt(s);
						int y = Integer.parseInt(s1);
						if (x < y) {
							temp.add(tempMap);
						}
					} else if (s.compareToIgnoreCase(s1) < 0) {
						temp.add(tempMap);
					}
				}
				Object[][] tempans = new Object[temp.size()][table.getSchema().size()];
				for (int i = 0; i < temp.size(); i++) {
					LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
					map = temp.get(i);
					int j = 0;
					for (String key : map.keySet()) {
						System.out.println((CheckType(table.getSchema(), key)));
						if (CheckType(table.getSchema(), key).equals("int")) {
							tempans[i][j] = Integer.parseInt(map.get(key));
						} else {
							tempans[i][j] = map.get(key);
						}
						j++;
					}
				}

				ArrayList<String> cols = new ArrayList<>();
				for (String key : table.getSchema().keySet()) {
					cols.add(key);
				}
				Proxy.getInstance().setColNames(cols);
				
				Cache_Pool.getInstance().returnTable(table);

				return tempans;
			} else if (condition.contains(">") && !condition.contains("=") && !condition.contains("<")) {

				String[] tempCond = condition.split(">");

				tempCond[0] = tempCond[0].toLowerCase().trim();
				tempCond[1] = tempCond[1].toLowerCase().trim();
				ArrayList<LinkedHashMap<String, String>> temp = new ArrayList<>();
				for (int i = 0; i < table.getData().size(); i++) {

					LinkedHashMap<String, String> tempMap = table.getData().get(i);
					String s = tempMap.get(tempCond[0]);
					String s1 = tempCond[1];
					if (s.matches("[[\\-]?0-9]+") && s1.matches("[[\\-]?0-9]+")) {
						int x = Integer.parseInt(s);
						int y = Integer.parseInt(s1);
						if (x > y) {
							temp.add(tempMap);
						}
					} else {
						if (s.compareToIgnoreCase(s1) > 0) {
							temp.add(tempMap);
						}
					}
				}
				Object[][] tempans = new Object[temp.size()][table.getSchema().size()];
				for (int i = 0; i < temp.size(); i++) {
					LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
					map = temp.get(i);
					int j = 0;
					for (String key : map.keySet()) {
						System.out.println((CheckType(table.getSchema(), key)));
						if (CheckType(table.getSchema(), key).equals("int")) {
							tempans[i][j] = Integer.parseInt(map.get(key));
						} else {
							tempans[i][j] = map.get(key);
						}

						j++;
					}
				}

				ArrayList<String> cols = new ArrayList<>();
				for (String key : table.getSchema().keySet()) {
					cols.add(key);
				}
				Proxy.getInstance().setColNames(cols);
				
				Cache_Pool.getInstance().returnTable(table);

				return tempans;
			} else if (condition.contains("=") && !condition.contains(">") && !condition.contains("<")) {

				String[] tempCond = condition.split("=");

				tempCond[0] = tempCond[0].toLowerCase().trim();
				tempCond[1] = tempCond[1].toLowerCase().trim();
				ArrayList<LinkedHashMap<String, String>> temp = new ArrayList<>();
				for (int i = 0; i < table.getData().size(); i++) {

					String s = "";
					String s1 = "";
					LinkedHashMap<String, String> tempMap = table.getData().get(i);
					s = tempMap.get(tempCond[0]);
					s1 = tempCond[1];
					if (s.matches("[[\\-]?0-9]+") && s1.matches("[[\\-]?0-9]+")) {
						int x = Integer.parseInt(s);
						int y = Integer.parseInt(s1);
						if (x == y) {
							temp.add(tempMap);
						}
					} else {
						if (s.compareToIgnoreCase(s1) == 0) {
							temp.add(tempMap);
						}
					}
				}
				Object[][] tempans = new Object[temp.size()][table.getSchema().size()];
				for (int i = 0; i < temp.size(); i++) {
					LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
					map = temp.get(i);
					int j = 0;
					for (String key : map.keySet()) {
						if (CheckType(table.getSchema(), key).equals("int")) {
							tempans[i][j] = Integer.parseInt(map.get(key));
						} else {
							tempans[i][j] = map.get(key);
						}
						j++;
					}
				}

				ArrayList<String> cols = new ArrayList<>();
				for (String key : table.getSchema().keySet()) {
					cols.add(key);
				}
				Proxy.getInstance().setColNames(cols);
				
				Cache_Pool.getInstance().returnTable(table);

				return tempans;
			}

		} else if (!query.contains("*") && !t.contains("where")) {

			Pattern pattern = Pattern.compile(regex10, Pattern.CASE_INSENSITIVE);
			Matcher match = pattern.matcher(query);
			match.find();
			if (query.replaceAll(" ", "").length() != match.group(0).replaceAll(" ", "").length()) {
				throw new SQLException();
			}
			String tableName = match.group(7);

			table = Cache_Pool.getInstance().getTable(tableName);

			String[] partions1 = match.group(3).split(",");

			System.out.println(partions1);

			Object[][] temp = new Object[table.getData().size()][partions1.length];

			for (int i = 0; i < table.getData().size(); i++) {
				LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
				map = table.getData().get(i);
				int j = 0;

				for (int k = 0; k < partions1.length; k++) {

					if (table.getSchema().containsKey(partions1[k].trim().toLowerCase())) {
						if (CheckType(table.getSchema(), partions1[k].trim().toLowerCase()).equals("int")) {
							temp[i][k] = Integer.parseInt(map.get(partions1[k].trim().toLowerCase()));
						} else {
							temp[i][k] = map.get(partions1[k].trim().toLowerCase());
						}
					} else {
						Cache_Pool.getInstance().returnTable(table);
						throw new SQLException();
					}

				}

			}

			ArrayList<String> cols = new ArrayList<>();
			for (int i = 0; i < partions1.length; i++) {
				cols.add(partions1[i].trim().toLowerCase());
			}
			Proxy.getInstance().setColNames(cols);
			
			Cache_Pool.getInstance().returnTable(table);

			return temp;

		} else if (!query.contains("*")) {

			Pattern pattern = Pattern.compile(regex10, Pattern.CASE_INSENSITIVE);
			Matcher match = pattern.matcher(query);
			match.find();
			if (query.replaceAll(" ", "").length() != match.group(0).replaceAll(" ", "").length()) {
				throw new SQLException();
			}
			String tableName = match.group(7);

			table = Cache_Pool.getInstance().getTable(tableName);

			String[] partions1 = match.group(3).split(",");
			String condition = match.group(9).trim().split("\\s*(?i)where\\s*")[1].trim();
			System.out.println(condition);
			String[] partions2 = {};

			System.out.println(partions1);

			ArrayList<LinkedHashMap<String, String>> temp = new ArrayList<>();

			if (condition.contains("=") && !condition.contains(">") && !condition.contains("<")) {

				partions2 = condition.split("=");

				for (int i = 0; i < table.getData().size(); i++) {
					LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
					map = table.getData().get(i);
					int j = 0;

					if (map.containsKey(partions2[0].trim().toLowerCase())) {

						if (map.get(partions2[0].trim().toLowerCase()).equals(partions2[1].trim().toLowerCase())) {

							LinkedHashMap<String, String> tempMap = new LinkedHashMap<>();

							for (int k = 0; k < partions1.length; k++) {

								if (table.getSchema().containsKey(partions1[k].trim().toLowerCase())) {

									tempMap.put(partions1[k].trim().toLowerCase(),
											map.get(partions1[k].trim().toLowerCase()));

								} else {
									Cache_Pool.getInstance().returnTable(table);
									throw new SQLException();
								}

							}

							temp.add(tempMap);

						}

					} else {
						Cache_Pool.getInstance().returnTable(table);
						throw new SQLException();
					}

				}

				Object[][] res = new Object[temp.size()][partions1.length];

				for (int i = 0; i < temp.size(); i++) {

					LinkedHashMap<String, String> rMap = new LinkedHashMap<>();
					rMap = temp.get(i);
					int j = 0;
					for (String key : rMap.keySet()) {

						if (CheckType(table.getSchema(), key).equals("int")) {
							res[i][j] = Integer.parseInt(rMap.get(key));
						} else {
							res[i][j] = rMap.get(key);
						}
						j++;

					}

				}

				ArrayList<String> cols = new ArrayList<>();
				for (int i = 0; i < partions1.length; i++) {
					cols.add(partions1[i].trim().toLowerCase());
				}
				Proxy.getInstance().setColNames(cols);
				
				Cache_Pool.getInstance().returnTable(table);

				return res;

			} else if (condition.contains("<") && !condition.contains(">") && !condition.contains("=")) {

				partions2 = condition.split("<");

				for (int i = 0; i < table.getData().size(); i++) {
					LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
					map = table.getData().get(i);
					int j = 0;

					if (map.containsKey(partions2[0].trim().toLowerCase())) {

						if (map.get(partions2[0].trim().toLowerCase()).matches("[[\\-]?0-9]+")
								&& map.get(partions2[0].trim().toLowerCase()).length() > 2) {
							if (Integer.parseInt(map.get(partions2[0].trim().toLowerCase())) < Integer
									.parseInt((partions2[1].trim().toLowerCase()))) {

								LinkedHashMap<String, String> tempMap = new LinkedHashMap<>();

								for (int k = 0; k < partions1.length; k++) {

									if (table.getSchema().containsKey(partions1[k].trim().toLowerCase())) {

										tempMap.put(partions1[k].trim().toLowerCase(),
												map.get(partions1[k].trim().toLowerCase()));

									} else {
										Cache_Pool.getInstance().returnTable(table);
										throw new SQLException();
									}

								}

								temp.add(tempMap);

							}
						} else {
							if (map.get(partions2[0].trim().toLowerCase())
									.compareToIgnoreCase(partions2[1].trim().toLowerCase()) < 0) {

								LinkedHashMap<String, String> tempMap = new LinkedHashMap<>();

								for (int k = 0; k < partions1.length; k++) {

									if (table.getSchema().containsKey(partions1[k].trim().toLowerCase())) {

										tempMap.put(partions1[k].trim().toLowerCase(),
												map.get(partions1[k].trim().toLowerCase()));

									} else {
										Cache_Pool.getInstance().returnTable(table);
										throw new SQLException();
									}

								}

								temp.add(tempMap);

							}
						}

					} else {
						Cache_Pool.getInstance().returnTable(table);
						throw new SQLException();
					}

				}

				Object[][] res = new Object[temp.size()][partions1.length];

				for (int i = 0; i < temp.size(); i++) {

					LinkedHashMap<String, String> rMap = new LinkedHashMap<>();
					rMap = temp.get(i);
					int j = 0;
					for (String key : rMap.keySet()) {

						if (CheckType(table.getSchema(), key).equals("int")) {
							res[i][j] = Integer.parseInt(rMap.get(key));
						} else {
							res[i][j] = rMap.get(key);
						}
						j++;

					}

				}

				ArrayList<String> cols = new ArrayList<>();
				for (int i = 0; i < partions1.length; i++) {
					cols.add(partions1[i].trim().toLowerCase());
				}
				Proxy.getInstance().setColNames(cols);
				
				Cache_Pool.getInstance().returnTable(table);

				return res;

			} else if (condition.contains(">") && !condition.contains("<") && !condition.contains("=")) {

				partions2 = condition.split(">");

				for (int i = 0; i < table.getData().size(); i++) {
					LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
					map = table.getData().get(i);
					int j = 0;

					if (map.containsKey(partions2[0].trim().toLowerCase())) {

						if (map.get(partions2[0].trim().toLowerCase()).matches("[[\\-]?0-9]+")
								&& map.get(partions2[0].trim().toLowerCase()).length() > 2) {
							if (Integer.parseInt(map.get(partions2[0].trim().toLowerCase())) > Integer
									.parseInt((partions2[1].trim().toLowerCase()))) {

								LinkedHashMap<String, String> tempMap = new LinkedHashMap<>();

								for (int k = 0; k < partions1.length; k++) {

									if (table.getSchema().containsKey(partions1[k].trim().toLowerCase())) {

										tempMap.put(partions1[k].trim().toLowerCase(),
												map.get(partions1[k].trim().toLowerCase()));

									} else {
										Cache_Pool.getInstance().returnTable(table);
										throw new SQLException();
									}

								}

								temp.add(tempMap);

							}
						} else {
							if (map.get(partions2[0].trim().toLowerCase())
									.compareToIgnoreCase(partions2[1].trim().toLowerCase()) > 0) {

								LinkedHashMap<String, String> tempMap = new LinkedHashMap<>();

								for (int k = 0; k < partions1.length; k++) {

									if (table.getSchema().containsKey(partions1[k].trim().toLowerCase())) {

										tempMap.put(partions1[k].trim().toLowerCase(),
												map.get(partions1[k].trim().toLowerCase()));

									} else {
										Cache_Pool.getInstance().returnTable(table);
										throw new SQLException();
									}

								}

								temp.add(tempMap);

							}
						}

					} else {
						Cache_Pool.getInstance().returnTable(table);
						throw new SQLException();
					}

				}

				Object[][] res = new Object[temp.size()][partions1.length];

				for (int i = 0; i < temp.size(); i++) {

					LinkedHashMap<String, String> rMap = new LinkedHashMap<>();
					rMap = temp.get(i);
					int j = 0;
					for (String key : rMap.keySet()) {

						if (CheckType(table.getSchema(), key).equals("int")) {
							res[i][j] = Integer.parseInt(rMap.get(key));
						} else {
							res[i][j] = rMap.get(key);
						}
						j++;

					}

				}

				ArrayList<String> cols = new ArrayList<>();
				for (int i = 0; i < partions1.length; i++) {
					cols.add(partions1[i].trim().toLowerCase());
				}
				Proxy.getInstance().setColNames(cols);
				
				Cache_Pool.getInstance().returnTable(table);

				return res;

			}

		}

		throw new SQLException();
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

	String CheckType(LinkedHashMap<String, String> SCmap, String temp) {
		if (SCmap.get(temp.trim()).trim().equals("int")) {
			return "int";
		} else if (SCmap.get(temp.trim()).trim().equalsIgnoreCase("varchar")) {
			return "string";
		}
		return null;
	}

	boolean CheckCompare(LinkedHashMap<String, String> SCmap, String ColName, String NewVal) {
		int f = 0;
		if (SCmap.get(ColName.trim()).trim().equals("int")) {
			if (!NewVal.matches("[[\\-]?0-9]+")) {
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
			return true;
		}
	}

}
