package eg.edu.alexu.csd.oop.db.cs61;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;

import eg.edu.alexu.csd.oop.db.Command;

public class Insert implements Command<Integer> {

	private static final String regex = "(INSERT)([\\s]*) {1}(INTO)([\\s]*) {1}([\\w\\d_0-9]*)([\\s]*?)([\\(]){0,1}([\\w\\d_0-9,?[\\s]?]*){0,1}([\\)]){0,1}([\\s]*){0,1}(VALUES)([\\s]*)([\\(])([[']?[\\-]?\\w\\d_0-9[']?,?[\\s]?]*)([\\)])";
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
		if (query.replaceAll(" ", "").length() != match.group(0).replaceAll(" ", "").length()) {
			throw new SQLException();
		}
		String tableName = match.group(5).trim().toLowerCase();

		table = new Table();

		table = Cache_Pool.getInstance().getTable(tableName);

		ArrayList<LinkedHashMap<String, String>> list = new ArrayList<>();
		LinkedHashMap<String, String> map = new LinkedHashMap();
		String[] partions1 = {};
		String[] partions2;
		if (match.group(8).trim().equals("") || match.group(8) == null) {
			partions2 = match.group(14).split(",");

			if (partions2.length != table.getSchema().size()) {
				Cache_Pool.getInstance().returnTable(table);
				throw new SQLException();
			}
			if (!CheckSchemaWithoutCol(table.getSchema(), partions2)) {
				Cache_Pool.getInstance().returnTable(table);
				throw new SQLException();
			} else {
				int i = 0;
				for (String key : table.getSchema().keySet()) {

					map.put(key, partions2[i].trim());
					i++;

				}

				System.out.println("the inserted row : " + map);
				System.out.println("the table before inserting : " + table.getData());

				table.getData().add(map);

				System.out.println("the table after inserting : " + table.getData());

				Cache_Pool.getInstance().returnTable(table);

				return 1;

			}

		} else {
			partions1 = match.group(8).split(",");
			partions2 = match.group(14).split(",");
		}

		ArrayList<String> temp1 = new ArrayList<String>();
		ArrayList<String> temp2 = new ArrayList<String>();

		for (int i = 0; i < partions1.length; i++) {
			if (table.getSchema().containsKey(partions1[i].trim().toLowerCase())) {
				temp1.add(partions1[i].trim());
				temp2.add(partions2[i].trim());
			} else {
				Cache_Pool.getInstance().returnTable(table);
				throw new SQLException();
			}

		}

		if (temp1.size() != temp2.size()) {
			try {
				Cache_Pool.getInstance().returnTable(table);
				throw new SQLException();
			} catch (SQLException e) {
				
			}
		}

		if (!CheckSchemaWithCol(table.getSchema(), partions1, partions2)) {
			Cache_Pool.getInstance().returnTable(table);
			throw new SQLException();
		}
		for (int i = 0; i < partions1.length; i++) {
			map.put(temp1.get(i).toLowerCase(), temp2.get(i));
		}
		System.out.println("the inserted row : " + map);
		System.out.println("the table before inserting : " + table.getData());

		table.getData().add(map);

		System.out.println("the table after inserting : " + table.getData());

		Cache_Pool.getInstance().returnTable(table);

		return 1;
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

	boolean CheckSchemaWithCol(LinkedHashMap<String, String> map, String[] col, String[] val) {
		System.out.println("ana hna ya abn el 7alal");
		int f = 0;
		for (int i = 0; i < col.length; i++) {
			if (map.get(col[i].trim().toLowerCase()).trim().equals("int")) {
				if (!val[i].trim().matches("([\\-]{0,1})([0-9]*)")) {
					f++;
				}
			} else if (map.get(col[i].trim().toLowerCase()).trim().equals("varchar")) {
				if (!(val[i].trim().charAt(0) == '\'') || !(val[i].trim().charAt(val[i].trim().length() - 1) == '\'')
						|| val[i].trim().length() == 1) {
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

	boolean CheckSchemaWithoutCol(LinkedHashMap<String, String> map, String[] val) {
		int f = 0;
		int i = 0;
		System.out.println("okkk");
		for (String key : map.keySet()) {
			String value = map.get(key);
			if (value.toLowerCase().trim().equals("int")) {
				if (!val[i].trim().matches("([\\-]{0,1})([0-9]*)")) {
					f++;
				}
			} else if (value.trim().toLowerCase().trim().equals("varchar")) {
				if (!(val[i].trim().charAt(0) == '\'') || !(val[i].trim().charAt(val[i].trim().length() - 1) == '\'')
						|| val[i].trim().length() == 1) {
					System.out.println("no sigle");
					f++;
				}
			}
			i++;
		}
		if (f > 0) {
			return false;
		} else {
			return true;
		}

	}

}
