package eg.edu.alexu.csd.oop.db.cs61;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import eg.edu.alexu.csd.oop.db.Command;

public class Delete implements Command<Integer> {

	private static final String regex = "(DELETE)([\\s]*) {1}(FROM)([\\s]*) {1}([\\w\\d_0-9]*)([\\s]*)((WHERE){0,1}([\\s]*?)([a-zA-Z_0-9\\s\\W\\s[\\-]?0-9]*))";
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
		/*if (query.replaceAll(" ", "").length() != match.group(0).replaceAll(" ", "").length()) {
			throw new SQLException();
		}*/
		String tableName = match.group(5).trim().toLowerCase();
		String condition = match.group(7);
		System.out.println("HEREEEEEEEEEe :" + condition);

		table = Cache_Pool.getInstance().getTable(tableName);

		if (condition == null || condition.equals("")) { // there isn't any condition i.e. clear the table

			updateCounter = table.getData().size();
			table.getData().clear();

		} else {

			String[] tempCond = condition.trim().split("\\s*(?i)where\\s*");

			if (tempCond[1].contains("=")) {

				String[] temp = tempCond[1].split("=");
				
				temp[0] = temp[0].trim().toLowerCase();
				temp[1] = temp[1].trim().toLowerCase();

				if (!table.getSchema().containsKey(temp[0].trim().toLowerCase())) {
					Cache_Pool.getInstance().returnTable(table);
					return 0;
				}
				for (int i = 0; i < table.getData().size(); i++) {
					
					if (!CheckCompare(table.getSchema(), temp[0].toLowerCase().trim(), temp[1].toLowerCase().trim())) {
						System.out.println("||||||||||||||");
						Cache_Pool.getInstance().returnTable(table);
						return 0; 
					}
					LinkedHashMap<String, String> tempMap = table.getData().get(i);
					
					
					if (tempMap.get(temp[0].trim()).equalsIgnoreCase(temp[1].trim())) {

						table.getData().remove(i);
						i--;
						updateCounter++;

					}

				}

			} else if (tempCond[1].contains("<")) {

				String[] temp = tempCond[1].split("<");
				
				temp[0] = temp[0].trim().toLowerCase();
				temp[1] = temp[1].trim().toLowerCase();
				
				if (!table.getSchema().containsKey(temp[0].trim().toLowerCase())) {
					Cache_Pool.getInstance().returnTable(table);
					return 0;
				}
				for (int i = 0; i < table.getData().size(); i++) {

					LinkedHashMap<String, String> tempMap = table.getData().get(i);
					
					if (!CheckCompare(table.getSchema(), temp[0].toLowerCase().trim(), temp[1].toLowerCase().trim())) {
						Cache_Pool.getInstance().returnTable(table);
						throw new SQLException();
					} 
					
					if (tempMap.get(temp[0]).trim().matches("[[\\-]?0-9]+") && temp[1].trim().matches("[[\\-]?0-9]+")) {
						int x = Integer.parseInt(tempMap.get(temp[0].trim()));
						int y = Integer.parseInt(temp[1].trim());
						if (x < y) {
							table.getData().remove(i);
							i--;
							updateCounter++;
						}
					}
						else if (tempMap.get(temp[0].trim()).compareToIgnoreCase(temp[1].trim()) < 0) {

						table.getData().remove(i);
						i--;
						updateCounter++;

					}

				}

			} else if (tempCond[1].contains(">")) {

				String[] temp = tempCond[1].split(">");
				
				temp[0] = temp[0].trim().toLowerCase();
				temp[1] = temp[1].trim().toLowerCase();
				
				if (!table.getSchema().containsKey(temp[0].trim().toLowerCase())) {
					Cache_Pool.getInstance().returnTable(table);
					return 0;
				}
				for (int i = 0; i < table.getData().size(); i++) {

					LinkedHashMap<String, String> tempMap = table.getData().get(i);
					
					if (!CheckCompare(table.getSchema(), temp[0].toLowerCase().trim(), temp[1].toLowerCase().trim())) {
						Cache_Pool.getInstance().returnTable(table);
						throw new SQLException();
					}
					
					if (tempMap.get(temp[0]).trim().matches("[[\\-]?0-9]+") && temp[1].trim().matches("[[\\-]?0-9]+")) {
						int x = Integer.parseInt(tempMap.get(temp[0]));
						int y = Integer.parseInt(temp[1]);
						if (x > y) {
							table.getData().remove(i);
							i--;
							updateCounter++;
						}
					}
					
					else if (tempMap.get(temp[0].trim()).compareToIgnoreCase(temp[1].trim()) > 0) {

						table.getData().remove(i);
						i--;
						updateCounter++;

					}

				}

			} else {

				try {
					Cache_Pool.getInstance().returnTable(table);
					throw new SQLException();
				} catch (SQLException e) {
					
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
				if (!NewMap.get(key).trim().matches("[[\\-]?0-9]+")) {
					f++;
				} 
			}  else if (SCmap.get(key.trim().toLowerCase()).equals("varchar")) {
				if (!(NewMap.get(key).trim().charAt(0) == '\'') ||
						!(NewMap.get(key).trim().charAt(NewMap.get(key).trim().length() - 1) == '\'')
						|| NewMap.get(key).trim().length() == 1) {
					System.out.println("no sigle");
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
			if (!NewVal.matches("[[\\-]?0-9]+")) {
				//System.out.println("hhhhhhh");
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
