package eg.edu.alexu.csd.oop.db.cs61;

import java.sql.SQLException;
import java.util.ArrayList;

import eg.edu.alexu.csd.oop.db.Database;

public class Proxy {
	
	private Database database;
	private static Proxy proxy;
	private Cache_Pool pool;
	private Validation verify;
	private String command = "";
	private ArrayList<String> cols;
	private Object[][] res = {};
	private Proxy() {
		this.verify = new Validation();
		this.pool = Cache_Pool.getInstance();
	}
	
	public static Proxy getInstance() {
		
		if (proxy == null) {
			proxy = new Proxy();
			return proxy;
		} else {
			return proxy;
		}
		
	}
	
	public ArrayList<String> verifyQuery(String query) {
		
		query = query.toLowerCase();
		
		ArrayList<String> text = new ArrayList<>();
		
		if (query.contains("create") || query.contains("drop")) {
			try {
				boolean success = database.executeStructureQuery(query);
				if (success) {
					String s = "Action performed Successfully !!";
					text.add(s);
				} else {
					String s = "Action performed Unsuccessfully !!";
					text.add(s);
				}
			} catch (SQLException e) {
				String s = "Syntax Error !! please enter a valid command";
				text.add(s);
			}
		} else if (query.contains("update") || query.contains("insert") || query.contains("delete")) {
			try {
				int count = database.executeUpdateQuery(query);
				String s = count + " ROWS have been updated !!";
				text.add(s);
			} catch (Exception e) {
				String s = "Syntax Error !! please enter a valid command";
				text.add(s);
			}
		} else if (query.contains("select")) {
			try {
			   res = database.executeQuery(query);
				for (int i = 0; i < res.length; i++) {
					String s = "";
					for (int j = 0; j < res[i].length; j++) {
						s += res[i][j] ;
						if (j != res[i].length - 1) {
							s+="  ....  ";
						}
					}
					text.add(s);
				}
			} catch (SQLException e) {
				String s = "Syntax Error !! please enter a valid command";
				text.add(s);
			}
		} else {
			String s = "Syntax Error !! please enter a valid command";
			text.add(s);
		}
		
		return text;
		
	}
	
	public void setDataBase(Database database) {
		this.database = database;
	}
	
	public void setColNames(ArrayList<String> cols) {
		this.cols = cols;
	}
	
	public ArrayList<String> getColNames() {
		return this.cols;
	}
	
	public Object [][] getselect () {
		return this.res;
	}

}
